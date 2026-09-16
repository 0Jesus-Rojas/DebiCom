package Controlador;

import Modelo.dto.SolicitudCreditoDTO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SolicitudCreditoDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Registra una solicitud de CREDITO como cupo solicitado.
     * El cupo queda pendiente de aprobación y no genera deuda hasta que
     * posteriormente se consuman productos asociados a ese crédito.
     */
    public int registrarSolicitud(int idCliente, int idTienda, BigDecimal cupo,
            LocalDate fechaVencimiento, String observaciones) {

        if (cupo == null || cupo.signum() <= 0) {
            throw new IllegalArgumentException("El cupo solicitado debe ser mayor que cero.");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }

        String sqlEstado = "SELECT id_estado_solicitud FROM estados_solicitud "
                + "WHERE UPPER(nombre_estado) = 'PENDIENTE' LIMIT 1";
        String sqlTipo = "SELECT id_tipo_prestamo FROM tipos_prestamo "
                + "WHERE UPPER(nombre_tipo) = 'CREDITO' LIMIT 1";
        String sqlCliente = "SELECT id_cliente FROM clientes WHERE id_cliente = ?";
        String sqlTienda = "SELECT id_tienda FROM tiendas WHERE id_tienda = ?";
        String sqlInsert = "INSERT INTO solicitudes_credito "
                + "(id_cliente, id_tienda, monto_total, saldo_pendiente, cupo_aprobado, id_tipo_prestamo, "
                + "id_estado_solicitud, fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones) "
                + "VALUES (?, ?, 0, 0, ?, ?, ?, CURRENT_TIMESTAMP, NULL, ?, ?)";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!existeId(conn, sqlCliente, idCliente)) {
                    throw new IllegalArgumentException("El cliente no existe.");
                }
                if (!existeId(conn, sqlTienda, idTienda)) {
                    throw new IllegalArgumentException("La tienda no existe.");
                }

                int idEstadoPendiente = obtenerIdUnico(conn, sqlEstado,
                        "No existe el estado PENDIENTE en estados_solicitud.");
                int idTipoCredito = obtenerIdUnico(conn, sqlTipo,
                        "No existe el tipo de préstamo CREDITO.");

                try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idTienda);
                    ps.setBigDecimal(3, cupo);
                    ps.setInt(4, idTipoCredito);
                    ps.setInt(5, idEstadoPendiente);
                    ps.setDate(6, Date.valueOf(fechaVencimiento));
                    if (observaciones == null || observaciones.isBlank()) {
                        ps.setNull(7, java.sql.Types.VARCHAR);
                    } else {
                        ps.setString(7, observaciones.trim());
                    }

                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("La BD no devolvió el ID de la solicitud.");
                        }
                        int idSolicitud = rs.getInt(1);
                        conn.commit();
                        return idSolicitud;
                    }
                }
            } catch (Exception e) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { e.addSuppressed(rollbackEx); }
                if (e instanceof IllegalArgumentException iae) throw iae;
                throw new IllegalStateException("No fue posible registrar la solicitud de crédito.", e);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible iniciar la transacción de crédito.", e);
        }
    }

    public List<SolicitudCreditoDTO> listarPorCliente(int idCliente) {
        String sql = baseSelect()
                + "WHERE sc.id_cliente = ? AND UPPER(tp.nombre_tipo) IN ('CREDITO','FIADO') "
                + "ORDER BY sc.fecha_solicitud DESC, sc.id_solicitud DESC";
        return listar(sql, idCliente);
    }

    public SolicitudCreditoDTO consultarPorIdYCliente(int idSolicitud, int idCliente) {
        String sql = baseSelect()
                + "WHERE sc.id_solicitud = ? AND sc.id_cliente = ? "
                + "AND UPPER(tp.nombre_tipo) IN ('CREDITO','FIADO')";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ps.setInt(2, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapDTO(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar la solicitud.", e);
        }
    }

    private String baseSelect() {
        return "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre, ' ', u.apellido) AS cliente, sc.monto_total, "
                + "sc.saldo_pendiente, sc.cupo_aprobado, sc.id_tipo_prestamo, tp.nombre_tipo AS tipo_prestamo, "
                + "es.nombre_estado, sc.fecha_solicitud, sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente = sc.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario = c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "INNER JOIN tipos_prestamo tp ON tp.id_tipo_prestamo = sc.id_tipo_prestamo ";
    }

    private List<SolicitudCreditoDTO> listar(String sql, int idCliente) {
        List<SolicitudCreditoDTO> solicitudes = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) solicitudes.add(mapDTO(rs));
            }
            return solicitudes;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible obtener las solicitudes de crédito.", e);
        }
    }

    /** Productos asociados a cualquier préstamo visible para el comprador. */
    public List<Modelo.dto.ProductoDTO> listarProductosSolicitud(int idSolicitud) {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, dsc.precio_unitario, p.stock, p.id_tienda, "
                + "dsc.cantidad, dsc.subtotal, u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM detalle_solicitud_credito dsc "
                + "INNER JOIN productos p ON p.id_producto=dsc.id_producto "
                + "LEFT JOIN unidades u ON u.id_unidad=p.id_unidad "
                + "LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE dsc.id_solicitud=? ORDER BY p.nombre";
        List<Modelo.dto.ProductoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Modelo.dto.ProductoDTO dto = new Modelo.dto.ProductoDTO();
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
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar los productos del préstamo.", e);
        }
    }

    private SolicitudCreditoDTO mapDTO(ResultSet rs) throws SQLException {
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

        Date fechaVencimiento = rs.getDate("fecha_vencimiento");
        if (fechaVencimiento != null) dto.setFechaVencimiento(fechaVencimiento.toLocalDate());

        dto.setObservaciones(rs.getString("observaciones"));
        return dto;
    }

    private int obtenerIdUnico(Connection conn, String sql, String message) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new IllegalStateException(message);
            return rs.getInt(1);
        }
    }

    private boolean existeId(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /*
     * Compatibilidad con las pruebas CRUD originales del proyecto.
     */
    public boolean insertarSolicitudCredito(Modelo.SolicitudesCredito solicitud) {
        if (solicitud == null) return false;

        String sql = "INSERT INTO solicitudes_credito "
                + "(id_cliente, id_tienda, monto_total, saldo_pendiente, cupo_aprobado, id_tipo_prestamo, "
                + "id_estado_solicitud, fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, solicitud.getIdCliente());
            ps.setInt(2, solicitud.getIdTienda());
            ps.setFloat(3, solicitud.getMontoTotal());
            ps.setFloat(4, solicitud.getSaldoPendiente());
            ps.setFloat(5, solicitud.getCupoAprobado() > 0 ? solicitud.getCupoAprobado() : solicitud.getMontoTotal());
            ps.setInt(6, solicitud.getIdTipoPrestamo() > 0 ? solicitud.getIdTipoPrestamo() : 1);
            ps.setInt(7, solicitud.getIdEstadoSolicitud());
            ps.setDate(8, solicitud.getFechaSolicitud());
            ps.setDate(9, solicitud.getFechaAprovacion());
            ps.setDate(10, solicitud.getFechaVencimiento());
            ps.setString(11, solicitud.getObservaciones());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public Modelo.SolicitudesCredito consultarSolicitudCredito(int idSolicitud) {
        String sql = "SELECT id_solicitud, id_cliente, id_tienda, monto_total, saldo_pendiente, "
                + "cupo_aprobado, id_tipo_prestamo, id_estado_solicitud, fecha_solicitud, fecha_aprobacion, "
                + "fecha_vencimiento, observaciones FROM solicitudes_credito WHERE id_solicitud = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Modelo.SolicitudesCredito s = new Modelo.SolicitudesCredito();
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setIdCliente(rs.getInt("id_cliente"));
                s.setIdTienda(rs.getInt("id_tienda"));
                s.setMontoTotal(rs.getFloat("monto_total"));
                s.setSaldoPendiente(rs.getFloat("saldo_pendiente"));
                s.setCupoAprobado(rs.getFloat("cupo_aprobado"));
                s.setIdTipoPrestamo(rs.getInt("id_tipo_prestamo"));
                s.setIdEstadoSolicitud(rs.getInt("id_estado_solicitud"));
                s.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                s.setFechaAprovacion(rs.getDate("fecha_aprobacion"));
                s.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                s.setObservaciones(rs.getString("observaciones"));
                return s;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean actualizarSolicitudCredito(Modelo.SolicitudesCredito solicitud) {
        if (solicitud == null) return false;
        String sql = "UPDATE solicitudes_credito SET id_cliente=?, id_tienda=?, monto_total=?, "
                + "saldo_pendiente=?, cupo_aprobado=?, id_tipo_prestamo=?, id_estado_solicitud=?, "
                + "fecha_solicitud=?, fecha_aprobacion=?, fecha_vencimiento=?, observaciones=? WHERE id_solicitud=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, solicitud.getIdCliente());
            ps.setInt(2, solicitud.getIdTienda());
            ps.setFloat(3, solicitud.getMontoTotal());
            ps.setFloat(4, solicitud.getSaldoPendiente());
            ps.setFloat(5, solicitud.getCupoAprobado());
            ps.setInt(6, solicitud.getIdTipoPrestamo() > 0 ? solicitud.getIdTipoPrestamo() : 1);
            ps.setInt(7, solicitud.getIdEstadoSolicitud());
            ps.setDate(8, solicitud.getFechaSolicitud());
            ps.setDate(9, solicitud.getFechaAprovacion());
            ps.setDate(10, solicitud.getFechaVencimiento());
            ps.setString(11, solicitud.getObservaciones());
            ps.setInt(12, solicitud.getIdSolicitud());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminarSolicitudCredito(int idSolicitud) {
        String sql = "DELETE FROM solicitudes_credito WHERE id_solicitud = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }
}
