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
     * Registra un pago presencial y lo aplica a los créditos pendientes
     * del comprador para las tiendas administradas por el vendedor.
     *
     * Se procesa primero la deuda con vencimiento más próximo. La operación
     * es transaccional y también sincroniza clientes.credito_actual con la
     * suma real de los saldos pendientes del cliente.
     */
    public boolean registrarPagoPresencial(int idCliente, int idVendedor, java.math.BigDecimal monto) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El comprador no es válido.");
        }
        if (idVendedor <= 0) {
            throw new IllegalArgumentException("El vendedor no es válido.");
        }
        if (monto == null || monto.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        final java.math.BigDecimal CERO = java.math.BigDecimal.ZERO;

        String sqlDeudas = "SELECT sc.id_solicitud, sc.saldo_pendiente "
                + "FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "WHERE sc.id_cliente = ? "
                + "AND t.id_vendedor = ? "
                + "AND sc.saldo_pendiente > 0 "
                + "AND UPPER(es.nombre_estado) IN ('APROBADO','VENCIDO') "
                + "ORDER BY sc.fecha_vencimiento ASC, sc.id_solicitud ASC "
                + "FOR UPDATE";

        String sqlActualizarSaldo = "UPDATE solicitudes_credito sc "
                + "SET sc.saldo_pendiente = ?, "
                + "    sc.id_estado_solicitud = CASE "
                + "        WHEN ? = 0 THEN (SELECT id_estado_solicitud FROM estados_solicitud "
                + "                         WHERE UPPER(nombre_estado) = 'PAGADO' LIMIT 1) "
                + "        ELSE sc.id_estado_solicitud END "
                + "WHERE sc.id_solicitud = ?";

        String sqlRegistrarPago = "INSERT INTO pagos_presenciales "
                + "(id_cliente, id_vendedor, monto_pagado, numero_referencia, observaciones) "
                + "VALUES (?, ?, ?, ?, ?)";

        String sqlSaldoCliente = "SELECT COALESCE(SUM(saldo_pendiente), 0) "
                + "FROM solicitudes_credito "
                + "WHERE id_cliente = ? "
                + "AND saldo_pendiente > 0";

        String sqlActualizarCliente = "UPDATE clientes SET credito_actual = ? WHERE id_cliente = ?";

        try (Connection conn = conect.getConnection()) {
            conn.setAutoCommit(false);

            try {
                java.math.BigDecimal restante = monto;
                boolean encontroDeuda = false;
                java.math.BigDecimal aplicado = CERO;

                try (PreparedStatement psDeudas = conn.prepareStatement(sqlDeudas)) {
                    psDeudas.setInt(1, idCliente);
                    psDeudas.setInt(2, idVendedor);

                    try (ResultSet rs = psDeudas.executeQuery()) {
                        while (rs.next() && restante.compareTo(CERO) > 0) {
                            encontroDeuda = true;

                            int idSolicitud = rs.getInt("id_solicitud");
                            java.math.BigDecimal saldo = rs.getBigDecimal("saldo_pendiente");
                            java.math.BigDecimal abono = restante.min(saldo);
                            java.math.BigDecimal nuevoSaldo = saldo.subtract(abono);

                            try (PreparedStatement psActualizar = conn.prepareStatement(sqlActualizarSaldo)) {
                                psActualizar.setBigDecimal(1, nuevoSaldo);
                                psActualizar.setBigDecimal(2, nuevoSaldo);
                                psActualizar.setInt(3, idSolicitud);
                                if (psActualizar.executeUpdate() != 1) {
                                    throw new IllegalStateException(
                                            "No fue posible actualizar el crédito #" + idSolicitud + ".");
                                }
                            }

                            aplicado = aplicado.add(abono);
                            restante = restante.subtract(abono);
                        }
                    }
                }

                if (!encontroDeuda) {
                    throw new IllegalArgumentException(
                            "El comprador no tiene créditos pendientes en las tiendas de este vendedor.");
                }

                if (restante.compareTo(CERO) > 0) {
                    throw new IllegalArgumentException(
                            "El monto ingresado supera la deuda pendiente del comprador.");
                }

                String referencia = "PRES-" + idCliente + "-" + System.currentTimeMillis();
                try (PreparedStatement psPago = conn.prepareStatement(sqlRegistrarPago)) {
                    psPago.setInt(1, idCliente);
                    psPago.setInt(2, idVendedor);
                    psPago.setBigDecimal(3, aplicado);
                    psPago.setString(4, referencia);
                    psPago.setString(5, "Pago presencial registrado por vendedor");
                    psPago.executeUpdate();
                }

                java.math.BigDecimal saldoTotalCliente;
                try (PreparedStatement psSaldo = conn.prepareStatement(sqlSaldoCliente)) {
                    psSaldo.setInt(1, idCliente);
                    try (ResultSet rs = psSaldo.executeQuery()) {
                        if (!rs.next()) {
                            saldoTotalCliente = CERO;
                        } else {
                            saldoTotalCliente = rs.getBigDecimal(1);
                            if (saldoTotalCliente == null) saldoTotalCliente = CERO;
                        }
                    }
                }

                try (PreparedStatement psCliente = conn.prepareStatement(sqlActualizarCliente)) {
                    psCliente.setBigDecimal(1, saldoTotalCliente);
                    psCliente.setInt(2, idCliente);
                    if (psCliente.executeUpdate() != 1) {
                        throw new IllegalStateException(
                                "No fue posible actualizar el saldo total del comprador.");
                    }
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                if (e instanceof IllegalArgumentException iae) {
                    throw iae;
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