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
}