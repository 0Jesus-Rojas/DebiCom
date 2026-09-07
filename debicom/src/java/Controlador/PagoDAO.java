/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Pagos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class PagoDAO {
    private Conexion conect = new Conexion();

    // Método para consultar un pago por su ID
    public Pagos consultarPago(int idPago) {
        Connection conn = conect.getconn();
        Pagos miPago = null;
        
        try {
            String querySql = "SELECT id_factura, numero_referencia_pago, monto_pagado, fecha_pago, id_tipo_pago, observaciones FROM pagos WHERE id_pago = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idPago);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miPago = new Pagos();
                miPago.setIdPagos(idPago);
                miPago.setIdFactura(rs.getInt("id_factura"));
                miPago.setNumeroReferenciaPago(rs.getString("numero_referencia_pago"));
                miPago.setMontoPagado(rs.getFloat("monto_pagado"));
                miPago.setFechaPago(rs.getDate("fecha_pago"));
                miPago.setIdTipoPago(rs.getInt("id_tipo_pago"));
                miPago.setObservaciones(rs.getString("observaciones"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miPago;
        }
        return miPago;
    }

    // Método para insertar un nuevo pago
    public boolean insertarPago(Pagos miPago) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO pagos(id_factura, numero_referencia_pago, monto_pagado, fecha_pago, id_tipo_pago, observaciones) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miPago.getIdFactura());
            ps.setString(2, miPago.getNumeroReferenciaPago());
            ps.setFloat(3, miPago.getMontoPagado());
            ps.setDate(4, miPago.getFechaPago());
            ps.setInt(5, miPago.getIdTipoPago());
            ps.setString(6, miPago.getObservaciones());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Pago registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    // Método para actualizar un pago existente
    public boolean actualizarPago(Pagos miPago) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE pagos SET id_factura = ?, numero_referencia_pago = ?, monto_pagado = ?, fecha_pago = ?, id_tipo_pago = ?, observaciones = ? WHERE id_pago = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miPago.getIdFactura());
            ps.setString(2, miPago.getNumeroReferenciaPago());
            ps.setFloat(3, miPago.getMontoPagado());
            ps.setDate(4, miPago.getFechaPago());
            ps.setInt(5, miPago.getIdTipoPago());
            ps.setString(6, miPago.getObservaciones());
            ps.setInt(7, miPago.getIdPagos());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Pago actualizado correctamente");
            } else {
                System.out.println("No se encontró el pago");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    // Método para eliminar un pago
    public boolean eliminarPago(int idPago) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM pagos WHERE id_pago = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idPago);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Pago eliminado correctamente");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
    /**
     * Registra un pago presencial aplicado exclusivamente al crédito indicado.
     * La operación es transaccional, bloquea el crédito y mantiene sincronizado
     * el saldo acumulado del comprador.
     */
    public boolean registrarPagoPresencial(int idSolicitud, int idVendedor, java.math.BigDecimal monto) {
        if (idSolicitud <= 0) {
            throw new IllegalArgumentException("El crédito no es válido.");
        }
        if (idVendedor <= 0) {
            throw new IllegalArgumentException("El vendedor no es válido.");
        }
        if (monto == null || monto.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        final String sqlCredito =
                "SELECT sc.id_cliente, sc.saldo_pendiente "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "WHERE sc.id_solicitud=? "
                + "AND t.id_vendedor=? "
                + "AND sc.saldo_pendiente>0 "
                + "FOR UPDATE";

        /*
         * CAMBIO PRINCIPAL:
         * El saldo se descuenta directamente en solicitudes_credito.
         * La condición saldo_pendiente >= monto evita saldos negativos
         * y la operación se hace dentro de la misma transacción.
         */
        final String sqlActualizarSaldo =
                "UPDATE solicitudes_credito SET "
                + "saldo_pendiente = saldo_pendiente - ?, "
                + "id_estado_solicitud = CASE "
                + "WHEN saldo_pendiente <= ? THEN ? "
                + "ELSE id_estado_solicitud END "
                + "WHERE id_solicitud=? "
                + "AND saldo_pendiente>=?";

        final String sqlEstadoPagado =
                "SELECT id_estado_solicitud "
                + "FROM estados_solicitud "
                + "WHERE UPPER(nombre_estado)='PAGADO' "
                + "LIMIT 1";

        final String sqlRegistrarPago =
                "INSERT INTO pagos_presenciales "
                + "(id_solicitud,id_cliente,id_vendedor,monto_pagado,"
                + "numero_referencia,observaciones) "
                + "VALUES (?,?,?,?,?,?)";

        final String sqlSaldoCliente =
                "SELECT COALESCE(SUM(saldo_pendiente),0) "
                + "FROM solicitudes_credito "
                + "WHERE id_cliente=? AND saldo_pendiente>0";

        final String sqlActualizarCliente =
                "UPDATE clientes SET credito_actual=? "
                + "WHERE id_cliente=?";

        try (Connection conn = conect.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int idCliente;
                java.math.BigDecimal saldoActual;

                try (PreparedStatement ps = conn.prepareStatement(sqlCredito)) {
                    ps.setInt(1, idSolicitud);
                    ps.setInt(2, idVendedor);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "El crédito no existe, no pertenece al vendedor o no tiene saldo pendiente.");
                        }

                        idCliente = rs.getInt("id_cliente");
                        saldoActual = rs.getBigDecimal("saldo_pendiente");
                    }
                }

                if (monto.compareTo(saldoActual) > 0) {
                    throw new IllegalArgumentException(
                            "El monto no puede superar el saldo pendiente del crédito ($"
                            + saldoActual + ").");
                }

                int idPagado;
                try (PreparedStatement ps = conn.prepareStatement(sqlEstadoPagado);
                     ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalStateException("No existe el estado PAGADO.");
                    }
                    idPagado = rs.getInt(1);
                }

                /*
                 * CAMBIO: actualizar directamente saldo_pendiente.
                 * Si el pago cubre exactamente la deuda, el crédito pasa a PAGADO.
                 */
                try (PreparedStatement ps = conn.prepareStatement(sqlActualizarSaldo)) {
                    ps.setBigDecimal(1, monto);
                    ps.setBigDecimal(2, monto);
                    ps.setInt(3, idPagado);
                    ps.setInt(4, idSolicitud);
                    ps.setBigDecimal(5, monto);

                    if (ps.executeUpdate() != 1) {
                        throw new IllegalStateException(
                                "No se pudo actualizar el saldo del crédito. "
                                + "El saldo pudo haber cambiado antes de registrar el pago.");
                    }
                }

                /*
                 * Recalcular credito_actual usando el valor real de
                 * solicitudes_credito después del abono.
                 */
                java.math.BigDecimal saldoTotalCliente;
                try (PreparedStatement ps = conn.prepareStatement(sqlSaldoCliente)) {
                    ps.setInt(1, idCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        saldoTotalCliente = (rs.next() && rs.getBigDecimal(1) != null)
                                ? rs.getBigDecimal(1)
                                : java.math.BigDecimal.ZERO;
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlActualizarCliente)) {
                    ps.setBigDecimal(1, saldoTotalCliente);
                    ps.setInt(2, idCliente);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("No se pudo sincronizar el crédito acumulado del comprador.");
                    }
                }

                /*
                 * El historial no controla la modificación de la deuda.
                 * Se utiliza un SAVEPOINT: si la instalación tiene una
                 * versión antigua/incompatible de pagos_presenciales, no
                 * se revierte el descuento ya aplicado al crédito.
                 */
                java.sql.Savepoint savepointHistorial = conn.setSavepoint("antes_historial_pago");
                try {
                    String referencia = "PRES-CRED-" + idSolicitud + "-" + System.currentTimeMillis();

                    try (PreparedStatement ps = conn.prepareStatement(sqlRegistrarPago)) {
                        ps.setInt(1, idSolicitud);
                        ps.setInt(2, idCliente);
                        ps.setInt(3, idVendedor);
                        ps.setBigDecimal(4, monto);
                        ps.setString(5, referencia);
                        ps.setString(6, "Pago presencial aplicado al crédito #" + idSolicitud);
                        ps.executeUpdate();
                    }
                } catch (SQLException historialError) {
                    conn.rollback(savepointHistorial);
                    System.err.println(
                            "Advertencia: el saldo del crédito fue actualizado, "
                            + "pero no se pudo guardar el historial en pagos_presenciales: "
                            + historialError.getMessage());
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollback) {
                    e.addSuppressed(rollback);
                }

                if (e instanceof IllegalArgumentException iae) {
                    throw iae;
                }

                if (e instanceof IllegalStateException ise) {
                    throw ise;
                }

                throw new IllegalStateException(
                        "No fue posible registrar el pago presencial.", e);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No fue posible conectar con la base de datos.", e);
        }
    }

}