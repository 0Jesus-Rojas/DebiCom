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

/**
 * DAO de solicitudes de crédito.
 */
public class SolicitudCreditoDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Registra una solicitud en estado PENDIENTE y retorna su ID.
     * La operación se realiza dentro de una transacción.
     */
    public int registrarSolicitud(int idCliente, int idTienda, BigDecimal monto,
            LocalDate fechaVencimiento, String observaciones) {

        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }

        String sqlEstado = "SELECT id_estado_solicitud FROM estados_solicitud "
                + "WHERE UPPER(nombre_estado) = 'PENDIENTE' LIMIT 1";

        String sqlCliente = "SELECT id_cliente FROM clientes WHERE id_cliente = ?";
        String sqlTienda = "SELECT id_tienda FROM tiendas WHERE id_tienda = ?";
        String sqlInsert = "INSERT INTO solicitudes_credito "
                + "(id_cliente, id_tienda, monto_total, saldo_pendiente, id_estado_solicitud, "
                + "fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, NULL, ?, ?)";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);

            try {
                if (!existeId(conn, sqlCliente, idCliente)) {
                    throw new IllegalArgumentException("El cliente no existe.");
                }
                if (!existeId(conn, sqlTienda, idTienda)) {
                    throw new IllegalArgumentException("La tienda no existe.");
                }

                int idEstadoPendiente = obtenerIdEstadoPendiente(conn, sqlEstado);

                try (PreparedStatement ps = conn.prepareStatement(
                        sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, idCliente);
                    ps.setInt(2, idTienda);
                    ps.setBigDecimal(3, monto);
                    ps.setBigDecimal(4, monto);
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
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }

                if (e instanceof IllegalArgumentException) {
                    throw (IllegalArgumentException) e;
                }
                if (e instanceof SQLException) {
                    throw new IllegalStateException("No fue posible registrar la solicitud de crédito.", e);
                }
                throw new IllegalStateException("No fue posible registrar la solicitud de crédito.", e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // La conexión se cerrará igualmente.
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible iniciar la transacción de crédito.", e);
        }
    }

    /**
     * Obtiene las solicitudes de un cliente, ordenadas de más reciente a más antigua.
     */
    public List<SolicitudCreditoDTO> listarPorCliente(int idCliente) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre, ' ', u.apellido) AS cliente, sc.monto_total, "
                + "sc.saldo_pendiente, es.nombre_estado, sc.fecha_solicitud, "
                + "sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente = sc.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario = c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "WHERE sc.id_cliente = ? "
                + "ORDER BY sc.fecha_solicitud DESC, sc.id_solicitud DESC";

        return listar(sql, idCliente);
    }

    /**
     * Busca una solicitud individual perteneciente a un cliente.
     */
    public SolicitudCreditoDTO consultarPorIdYCliente(int idSolicitud, int idCliente) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre, ' ', u.apellido) AS cliente, sc.monto_total, "
                + "sc.saldo_pendiente, es.nombre_estado, sc.fecha_solicitud, "
                + "sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente = sc.id_cliente "
                + "INNER JOIN usuarios u ON u.id_usuario = c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "WHERE sc.id_solicitud = ? AND sc.id_cliente = ?";

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

    private List<SolicitudCreditoDTO> listar(String sql, int idCliente) {
        List<SolicitudCreditoDTO> solicitudes = new ArrayList<>();

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapDTO(rs));
                }
            }

            return solicitudes;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible obtener las solicitudes de crédito.", e);
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
        dto.setEstado(rs.getString("nombre_estado"));

        Timestamp fechaSolicitud = rs.getTimestamp("fecha_solicitud");
        if (fechaSolicitud != null) {
            dto.setFechaSolicitud(fechaSolicitud.toLocalDateTime());
        }

        Timestamp fechaAprobacion = rs.getTimestamp("fecha_aprobacion");
        if (fechaAprobacion != null) {
            dto.setFechaAprobacion(fechaAprobacion.toLocalDateTime());
        }

        Date fechaVencimiento = rs.getDate("fecha_vencimiento");
        if (fechaVencimiento != null) {
            dto.setFechaVencimiento(fechaVencimiento.toLocalDate());
        }

        dto.setObservaciones(rs.getString("observaciones"));
        return dto;
    }

    private int obtenerIdEstadoPendiente(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                throw new IllegalStateException("No existe el estado PENDIENTE en estados_solicitud.");
            }
            return rs.getInt(1);
        }
    }

    private boolean existeId(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    /*
     * Compatibilidad con las pruebas CRUD originales del proyecto.
     * La aplicación nueva utiliza registrarSolicitud/listarPorCliente/consultarPorIdYCliente.
     */
    public boolean insertarSolicitudCredito(Modelo.SolicitudesCredito solicitud) {
        if (solicitud == null) {
            return false;
        }

        String sql = "INSERT INTO solicitudes_credito "
                + "(id_cliente, id_tienda, monto_total, saldo_pendiente, id_estado_solicitud, "
                + "fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, solicitud.getIdCliente());
            ps.setInt(2, solicitud.getIdTienda());
            ps.setFloat(3, solicitud.getMontoTotal());
            ps.setFloat(4, solicitud.getSaldoPendiente());
            ps.setInt(5, solicitud.getIdEstadoSolicitud());

            if (solicitud.getFechaSolicitud() != null) {
                ps.setDate(6, solicitud.getFechaSolicitud());
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            if (solicitud.getFechaAprovacion() != null) {
                ps.setDate(7, solicitud.getFechaAprovacion());
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }

            ps.setDate(8, solicitud.getFechaVencimiento());
            ps.setString(9, solicitud.getObservaciones());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public Modelo.SolicitudesCredito consultarSolicitudCredito(int idSolicitud) {
        String sql = "SELECT id_solicitud, id_cliente, id_tienda, monto_total, saldo_pendiente, "
                + "id_estado_solicitud, fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones "
                + "FROM solicitudes_credito WHERE id_solicitud = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Modelo.SolicitudesCredito s = new Modelo.SolicitudesCredito();
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setIdCliente(rs.getInt("id_cliente"));
                s.setIdTienda(rs.getInt("id_tienda"));
                s.setMontoTotal(rs.getFloat("monto_total"));
                s.setSaldoPendiente(rs.getFloat("saldo_pendiente"));
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
        if (solicitud == null) {
            return false;
        }

        String sql = "UPDATE solicitudes_credito SET id_cliente=?, id_tienda=?, monto_total=?, "
                + "saldo_pendiente=?, id_estado_solicitud=?, fecha_solicitud=?, fecha_aprobacion=?, "
                + "fecha_vencimiento=?, observaciones=? WHERE id_solicitud=?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, solicitud.getIdCliente());
            ps.setInt(2, solicitud.getIdTienda());
            ps.setFloat(3, solicitud.getMontoTotal());
            ps.setFloat(4, solicitud.getSaldoPendiente());
            ps.setInt(5, solicitud.getIdEstadoSolicitud());
            ps.setDate(6, solicitud.getFechaSolicitud());
            ps.setDate(7, solicitud.getFechaAprovacion());
            ps.setDate(8, solicitud.getFechaVencimiento());
            ps.setString(9, solicitud.getObservaciones());
            ps.setInt(10, solicitud.getIdSolicitud());

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
