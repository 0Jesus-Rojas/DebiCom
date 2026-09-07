package Controlador;

import Modelo.dto.DeudaPendienteDTO;
import Modelo.dto.PagoHistorialDTO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Consultas de lectura específicas para las pantallas del comprador. */
public class ReporteCompradorDAO {
    private final Conexion conexion = new Conexion();

    public List<PagoHistorialDTO> listarPagosPorUsuario(int idUsuario) {
        String sql =
                "SELECT p.id_pago, p.id_factura, f.numero_factura, "
                + "p.numero_referencia_pago, p.monto_pagado, p.fecha_pago, "
                + "tp.nombre_pago AS tipo_pago, t.nombre_tienda, p.observaciones "
                + "FROM pagos p "
                + "INNER JOIN facturas f ON f.id_factura = p.id_factura "
                + "INNER JOIN clientes c ON c.id_cliente = f.id_cliente "
                + "INNER JOIN tiendas t ON t.id_tienda = f.id_tienda "
                + "LEFT JOIN tipo_pago tp ON tp.id_tipo_pago = p.id_tipo_pago "
                + "WHERE c.id_usuario = ? "
                + "AND (tp.nombre_pago IS NULL "
                + "OR UPPER(TRIM(tp.nombre_pago)) NOT LIKE '%PRESENCIAL%') "
                + "UNION ALL "
                + "SELECT pp.id_pago_presencial AS id_pago, "
                + "COALESCE(f.id_factura, 0) AS id_factura, "
                + "f.numero_factura, "
                + "pp.numero_referencia AS numero_referencia_pago, "
                + "pp.monto_pagado, "
                + "pp.fecha_pago, "
                + "'Presencial' AS tipo_pago, "
                + "t.nombre_tienda, "
                + "pp.observaciones "
                + "FROM pagos_presenciales pp "
                + "INNER JOIN solicitudes_credito sc "
                + "ON sc.id_solicitud = pp.id_solicitud "
                + "INNER JOIN clientes c "
                + "ON c.id_cliente = pp.id_cliente "
                + "INNER JOIN tiendas t "
                + "ON t.id_tienda = sc.id_tienda "
                + "LEFT JOIN facturas f "
                + "ON f.id_solicitud = sc.id_solicitud "
                + "WHERE c.id_usuario = ? "
                + "ORDER BY fecha_pago DESC, id_pago DESC";
        List<PagoHistorialDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PagoHistorialDTO dto = new PagoHistorialDTO();
                    dto.setIdPago(rs.getInt("id_pago"));
                    dto.setIdFactura(rs.getInt("id_factura"));
                    dto.setNumeroFactura(rs.getString("numero_factura"));
                    dto.setNumeroReferenciaPago(rs.getString("numero_referencia_pago"));
                    dto.setMontoPagado(rs.getBigDecimal("monto_pagado"));
                    Timestamp ts = rs.getTimestamp("fecha_pago");
                    if (ts != null) dto.setFechaPago(ts.toLocalDateTime());
                    dto.setTipoPago(rs.getString("tipo_pago"));
                    dto.setTienda(rs.getString("nombre_tienda"));
                    dto.setObservaciones(rs.getString("observaciones"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar el historial de pagos.", e);
        }
        return result;
    }

    public List<DeudaPendienteDTO> listarDeudasPorUsuario(int idUsuario) {
        String sql = "SELECT sc.id_solicitud, COALESCE(f.id_factura, 0) AS id_factura, "
                + "t.nombre_tienda, sc.monto_total, sc.saldo_pendiente, sc.fecha_vencimiento, es.nombre_estado "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN clientes c ON c.id_cliente = sc.id_cliente "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "LEFT JOIN facturas f ON f.id_solicitud = sc.id_solicitud "
                + "WHERE c.id_usuario = ? AND sc.saldo_pendiente > 0 "
                + "AND UPPER(es.nombre_estado) = 'APROBADO' "
                + "ORDER BY sc.fecha_vencimiento ASC, sc.id_solicitud ASC";
        List<DeudaPendienteDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DeudaPendienteDTO dto = new DeudaPendienteDTO();
                    dto.setIdSolicitud(rs.getInt("id_solicitud"));
                    dto.setIdFactura(rs.getInt("id_factura"));
                    dto.setTienda(rs.getString("nombre_tienda"));
                    dto.setMontoOriginal(rs.getBigDecimal("monto_total"));
                    dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
                    Date date = rs.getDate("fecha_vencimiento");
                    if (date != null) dto.setFechaVencimiento(date.toLocalDate());
                    dto.setEstado(rs.getString("nombre_estado"));
                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar las deudas pendientes.", e);
        }
        return result;
    }
}