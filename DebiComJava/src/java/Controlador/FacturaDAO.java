/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Facturas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class FacturaDAO {
    private Conexion conect = new Conexion();

    public Facturas consultarFactura(int idFactura) {
        Connection conn = conect.getconn();
        Facturas miFactura = null;
        
        try {
            String querySql = "SELECT numero_factura, id_solicitud, id_cliente, id_tienda, subtotal, impuestos, total, fecha_emision, id_estado_factura FROM facturas WHERE id_factura = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idFactura);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miFactura = new Facturas();
                miFactura.setIdFactura(idFactura);
                miFactura.setNumeroFactura(rs.getString("numero_factura"));
                miFactura.setIdSolicitud(rs.getInt("id_solicitud"));
                miFactura.setIdCliente(rs.getInt("id_cliente"));
                miFactura.setIdTienda(rs.getInt("id_tienda"));
                miFactura.setSubtotal(rs.getFloat("subtotal"));
                miFactura.setImpuestos(rs.getFloat("impuestos"));
                miFactura.setTotal(rs.getFloat("total"));
                miFactura.setFechaEmision(rs.getDate("fecha_emision"));
                miFactura.setIdEstadoFactura(rs.getInt("id_estado_factura"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miFactura;
        }
        return miFactura;
    }

    public boolean insertarFactura(Facturas miFactura) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO facturas(numero_factura, id_solicitud, id_cliente, id_tienda, subtotal, impuestos, total, fecha_emision, id_estado_factura) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miFactura.getNumeroFactura());
            ps.setInt(2, miFactura.getIdSolicitud());
            ps.setInt(3, miFactura.getIdCliente());
            ps.setInt(4, miFactura.getIdTienda());
            ps.setFloat(5, miFactura.getSubtotal());
            ps.setFloat(6, miFactura.getTotal());
            ps.setFloat(7, miFactura.getImpuestos());
            ps.setDate(8, miFactura.getFechaEmision());
            ps.setInt(9, miFactura.getIdEstadoFactura());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Factura registrada");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarFactura(Facturas miFactura) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE facturas SET numero_factura = ?, id_solicitud = ?, id_cliente = ?, id_tienda = ?, subtotal = ?, impuestos = ?, total = ?, fecha_emision = ?, id_estado_factura = ? WHERE id_factura = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miFactura.getNumeroFactura());
            ps.setInt(2, miFactura.getIdSolicitud());
            ps.setInt(3, miFactura.getIdCliente());
            ps.setInt(4, miFactura.getIdTienda());
            ps.setFloat(5, miFactura.getSubtotal());
            ps.setFloat(6, miFactura.getImpuestos());
            ps.setFloat(7, miFactura.getTotal());
            ps.setDate(8, miFactura.getFechaEmision());
            ps.setInt(9, miFactura.getIdEstadoFactura());
            ps.setInt(10, miFactura.getIdFactura());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Factura actualizada exitosamente");
            } else {
                System.out.println("No se encontró la factura");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarFactura(int idFactura) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM facturas WHERE id_factura = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idFactura);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Factura eliminada");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
}