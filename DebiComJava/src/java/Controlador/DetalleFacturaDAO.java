/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.DetalleFacturas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class DetalleFacturaDAO {
    private Conexion conect = new Conexion();

    // Método para consultar un detalle de factura por su ID
    public DetalleFacturas consultarDetalleFactura(int idDetalleFactura) {
        Connection conn = conect.getconn();
        DetalleFacturas miDetalle = null;
        
        try {
            String querySql = "SELECT id_factura, id_producto, cantidad, precio_unitario, subtotal FROM detalle_facturas WHERE id_detalle_factura = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idDetalleFactura);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miDetalle = new DetalleFacturas();
                miDetalle.setIdDetalleFactura(idDetalleFactura);
                miDetalle.setIdFactura(rs.getInt("id_factura"));
                miDetalle.setIdProducto(rs.getInt("id_producto"));
                miDetalle.setCantidad(rs.getInt("cantidad"));
                miDetalle.setPrecioUnitarrio(rs.getFloat("precio_unitario"));
                miDetalle.setSubtotal(rs.getFloat("subtotal"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miDetalle;
        }
        return miDetalle;
    }

    // Método para insertar un nuevo detalle de factura
    public boolean insertarDetalleFactura(DetalleFacturas miDetalle) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO detalle_facturas(id_factura, id_producto, cantidad, precio_unitario, subtotal) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miDetalle.getIdFactura());
            ps.setInt(2, miDetalle.getIdProducto());
            ps.setInt(3, miDetalle.getCantidad());
            ps.setFloat(4, miDetalle.getPrecioUnitarrio());
            ps.setFloat(5, miDetalle.getSubtotal());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Detalle de factura registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    // Método para actualizar un detalle de factura existente
    public boolean actualizarDetalleFactura(DetalleFacturas miDetalle) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE detalle_facturas SET id_factura = ?, id_producto = ?, cantidad = ?, precio_unitario = ?, subtotal = ? WHERE id_detalle_factura = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miDetalle.getIdFactura());
            ps.setInt(2, miDetalle.getIdProducto());
            ps.setInt(3, miDetalle.getCantidad());
            ps.setFloat(4, miDetalle.getPrecioUnitarrio());
            ps.setFloat(5, miDetalle.getSubtotal());
            ps.setInt(6, miDetalle.getIdDetalleFactura());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Detalle de factura actualizado");
            } else {
                System.out.println("No se encontró el detalle de la factura");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    // Método para eliminar un detalle de factura
    public boolean eliminarDetalleFactura(int idDetalleFactura) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM detalle_facturas WHERE id_detalle_factura = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idDetalleFactura);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Detalle de factura eliminado");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
}