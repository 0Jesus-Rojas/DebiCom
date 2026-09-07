package Controlador;

import Modelo.dto.CompradorDTO;
import Modelo.dto.CreditoPendienteDTO;
import Modelo.dto.CreditosPendientesDTO;
import Modelo.dto.DashboardVendedorDTO;
import Modelo.dto.DetalleCreditoDTO;
import Modelo.dto.ItemCreditoDTO;
import Modelo.dto.PagoVendedorHistorialDTO;
import Modelo.dto.ProductoDTO;
import Modelo.dto.SolicitudCreditoDTO;
import Modelo.dto.TiendaDTO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Consultas y comandos agregados para el módulo de administración del vendedor. */
public class VendedorDAO {
    private static final int STOCK_BAJO_UMBRAL = 5;
    private final Conexion conexion = new Conexion();

    public List<TiendaDTO> listarTiendasDelVendedor(int idVendedor) {
        String sql = "SELECT id_tienda, nombre_tienda, nit, direccion, telefono, id_vendedor, fecha_registro "
                + "FROM tiendas WHERE id_vendedor = ? ORDER BY nombre_tienda";
        List<TiendaDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTienda(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar las tiendas del vendedor.", e);
        }
        return result;
    }

    public DashboardVendedorDTO obtenerDashboard(int idVendedor) {
        String base = " FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "WHERE t.id_vendedor = ? ";
        DashboardVendedorDTO dto = new DashboardVendedorDTO();
        String sqlClientes = "SELECT COUNT(DISTINCT sc.id_cliente)" + base;
        String sqlNuevas = "SELECT COUNT(*)" + base + "AND UPPER(es.nombre_estado) = 'PENDIENTE' AND sc.fecha_solicitud >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 7 DAY)";
        String sqlAprobadas = "SELECT COUNT(*)" + base + "AND UPPER(es.nombre_estado) = 'APROBADO'";
        String sqlPagos = "SELECT "
                + "(SELECT COALESCE(SUM(p.monto_pagado),0) FROM pagos p "
                + "INNER JOIN facturas f ON f.id_factura = p.id_factura "
                + "INNER JOIN tiendas t ON t.id_tienda = f.id_tienda WHERE t.id_vendedor = ?) "
                + "+ (SELECT COALESCE(SUM(pp.monto_pagado),0) FROM pagos_presenciales pp "
                + "WHERE pp.id_vendedor = ?)";
        String sqlDeuda = "SELECT COALESCE(SUM(sc.saldo_pendiente),0)" + base + "AND sc.saldo_pendiente > 0 "
                + "AND UPPER(es.nombre_estado) IN ('APROBADO','VENCIDO')";
        try (Connection conn = conexion.getConnection()) {
            dto.setTotalClientes(scalarLong(conn, sqlClientes, idVendedor));
            dto.setSolicitudesNuevas(scalarLong(conn, sqlNuevas, idVendedor));
            dto.setSolicitudesPendientes(dto.getSolicitudesNuevas());
            dto.setSolicitudesAprobadas(scalarLong(conn, sqlAprobadas, idVendedor));
            try (PreparedStatement ps = conn.prepareStatement(sqlPagos)) {
                ps.setInt(1, idVendedor);
                ps.setInt(2, idVendedor);
                try (ResultSet rs = ps.executeQuery()) {
                    dto.setPagosRecibidos(rs.next() && rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO);
                }
            }
            dto.setDeudaPendiente(scalarDecimal(conn, sqlDeuda, idVendedor));
            return dto;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible calcular el dashboard.", e);
        }
    }

    public TiendaDTO obtenerMetricasTienda(int idTienda, int idVendedor) {
        String sql = "SELECT t.id_tienda, t.nombre_tienda, t.nit, t.direccion, t.telefono, t.id_vendedor, "
                + "t.fecha_registro, CONCAT(u.nombre,' ',u.apellido) AS vendedor, "
                + "COALESCE((SELECT SUM(f.total) FROM facturas f INNER JOIN estados_factura ef ON ef.id_estado_factura=f.id_estado_factura WHERE f.id_tienda=t.id_tienda AND UPPER(ef.nombre_estado) <> 'ANULADA'),0) AS ventas_totales, "
                + "(SELECT COUNT(*) FROM solicitudes_credito sc INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + " WHERE sc.id_tienda=t.id_tienda AND UPPER(es.nombre_estado)='APROBADO') AS creditos_otorgados, "
                + "(SELECT COUNT(DISTINCT sc.id_cliente) FROM solicitudes_credito sc INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + " WHERE sc.id_tienda=t.id_tienda AND UPPER(es.nombre_estado)='APROBADO' AND sc.saldo_pendiente > 0) AS clientes_activos, "
                + "(SELECT COUNT(*) FROM productos p WHERE p.id_tienda=t.id_tienda) AS total_productos "
                + "FROM tiendas t INNER JOIN usuarios u ON u.id_usuario=t.id_vendedor "
                + "WHERE t.id_tienda=? AND t.id_vendedor=?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTienda); ps.setInt(2, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                TiendaDTO dto = mapTienda(rs);
                dto.setVendedor(rs.getString("vendedor"));
                dto.setVentasTotales(rs.getBigDecimal("ventas_totales"));
                dto.setCreditosOtorgados(rs.getLong("creditos_otorgados"));
                dto.setClientesActivos(rs.getLong("clientes_activos"));
                dto.setTotalProductos(rs.getLong("total_productos"));
                return dto;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible calcular las métricas de la tienda.", e);
        }
    }

    /**
     * Solicitudes que el vendedor puede aprobar/rechazar.
     * El estado PENDIENTE se fuerza en SQL para impedir que esta
     * pantalla exponga créditos ya procesados.
     */
    public List<SolicitudCreditoDTO> listarSolicitudesPendientes(int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, "
                + "es.nombre_estado, sc.fecha_solicitud, sc.fecha_aprobacion, "
                + "sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE t.id_vendedor=? "
                + "AND UPPER(es.nombre_estado)='PENDIENTE' "
                + "ORDER BY sc.fecha_solicitud ASC, sc.id_solicitud ASC";

        List<SolicitudCreditoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapSolicitud(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No fue posible listar las solicitudes pendientes.", e);
        }
        return result;
    }

    /**
     * Historial de créditos efectivamente otorgados por el vendedor.
     * Se incluyen estados posteriores a la aprobación (APROBADO, PAGADO
     * y VENCIDO), pero nunca PENDIENTE ni RECHAZADO.
     */
    public List<SolicitudCreditoDTO> listarHistorialCreditos(int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, "
                + "es.nombre_estado, sc.fecha_solicitud, sc.fecha_aprobacion, "
                + "sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE t.id_vendedor=? "
                + "AND UPPER(es.nombre_estado) IN ('APROBADO','PAGADO','VENCIDO') "
                + "ORDER BY sc.id_solicitud DESC";

        List<SolicitudCreditoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapSolicitud(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No fue posible consultar el historial de créditos.", e);
        }
        return result;
    }

    /**
     * Historial de pagos realizados por clientes a créditos del vendedor.
     * La pertenencia se valida tanto por pagos_presenciales.id_vendedor
     * como por tiendas.id_vendedor para evitar cruces de información.
     */
    public List<PagoVendedorHistorialDTO> listarHistorialPagos(int idVendedor) {
        String sql = "SELECT pp.id_pago_presencial, pp.id_solicitud, pp.id_cliente, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, "
                + "t.nombre_tienda, pp.monto_pagado, pp.fecha_pago, "
                + "pp.numero_referencia, pp.observaciones, es.nombre_estado "
                + "FROM pagos_presenciales pp "
                + "INNER JOIN solicitudes_credito sc ON sc.id_solicitud=pp.id_solicitud "
                + "INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=pp.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE pp.id_vendedor=? AND t.id_vendedor=? "
                + "ORDER BY pp.fecha_pago DESC, pp.id_pago_presencial DESC";

        List<PagoVendedorHistorialDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            ps.setInt(2, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PagoVendedorHistorialDTO dto = new PagoVendedorHistorialDTO();
                    dto.setIdPago(rs.getInt("id_pago_presencial"));
                    dto.setIdSolicitud(rs.getInt("id_solicitud"));
                    dto.setIdCliente(rs.getInt("id_cliente"));
                    dto.setCliente(rs.getString("cliente"));
                    dto.setTienda(rs.getString("nombre_tienda"));
                    dto.setMontoPagado(rs.getBigDecimal("monto_pagado"));

                    Timestamp fecha = rs.getTimestamp("fecha_pago");
                    if (fecha != null) {
                        dto.setFechaPago(fecha.toLocalDateTime());
                    }

                    dto.setNumeroReferencia(rs.getString("numero_referencia"));
                    dto.setObservaciones(rs.getString("observaciones"));
                    dto.setEstadoCredito(rs.getString("nombre_estado"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No fue posible consultar el historial de pagos del vendedor.", e);
        }
        return result;
    }

    public List<SolicitudCreditoDTO> listarSolicitudes(int idVendedor, String estado) {
        StringBuilder sql = new StringBuilder("SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, es.nombre_estado, "
                + "sc.fecha_solicitud, sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE t.id_vendedor=? ");
        if (estado != null && !estado.isBlank()) sql.append("AND UPPER(es.nombre_estado)=UPPER(?) ");
        sql.append("ORDER BY sc.fecha_solicitud DESC, sc.id_solicitud DESC");
        List<SolicitudCreditoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, idVendedor);
            if (estado != null && !estado.isBlank()) ps.setString(2, estado.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapSolicitud(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible listar las solicitudes.", e);
        }
        return result;
    }

    public DetalleCreditoDTO obtenerDetalleCredito(int idSolicitud, int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, es.nombre_estado, "
                + "sc.fecha_solicitud, sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE sc.id_solicitud=? AND t.id_vendedor=?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud); ps.setInt(2, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                SolicitudCreditoDTO solicitud = mapSolicitud(rs);
                DetalleCreditoDTO detalle = new DetalleCreditoDTO();
                detalle.setSolicitud(solicitud);
                detalle.setProductos(listarProductosSolicitud(conn, idSolicitud));
                return detalle;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar el detalle del crédito.", e);
        }
    }

    private List<ProductoDTO> listarProductosSolicitud(Connection conn, int idSolicitud) throws SQLException {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, dsc.precio_unitario, p.stock, p.id_tienda, "
                + "dsc.cantidad, dsc.subtotal, u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM detalle_solicitud_credito dsc INNER JOIN productos p ON p.id_producto=dsc.id_producto "
                + "LEFT JOIN unidades u ON u.id_unidad=p.id_unidad LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE dsc.id_solicitud=? ORDER BY p.nombre";
        List<ProductoDTO> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(rs.getInt("id_producto"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setDescripcion(rs.getString("descripcion"));
                    dto.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    dto.setCantidad(rs.getInt("cantidad"));
                    dto.setSubtotal(rs.getBigDecimal("subtotal"));
                    dto.setStock(rs.getInt("stock"));
                    dto.setIdTienda(rs.getInt("id_tienda"));
                    dto.setUnidad(rs.getString("unidad"));
                    dto.setEstado(rs.getString("nombre_estado"));
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public boolean actualizarEstadoCredito(int idSolicitud, int idVendedor, String nuevoEstado) {
        if (idSolicitud <= 0 || idVendedor <= 0) {
            throw new IllegalArgumentException("La solicitud o el vendedor no son válidos.");
        }

        if (nuevoEstado == null
                || (!"APROBADO".equalsIgnoreCase(nuevoEstado)
                && !"RECHAZADO".equalsIgnoreCase(nuevoEstado))) {
            throw new IllegalArgumentException("Estado no permitido. Use APROBADO o RECHAZADO.");
        }

        String estado = nuevoEstado.trim().toUpperCase();

        String sqlEstado = "SELECT id_estado_solicitud "
                + "FROM estados_solicitud "
                + "WHERE UPPER(nombre_estado) = ? "
                + "LIMIT 1";

        String sqlVerificar = "SELECT sc.id_solicitud "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN estados_solicitud es "
                + "ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "WHERE sc.id_solicitud = ? "
                + "AND t.id_vendedor = ? "
                + "AND UPPER(es.nombre_estado) = 'PENDIENTE'";

        String sqlUpdate = "UPDATE solicitudes_credito "
                + "SET id_estado_solicitud = ?, "
                + "fecha_aprobacion = CURRENT_TIMESTAMP "
                + "WHERE id_solicitud = ?";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int idEstado = -1;

                try (PreparedStatement ps = conn.prepareStatement(sqlEstado)) {
                    ps.setString(1, estado);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idEstado = rs.getInt("id_estado_solicitud");
                        }
                    }
                }

                if (idEstado <= 0) {
                    throw new IllegalStateException(
                            "No existe el estado " + estado + " en la tabla estados_solicitud.");
                }

                boolean puedeActualizar = false;

                try (PreparedStatement ps = conn.prepareStatement(sqlVerificar)) {
                    ps.setInt(1, idSolicitud);
                    ps.setInt(2, idVendedor);
                    try (ResultSet rs = ps.executeQuery()) {
                        puedeActualizar = rs.next();
                    }
                }

                if (!puedeActualizar) {
                    conn.rollback();
                    return false;
                }

                int filasActualizadas;

                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, idEstado);
                    ps.setInt(2, idSolicitud);
                    filasActualizadas = ps.executeUpdate();
                }

                if (filasActualizadas != 1) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (IllegalArgumentException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw new IllegalStateException(
                        "No fue posible actualizar el estado del crédito.", e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // La conexión se cerrará igualmente.
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No fue posible actualizar el estado del crédito.", e);
        }
    }

    public CreditosPendientesDTO obtenerCreditosPendientes(int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, CONCAT(u.nombre,' ',u.apellido) AS cliente, "
                + "sc.id_tienda, t.nombre_tienda, sc.monto_total, sc.saldo_pendiente, es.nombre_estado, "
                + "sc.fecha_solicitud, sc.fecha_vencimiento "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE t.id_vendedor=? AND UPPER(es.nombre_estado) IN ('APROBADO','VENCIDO') AND sc.saldo_pendiente>0 "
                + "ORDER BY sc.fecha_vencimiento ASC, sc.id_solicitud ASC";
        CreditosPendientesDTO result = new CreditosPendientesDTO();
        List<CreditoPendienteDTO> list = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                BigDecimal total = BigDecimal.ZERO;
                java.util.HashSet<Integer> clientes = new java.util.HashSet<>();
                while (rs.next()) {
                    CreditoPendienteDTO dto = new CreditoPendienteDTO();
                    dto.setIdSolicitud(rs.getInt("id_solicitud"));
                    dto.setIdCliente(rs.getInt("id_cliente"));
                    dto.setCliente(rs.getString("cliente"));
                    dto.setIdTienda(rs.getInt("id_tienda"));
                    dto.setTienda(rs.getString("nombre_tienda"));
                    dto.setMontoOriginal(rs.getBigDecimal("monto_total"));
                    dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
                    dto.setEstado(rs.getString("nombre_estado"));
                    Timestamp ts = rs.getTimestamp("fecha_solicitud");
                    if (ts != null) dto.setFechaSolicitud(ts.toLocalDateTime());
                    Date date = rs.getDate("fecha_vencimiento");
                    if (date != null) dto.setFechaVencimiento(date.toLocalDate());
                    if (dto.getFechaVencimiento() != null && dto.getFechaVencimiento().isBefore(LocalDate.now())) {
                        dto.setDiasMora(ChronoUnit.DAYS.between(dto.getFechaVencimiento(), LocalDate.now()));
                    } else dto.setDiasMora(0);
                    list.add(dto);
                    total = total.add(dto.getSaldoPendiente());
                    clientes.add(dto.getIdCliente());
                }
                result.setCreditos(list);
                result.setCreditosActivos(list.size());
                result.setDineroTotalPorCobrar(total);
                result.setClientesConDeuda(clientes.size());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar los créditos pendientes.", e);
        }
        return result;
    }

    /** Lista compradores activos (usuarios que tienen registro en clientes). */
    public List<CompradorDTO> listarCompradores() {
        String sql = "SELECT c.id_cliente, CONCAT(u.nombre,' ',u.apellido) AS nombre_completo, u.identificacion "
                + "FROM clientes c INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "ORDER BY u.nombre, u.apellido";
        List<CompradorDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CompradorDTO dto = new CompradorDTO();
                dto.setIdCliente(rs.getInt("id_cliente"));
                dto.setNombreCompleto(rs.getString("nombre_completo"));
                dto.setIdentificacion(rs.getString("identificacion"));
                result.add(dto);
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible listar los compradores.", e);
        }
    }

    /** Productos vendibles de una tienda propiedad del vendedor autenticado. */
    public List<ProductoDTO> listarProductosParaCredito(int idTienda, int idVendedor) {
        if (idTienda <= 0 || idVendedor <= 0) throw new IllegalArgumentException("Tienda o vendedor no válidos.");
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_unitario, p.stock, p.id_tienda, "
                + "u.nombre_unidad AS unidad, ep.nombre_estado FROM productos p "
                + "INNER JOIN tiendas t ON t.id_tienda=p.id_tienda "
                + "LEFT JOIN unidades u ON u.id_unidad=p.id_unidad "
                + "LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE p.id_tienda=? AND t.id_vendedor=? AND p.stock>0 AND UPPER(COALESCE(ep.nombre_estado,''))='ACTIVO' "
                + "ORDER BY p.nombre";
        List<ProductoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTienda); ps.setInt(2, idVendedor);
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
            throw new IllegalStateException("No fue posible cargar los productos para el crédito.", e);
        }
        return result;
    }

    /**
     * Crea un crédito directo en estado APROBADO, registra sus detalles,
     * descuenta inventario y sincroniza la deuda acumulada del comprador.
     */
    public int otorgarCreditoDirecto(int idVendedor, int idTienda, int idCliente,
                                     LocalDate fechaVencimiento, String observaciones,
                                     List<ItemCreditoDTO> items) {
        if (idVendedor <= 0 || idTienda <= 0 || idCliente <= 0) {
            throw new IllegalArgumentException("Vendedor, tienda y comprador son obligatorios.");
        }
        if (fechaVencimiento == null || fechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser hoy o posterior.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un producto.");
        }

        java.util.LinkedHashMap<Integer, Integer> cantidades = new java.util.LinkedHashMap<>();
        for (ItemCreditoDTO item : items) {
            if (item == null || item.getIdProducto() <= 0 || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada producto debe tener una cantidad mayor que cero.");
            }
            cantidades.merge(item.getIdProducto(), item.getCantidad(), Integer::sum);
        }

        String sqlTienda = "SELECT id_tienda FROM tiendas WHERE id_tienda=? AND id_vendedor=?";
        String sqlExisteCliente = "SELECT id_cliente FROM clientes WHERE id_cliente=?";
        String sqlEstado = "SELECT id_estado_solicitud FROM estados_solicitud WHERE UPPER(nombre_estado)='APROBADO' LIMIT 1";
        String sqlSalida = "SELECT id_tipo_movimiento FROM tipos_movimiento WHERE UPPER(nombre_tipo)='SALIDA' LIMIT 1";
        String sqlProducto = "SELECT p.id_producto, p.nombre, p.precio_unitario, p.stock FROM productos p "
                + "INNER JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE p.id_producto=? AND p.id_tienda=? AND UPPER(ep.nombre_estado)='ACTIVO' FOR UPDATE";
        String sqlInsertCredito = "INSERT INTO solicitudes_credito "
                + "(id_cliente,id_tienda,monto_total,saldo_pendiente,id_estado_solicitud,fecha_solicitud,fecha_aprobacion,fecha_vencimiento,observaciones) "
                + "VALUES (?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?,?)";
        String sqlInsertDetalle = "INSERT INTO detalle_solicitud_credito "
                + "(id_solicitud,id_producto,cantidad,precio_unitario,subtotal) VALUES (?,?,?,?,?)";
        String sqlUpdateStock = "UPDATE productos SET stock=? WHERE id_producto=?";
        String sqlMovimiento = "INSERT INTO movimientos_inventario(id_producto,id_tipo_movimiento,cantidad,motivo,fecha_movimiento) "
                + "VALUES (?,?,?,? ,CURRENT_TIMESTAMP)";
        String sqlActualizarCliente = "UPDATE clientes SET credito_actual=(SELECT COALESCE(SUM(saldo_pendiente),0) FROM solicitudes_credito WHERE id_cliente=? AND saldo_pendiente>0) WHERE id_cliente=?";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!existeId(conn, sqlTienda, idTienda, idVendedor)) {
                    throw new SecurityException("La tienda no pertenece al vendedor autenticado.");
                }
                if (!existeId(conn, sqlExisteCliente, idCliente)) {
                    throw new IllegalArgumentException("El comprador no existe.");
                }
                int idEstadoAprobado = consultarIdUnico(conn, sqlEstado, "No existe el estado APROBADO.");
                int idTipoSalida = consultarIdUnico(conn, sqlSalida, "No existe el tipo de movimiento SALIDA.");

                class ItemValido { int id; int cantidad; BigDecimal precio; BigDecimal subtotal; int stock; ItemValido(int id,int cantidad,BigDecimal precio,BigDecimal subtotal,int stock){this.id=id;this.cantidad=cantidad;this.precio=precio;this.subtotal=subtotal;this.stock=stock;} }
                List<ItemValido> validos = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;
                for (var entry : cantidades.entrySet()) {
                    int idProducto = entry.getKey();
                    int cantidad = entry.getValue();
                    try (PreparedStatement ps = conn.prepareStatement(sqlProducto)) {
                        ps.setInt(1, idProducto); ps.setInt(2, idTienda);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) throw new IllegalArgumentException("El producto #" + idProducto + " no está disponible en la tienda seleccionada.");
                            BigDecimal precio = rs.getBigDecimal("precio_unitario");
                            int stock = rs.getInt("stock");
                            if (cantidad > stock) throw new IllegalArgumentException("Stock insuficiente para el producto #" + idProducto + ". Disponible: " + stock + ".");
                            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, java.math.RoundingMode.HALF_UP);
                            validos.add(new ItemValido(idProducto,cantidad,precio,subtotal,stock));
                            total = total.add(subtotal);
                        }
                    }
                }
                if (total.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El total del crédito debe ser mayor que cero.");

                int idSolicitud;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertCredito, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1,idCliente); ps.setInt(2,idTienda); ps.setBigDecimal(3,total); ps.setBigDecimal(4,total); ps.setInt(5,idEstadoAprobado); ps.setDate(6,Date.valueOf(fechaVencimiento));
                    if (observaciones == null || observaciones.isBlank()) ps.setNull(7, java.sql.Types.VARCHAR); else ps.setString(7, observaciones.trim());
                    ps.executeUpdate();
                    try (ResultSet rs=ps.getGeneratedKeys()) { if(!rs.next()) throw new SQLException("No se generó el ID del crédito."); idSolicitud=rs.getInt(1); }
                }

                for (ItemValido item : validos) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertDetalle)) {
                        ps.setInt(1,idSolicitud); ps.setInt(2,item.id); ps.setInt(3,item.cantidad); ps.setBigDecimal(4,item.precio); ps.setBigDecimal(5,item.subtotal); ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStock)) {
                        ps.setInt(1,item.stock-item.cantidad); ps.setInt(2,item.id); if(ps.executeUpdate()!=1) throw new SQLException("No se pudo actualizar el inventario del producto #"+item.id+".");
                    }
                    try (PreparedStatement ps = conn.prepareStatement(sqlMovimiento)) {
                        ps.setInt(1,item.id); ps.setInt(2,idTipoSalida); ps.setInt(3,item.cantidad); ps.setString(4,"Venta a crédito #"+idSolicitud); ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlActualizarCliente)) {
                    ps.setInt(1,idCliente); ps.setInt(2,idCliente); ps.executeUpdate();
                }
                conn.commit();
                return idSolicitud;
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                if (e instanceof IllegalArgumentException iae) throw iae;
                if (e instanceof SecurityException se) throw se;
                throw new IllegalStateException("No fue posible otorgar el crédito.", e);
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { throw new IllegalStateException("No fue posible iniciar la transacción del crédito.", e); }
    }

    private boolean existeId(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){return rs.next();} }
    }

    private boolean existeId(Connection conn, String sql, int id, int second) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1,id); ps.setInt(2,second); try(ResultSet rs=ps.executeQuery()){return rs.next();} }
    }

    private int consultarIdUnico(Connection conn, String sql, String message) throws SQLException {
        try (PreparedStatement ps=conn.prepareStatement(sql); ResultSet rs=ps.executeQuery()) { if(!rs.next()) throw new IllegalStateException(message); return rs.getInt(1); }
    }

    public InventarioData obtenerInventario(int idTienda, int idVendedor, String search) {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_unitario, p.stock, p.id_tienda, "
                + "u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM productos p LEFT JOIN unidades u ON u.id_unidad=p.id_unidad "
                + "LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "INNER JOIN tiendas t ON t.id_tienda=p.id_tienda WHERE p.id_tienda=? AND t.id_vendedor=? ";
        boolean filtering = search != null && !search.isBlank();
        if (filtering) sql += "AND p.nombre LIKE ? ";
        sql += "ORDER BY p.nombre ASC";
        List<ProductoDTO> productos = new ArrayList<>();
        InventarioData data = new InventarioData();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTienda); ps.setInt(2, idVendedor); if (filtering) ps.setString(3, "%" + search.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(rs.getInt("id_producto"));
                    dto.setNombre(rs.getString("nombre")); dto.setDescripcion(rs.getString("descripcion"));
                    dto.setPrecioUnitario(rs.getBigDecimal("precio_unitario")); dto.setStock(rs.getInt("stock"));
                    dto.setIdTienda(rs.getInt("id_tienda")); dto.setUnidad(rs.getString("unidad")); dto.setEstado(rs.getString("nombre_estado"));
                    productos.add(dto);
                }
            }
            String metricSql = "SELECT "
                    + "SUM(CASE WHEN UPPER(COALESCE(ep.nombre_estado,'')) = 'ACTIVO' THEN 1 ELSE 0 END) AS activos, "
                    + "SUM(CASE WHEN UPPER(COALESCE(ep.nombre_estado,'')) = 'ACTIVO' AND p.stock > 0 THEN 1 ELSE 0 END) AS disponibles, "
                    + "SUM(CASE WHEN p.stock > 0 AND p.stock <= ? THEN 1 ELSE 0 END) AS bajo_stock, "
                    + "SUM(CASE WHEN p.stock <= 0 OR UPPER(COALESCE(ep.nombre_estado,'')) = 'AGOTADO' THEN 1 ELSE 0 END) AS agotados "
                    + "FROM productos p LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                    + "WHERE p.id_tienda=?";
            try (PreparedStatement ps2 = conn.prepareStatement(metricSql)) {
                ps2.setInt(1, STOCK_BAJO_UMBRAL); ps2.setInt(2, idTienda);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        data.activos = rs.getLong("activos"); data.disponibles = rs.getLong("disponibles");
                        data.bajoStock = rs.getLong("bajo_stock"); data.agotados = rs.getLong("agotados");
                    }
                }
            }
            data.productos = productos;
            return data;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar el inventario.", e);
        }
    }

    private long scalarLong(Connection conn, String sql, int param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1,param); try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getLong(1):0;} }
    }
    private BigDecimal scalarDecimal(Connection conn, String sql, int param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1,param); try(ResultSet rs=ps.executeQuery()){return rs.next() && rs.getBigDecimal(1)!=null?rs.getBigDecimal(1):BigDecimal.ZERO;} }
    }
    private TiendaDTO mapTienda(ResultSet rs) throws SQLException {
        TiendaDTO dto = new TiendaDTO(); dto.setIdTienda(rs.getInt("id_tienda")); dto.setNombreTienda(rs.getString("nombre_tienda"));
        dto.setNit(rs.getString("nit")); dto.setDireccion(rs.getString("direccion")); dto.setTelefono(rs.getString("telefono")); dto.setIdVendedor(rs.getInt("id_vendedor")); return dto;
    }
    private SolicitudCreditoDTO mapSolicitud(ResultSet rs) throws SQLException {
        SolicitudCreditoDTO dto = new SolicitudCreditoDTO(); dto.setIdSolicitud(rs.getInt("id_solicitud")); dto.setIdCliente(rs.getInt("id_cliente"));
        dto.setIdTienda(rs.getInt("id_tienda")); dto.setTienda(rs.getString("nombre_tienda")); dto.setCliente(rs.getString("cliente"));
        dto.setMontoTotal(rs.getBigDecimal("monto_total")); dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente")); dto.setEstado(rs.getString("nombre_estado"));
        Timestamp s=rs.getTimestamp("fecha_solicitud"); if(s!=null)dto.setFechaSolicitud(s.toLocalDateTime()); Timestamp a=rs.getTimestamp("fecha_aprobacion"); if(a!=null)dto.setFechaAprobacion(a.toLocalDateTime());
        Date v=rs.getDate("fecha_vencimiento"); if(v!=null)dto.setFechaVencimiento(v.toLocalDate()); dto.setObservaciones(rs.getString("observaciones")); return dto;
    }

    public static class InventarioData {
        public long activos, disponibles, bajoStock, agotados;
        public List<ProductoDTO> productos = new ArrayList<>();
    }
}
