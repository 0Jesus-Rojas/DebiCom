package Controlador;

import Modelo.dto.CompradorDTO;
import Modelo.dto.ItemCreditoDTO;
import Modelo.dto.ProductoDTO;
import Modelo.dto.SolicitudCreditoDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FiadoDAO {

    private final Conexion conexion = new Conexion();

    public List<ProductoDTO> listarProductos(int idTienda, int idVendedor) {
        if (idTienda <= 0 || idVendedor <= 0) {
            throw new IllegalArgumentException("Tienda o vendedor no válidos.");
        }
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_unitario, p.stock, p.id_tienda, "
                + "u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM productos p INNER JOIN tiendas t ON t.id_tienda=p.id_tienda "
                + "LEFT JOIN unidades u ON u.id_unidad=p.id_unidad "
                + "LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE p.id_tienda=? AND t.id_vendedor=? AND p.stock>0 "
                + "AND UPPER(COALESCE(ep.nombre_estado,''))='ACTIVO' ORDER BY p.nombre";

        List<ProductoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
            throw new IllegalStateException("No fue posible cargar los productos para el fiado.", e);
        }
        return result;
    }

    public int asignarFiado(int idVendedor, int idTienda, int idCliente,
                            LocalDate fechaVencimiento, String observaciones,
                            List<ItemCreditoDTO> items) {
        if (idVendedor <= 0 || idTienda <= 0 || idCliente <= 0) {
            throw new IllegalArgumentException("Vendedor, tienda y comprador son obligatorios.");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        LocalDate hoy = LocalDate.now();
        if (fechaVencimiento.isBefore(hoy) || fechaVencimiento.isAfter(hoy.plusDays(7))) {
            throw new IllegalArgumentException("El plazo del fiado debe ser de máximo 7 días.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un producto para el fiado.");
        }

        LinkedHashMap<Integer,Integer> cantidades = new LinkedHashMap<>();
        for (ItemCreditoDTO item : items) {
            if (item == null || item.getIdProducto() <= 0 || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada producto debe tener una cantidad mayor que cero.");
            }
            cantidades.merge(item.getIdProducto(), item.getCantidad(), Math::addExact);
        }

        String sqlTienda = "SELECT id_tienda FROM tiendas WHERE id_tienda=? AND id_vendedor=?";
        String sqlCliente = "SELECT id_cliente FROM clientes WHERE id_cliente=?";
        String sqlEstado = "SELECT id_estado_solicitud FROM estados_solicitud "
                + "WHERE UPPER(nombre_estado)='APROBADO' LIMIT 1";
        String sqlTipo = "SELECT id_tipo_prestamo FROM tipos_prestamo "
                + "WHERE UPPER(nombre_tipo)='FIADO' LIMIT 1";
        String sqlSalida = "SELECT id_tipo_movimiento FROM tipos_movimiento "
                + "WHERE UPPER(nombre_tipo)='SALIDA' LIMIT 1";
        String sqlProducto = "SELECT p.id_producto, p.nombre, p.precio_unitario, p.stock FROM productos p "
                + "INNER JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE p.id_producto=? AND p.id_tienda=? AND UPPER(ep.nombre_estado)='ACTIVO' FOR UPDATE";
        String sqlInsert = "INSERT INTO solicitudes_credito "
                + "(id_cliente,id_tienda,monto_total,saldo_pendiente,cupo_aprobado,id_tipo_prestamo,"
                + "id_estado_solicitud,fecha_solicitud,fecha_aprobacion,fecha_vencimiento,observaciones) "
                + "VALUES (?, ?, ?, ?, 0, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_solicitud_credito "
                + "(id_solicitud,id_producto,cantidad,precio_unitario,subtotal) VALUES (?,?,?,?,?)";
        String sqlStock = "UPDATE productos SET stock=? WHERE id_producto=?";
        String sqlMovimiento = "INSERT INTO movimientos_inventario"
                + "(id_producto,id_tipo_movimiento,cantidad,motivo,fecha_movimiento) VALUES (?,?,?, ?,CURRENT_TIMESTAMP)";
        String sqlSaldoCliente = "UPDATE clientes SET credito_actual="
                + "(SELECT COALESCE(SUM(saldo_pendiente),0) FROM solicitudes_credito WHERE id_cliente=? AND saldo_pendiente>0) "
                + "WHERE id_cliente=?";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!existeId(conn, sqlTienda, idTienda, idVendedor)) {
                    throw new SecurityException("La tienda no pertenece al vendedor autenticado.");
                }
                if (!existeId(conn, sqlCliente, idCliente)) {
                    throw new IllegalArgumentException("El comprador no existe.");
                }

                int idEstado = consultarId(conn, sqlEstado, "No existe el estado APROBADO.");
                int idTipoFiado = consultarId(conn, sqlTipo, "No existe el tipo FIADO.");
                int idSalida = consultarId(conn, sqlSalida, "No existe el tipo de movimiento SALIDA.");

                class ItemValido {
                    int id, cantidad, stock;
                    BigDecimal precio, subtotal;
                    ItemValido(int id,int cantidad,int stock,BigDecimal precio,BigDecimal subtotal) {
                        this.id=id; this.cantidad=cantidad; this.stock=stock; this.precio=precio; this.subtotal=subtotal;
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
                                throw new IllegalArgumentException("El producto #" + entry.getKey() + " no está disponible en la tienda.");
                            }
                            int cantidad = entry.getValue();
                            int stock = rs.getInt("stock");
                            if (cantidad > stock) {
                                throw new IllegalArgumentException("Stock insuficiente para el producto #" + entry.getKey() + ".");
                            }
                            BigDecimal precio = rs.getBigDecimal("precio_unitario").setScale(2, RoundingMode.HALF_UP);
                            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
                            validos.add(new ItemValido(entry.getKey(), cantidad, stock, precio, subtotal));
                            total = total.add(subtotal).setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                }

                if (total.signum() <= 0) {
                    throw new IllegalArgumentException("El total del fiado debe ser mayor que cero.");
                }

                int idSolicitud;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idTienda);
                    ps.setBigDecimal(3, total);
                    ps.setBigDecimal(4, total);
                    ps.setInt(5, idTipoFiado);
                    ps.setInt(6, idEstado);
                    ps.setDate(7, Date.valueOf(fechaVencimiento));
                    if (observaciones == null || observaciones.isBlank()) ps.setNull(8, java.sql.Types.VARCHAR);
                    else ps.setString(8, observaciones.trim());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No se generó el ID del fiado.");
                        idSolicitud = rs.getInt(1);
                    }
                }

                for (ItemValido item : validos) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
                        ps.setInt(1, idSolicitud);
                        ps.setInt(2, item.id);
                        ps.setInt(3, item.cantidad);
                        ps.setBigDecimal(4, item.precio);
                        ps.setBigDecimal(5, item.subtotal);
                        if (ps.executeUpdate() != 1) throw new SQLException("No se pudo guardar el detalle del fiado.");
                    }
                    try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                        ps.setInt(1, item.stock - item.cantidad);
                        ps.setInt(2, item.id);
                        if (ps.executeUpdate() != 1) throw new SQLException("No se pudo actualizar inventario.");
                    }
                    try (PreparedStatement ps = conn.prepareStatement(sqlMovimiento)) {
                        ps.setInt(1, item.id);
                        ps.setInt(2, idSalida);
                        ps.setInt(3, item.cantidad);
                        ps.setString(4, "Fiado #" + idSolicitud);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlSaldoCliente)) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idCliente);
                    ps.executeUpdate();
                }

                conn.commit();
                return idSolicitud;
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                if (e instanceof IllegalArgumentException iae) throw iae;
                if (e instanceof SecurityException se) throw se;
                throw new IllegalStateException("No fue posible asignar el fiado.", e);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible iniciar la transacción del fiado.", e);
        }
    }

    public List<SolicitudCreditoDTO> listarFiadosPendientes(int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, "
                + "sc.cupo_aprobado, sc.id_tipo_prestamo, tp.nombre_tipo AS tipo_prestamo, "
                + "es.nombre_estado, sc.fecha_solicitud, sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "INNER JOIN tipos_prestamo tp ON tp.id_tipo_prestamo=sc.id_tipo_prestamo "
                + "WHERE t.id_vendedor=? AND UPPER(tp.nombre_tipo)='FIADO' "
                + "AND UPPER(es.nombre_estado) IN ('APROBADO','VENCIDO') AND sc.saldo_pendiente>0 "
                + "ORDER BY sc.fecha_vencimiento ASC, sc.id_solicitud ASC";

        List<SolicitudCreditoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapSolicitud(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar los fiados pendientes.", e);
        }
        return result;
    }

    private SolicitudCreditoDTO mapSolicitud(ResultSet rs) throws SQLException {
        SolicitudCreditoDTO dto = new SolicitudCreditoDTO();
        dto.setIdSolicitud(rs.getInt("id_solicitud"));
        dto.setIdCliente(rs.getInt("id_cliente"));
        dto.setIdTienda(rs.getInt("id_tienda"));
        dto.setTienda(rs.getString("nombre_tienda"));
        dto.setCliente(rs.getString("cliente"));
        dto.setMontoTotal(rs.getBigDecimal("monto_total"));
        dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
        dto.setCupoAprobado(rs.getBigDecimal("cupo_aprobado"));
        dto.setIdTipoPrestamo(rs.getInt("id_tipo_prestamo"));
        dto.setTipoPrestamo(rs.getString("tipo_prestamo"));
        dto.setEstado(rs.getString("nombre_estado"));
        Timestamp fechaSolicitud = rs.getTimestamp("fecha_solicitud");
        if (fechaSolicitud != null) dto.setFechaSolicitud(fechaSolicitud.toLocalDateTime());
        Timestamp fechaAprobacion = rs.getTimestamp("fecha_aprobacion");
        if (fechaAprobacion != null) dto.setFechaAprobacion(fechaAprobacion.toLocalDateTime());
        Date fecha = rs.getDate("fecha_vencimiento");
        if (fecha != null) dto.setFechaVencimiento(fecha.toLocalDate());
        dto.setObservaciones(rs.getString("observaciones"));
        return dto;
    }

    private boolean existeId(Connection conn, String sql, int a, int b) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a); ps.setInt(2, b);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private boolean existeId(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private int consultarId(Connection conn, String sql, String mensaje) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new IllegalStateException(mensaje);
            return rs.getInt(1);
        }
    }
}
