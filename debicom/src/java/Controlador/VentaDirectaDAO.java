package Controlador;

import Modelo.TipoPago;
import Modelo.dto.FacturaPDFDTO;
import Modelo.dto.ItemVentaDirectaDTO;
import Modelo.dto.ProductoDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Persistencia transaccional de ventas directas de contado.
 * No crea solicitudes de crédito: la factura queda con id_solicitud = NULL.
 */
public class VentaDirectaDAO {
    private final Conexion conexion = new Conexion();
    private static final DateTimeFormatter FACTURA_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public List<ProductoDTO> listarProductosParaVenta(int idTienda, int idVendedor) {
        if (idTienda <= 0 || idVendedor <= 0) {
            throw new IllegalArgumentException("Tienda o vendedor no válidos.");
        }
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_unitario, p.stock, p.id_tienda, "
                + "u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM productos p "
                + "INNER JOIN tiendas t ON t.id_tienda = p.id_tienda "
                + "LEFT JOIN unidades u ON u.id_unidad = p.id_unidad "
                + "LEFT JOIN estados_producto ep ON ep.id_estado_producto = p.id_estado_producto "
                + "WHERE p.id_tienda = ? AND t.id_vendedor = ? AND p.stock > 0 "
                + "AND UPPER(COALESCE(ep.nombre_estado, '')) = 'ACTIVO' "
                + "ORDER BY p.nombre";
        List<ProductoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTienda);
            ps.setInt(2, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(rs.getInt("id_producto"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setDescripcion(rs.getString("descripcion"));
                    dto.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    dto.setStock(rs.getInt("stock"));
                    dto.setIdTienda(rs.getInt("id_tienda"));
                    dto.setUnidad(rs.getString("unidad"));
                    dto.setEstado(rs.getString("nombre_estado"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible cargar los productos para la venta directa.", e);
        }
        return result;
    }

    public List<TipoPago> listarTiposPago() {
        String sql = "SELECT id_tipo_pago, nombre_pago FROM tipo_pago "
                + "WHERE UPPER(TRIM(nombre_pago)) NOT LIKE '%PRESENCIAL%' ORDER BY nombre_pago";
        List<TipoPago> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TipoPago tipo = new TipoPago();
                tipo.setIdTipoPago(rs.getInt("id_tipo_pago"));
                tipo.setNombrePago(rs.getString("nombre_pago"));
                result.add(tipo);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar los métodos de pago.", e);
        }
        return result;
    }

    public int registrarVentaDirecta(int idVendedor, int idTienda, int idCliente, int idTipoPago,
                                     String observaciones, List<ItemVentaDirectaDTO> items) {
        if (idVendedor <= 0 || idTienda <= 0 || idCliente <= 0 || idTipoPago <= 0) {
            throw new IllegalArgumentException("Vendedor, tienda, comprador y método de pago son obligatorios.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un producto.");
        }

        LinkedHashMap<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (ItemVentaDirectaDTO item : items) {
            if (item == null || item.getIdProducto() <= 0 || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada producto debe tener una cantidad mayor que cero.");
            }
            cantidades.merge(item.getIdProducto(), item.getCantidad(), Math::addExact);
        }

        String sqlTienda = "SELECT 1 FROM tiendas WHERE id_tienda = ? AND id_vendedor = ?";
        String sqlCliente = "SELECT 1 FROM clientes WHERE id_cliente = ?";
        String sqlTipoPago = "SELECT id_tipo_pago FROM tipo_pago WHERE id_tipo_pago = ? "
                + "AND UPPER(TRIM(nombre_pago)) NOT LIKE '%PRESENCIAL%'";
        String sqlEstadoFactura = "SELECT id_estado_factura FROM estados_factura "
                + "WHERE UPPER(TRIM(nombre_estado)) IN ('PAGADA','PAGADO') LIMIT 1";
        String sqlSalida = "SELECT id_tipo_movimiento FROM tipos_movimiento "
                + "WHERE UPPER(TRIM(nombre_tipo)) = 'SALIDA' LIMIT 1";
        String sqlProducto = "SELECT p.id_producto, p.nombre, p.precio_unitario, p.stock "
                + "FROM productos p INNER JOIN estados_producto ep ON ep.id_estado_producto = p.id_estado_producto "
                + "WHERE p.id_producto = ? AND p.id_tienda = ? AND UPPER(TRIM(ep.nombre_estado)) = 'ACTIVO' FOR UPDATE";
        String sqlFactura = "INSERT INTO facturas "
                + "(numero_factura, id_solicitud, id_cliente, id_tienda, subtotal, impuestos, total, fecha_emision, id_estado_factura) "
                + "VALUES (?, NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
        String sqlDetalle = "INSERT INTO detalle_facturas "
                + "(id_factura, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        String sqlMovimiento = "INSERT INTO movimientos_inventario "
                + "(id_producto, id_tipo_movimiento, cantidad, motivo, fecha_movimiento) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String sqlPago = "INSERT INTO pagos "
                + "(id_factura, numero_referencia_pago, monto_pagado, fecha_pago, id_tipo_pago, observaciones) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!existe(conn, sqlTienda, idTienda, idVendedor)) {
                    throw new SecurityException("La tienda no pertenece al vendedor autenticado.");
                }
                if (!existe(conn, sqlCliente, idCliente)) {
                    throw new IllegalArgumentException("El comprador no existe.");
                }
                if (!existe(conn, sqlTipoPago, idTipoPago)) {
                    throw new IllegalArgumentException("El método de pago seleccionado no es válido.");
                }

                int idEstadoPagada = consultarId(conn, sqlEstadoFactura, "No existe el estado de factura PAGADA/PAGADO.");
                int idTipoSalida = consultarId(conn, sqlSalida, "No existe el tipo de movimiento SALIDA.");

                class ItemValido {
                    int idProducto;
                    String nombre;
                    int cantidad;
                    BigDecimal precio;
                    BigDecimal subtotal;
                    int stock;

                    ItemValido(int idProducto, String nombre, int cantidad, BigDecimal precio, BigDecimal subtotal, int stock) {
                        this.idProducto = idProducto;
                        this.nombre = nombre;
                        this.cantidad = cantidad;
                        this.precio = precio;
                        this.subtotal = subtotal;
                        this.stock = stock;
                    }
                }

                List<ItemValido> validos = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

                for (var entry : cantidades.entrySet()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlProducto)) {
                        ps.setInt(1, entry.getKey());
                        ps.setInt(2, idTienda);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new IllegalArgumentException("El producto #" + entry.getKey() + " no está disponible en la tienda seleccionada.");
                            }
                            int stock = rs.getInt("stock");
                            int cantidad = entry.getValue();
                            if (cantidad > stock) {
                                throw new IllegalArgumentException("Stock insuficiente para el producto #" + entry.getKey() + ". Disponible: " + stock + ".");
                            }
                            BigDecimal precio = rs.getBigDecimal("precio_unitario").setScale(2, RoundingMode.HALF_UP);
                            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
                            validos.add(new ItemValido(entry.getKey(), rs.getString("nombre"), cantidad, precio, subtotal, stock));
                            total = total.add(subtotal).setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                }

                if (total.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El total de la venta debe ser mayor que cero.");
                }

                String numeroFactura = "VD-" + LocalDateTime.now().format(FACTURA_FMT) + "-"
                        + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                int idFactura;
                try (PreparedStatement ps = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, numeroFactura);
                    ps.setInt(2, idCliente);
                    ps.setInt(3, idTienda);
                    ps.setBigDecimal(4, total);
                    ps.setBigDecimal(5, BigDecimal.ZERO.setScale(2));
                    ps.setBigDecimal(6, total);
                    ps.setInt(7, idEstadoPagada);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se generó el ID de la factura.");
                        }
                        idFactura = rs.getInt(1);
                    }
                }

                for (ItemValido item : validos) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
                        ps.setInt(1, idFactura);
                        ps.setInt(2, item.idProducto);
                        ps.setInt(3, item.cantidad);
                        ps.setBigDecimal(4, item.precio);
                        ps.setBigDecimal(5, item.subtotal);
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("No se pudo guardar el detalle de la factura.");
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                        ps.setInt(1, item.cantidad);
                        ps.setInt(2, item.idProducto);
                        ps.setInt(3, item.cantidad);
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("No se pudo actualizar el inventario del producto #" + item.idProducto + ".");
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement(sqlMovimiento)) {
                        ps.setInt(1, item.idProducto);
                        ps.setInt(2, idTipoSalida);
                        ps.setInt(3, item.cantidad);
                        ps.setString(4, "Venta directa " + numeroFactura);
                        ps.executeUpdate();
                    }
                }

                String referencia = "VD-PAGO-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
                try (PreparedStatement ps = conn.prepareStatement(sqlPago)) {
                    ps.setInt(1, idFactura);
                    ps.setString(2, referencia);
                    ps.setBigDecimal(3, total);
                    ps.setInt(4, idTipoPago);
                    if (observaciones == null || observaciones.isBlank()) {
                        ps.setNull(5, java.sql.Types.VARCHAR);
                    } else {
                        ps.setString(5, observaciones.trim());
                    }
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("No se pudo registrar el pago de contado.");
                    }
                }

                conn.commit();
                return idFactura;
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollback) {
                    e.addSuppressed(rollback);
                }
                if (e instanceof IllegalArgumentException iae) throw iae;
                if (e instanceof SecurityException se) throw se;
                throw new IllegalStateException("No fue posible registrar la venta directa.", e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible iniciar la transacción de la venta directa.", e);
        }
    }

    public FacturaPDFDTO obtenerFacturaDirectaParaPDF(int idFactura, int idVendedor) {
        if (idFactura <= 0 || idVendedor <= 0) {
            return null;
        }
        String sqlCabecera = "SELECT f.id_factura, f.numero_factura, f.subtotal, f.impuestos, f.total, f.fecha_emision, "
                + "CONCAT(u.nombre, ' ', u.apellido) AS comprador, u.identificacion, "
                + "t.nombre_tienda, t.nit, t.direccion, tp.nombre_pago, p.numero_referencia_pago "
                + "FROM facturas f "
                + "INNER JOIN clientes c ON c.id_cliente = f.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario = c.id_usuario "
                + "INNER JOIN tiendas t ON t.id_tienda = f.id_tienda "
                + "INNER JOIN pagos p ON p.id_factura = f.id_factura "
                + "LEFT JOIN tipo_pago tp ON tp.id_tipo_pago = p.id_tipo_pago "
                + "WHERE f.id_factura = ? AND f.id_solicitud IS NULL AND t.id_vendedor = ?";
        String sqlDetalle = "SELECT pr.nombre, df.cantidad, df.precio_unitario, df.subtotal "
                + "FROM detalle_facturas df INNER JOIN productos pr ON pr.id_producto = df.id_producto "
                + "WHERE df.id_factura = ? ORDER BY df.id_detalle_factura";

        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlCabecera)) {
            ps.setInt(1, idFactura);
            ps.setInt(2, idVendedor);
            FacturaPDFDTO dto;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                dto = new FacturaPDFDTO();
                dto.setIdFactura(rs.getInt("id_factura"));
                dto.setNumeroFactura(rs.getString("numero_factura"));
                dto.setComprador(rs.getString("comprador"));
                dto.setIdentificacionComprador(rs.getString("identificacion"));
                dto.setTienda(rs.getString("nombre_tienda"));
                dto.setNitTienda(rs.getString("nit"));
                dto.setDireccionTienda(rs.getString("direccion"));
                dto.setMetodoPago(rs.getString("nombre_pago"));
                dto.setReferenciaPago(rs.getString("numero_referencia_pago"));
                dto.setSubtotal(rs.getBigDecimal("subtotal"));
                dto.setImpuestos(rs.getBigDecimal("impuestos"));
                dto.setTotal(rs.getBigDecimal("total"));
                Timestamp ts = rs.getTimestamp("fecha_emision");
                if (ts != null) dto.setFechaEmision(ts.toLocalDateTime());
            }
            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                psDetalle.setInt(1, idFactura);
                try (ResultSet rs = psDetalle.executeQuery()) {
                    while (rs.next()) {
                        FacturaPDFDTO.Item item = new FacturaPDFDTO.Item();
                        item.setNombre(rs.getString("nombre"));
                        item.setCantidad(rs.getInt("cantidad"));
                        item.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                        item.setSubtotal(rs.getBigDecimal("subtotal"));
                        dto.getItems().add(item);
                    }
                }
            }
            return dto;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar la factura de venta directa.", e);
        }
    }

    private boolean existe(Connection conn, String sql, int value) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existe(Connection conn, String sql, int first, int second) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, first);
            ps.setInt(2, second);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int consultarId(Connection conn, String sql, String message) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new IllegalStateException(message);
            return rs.getInt(1);
        }
    }
}
