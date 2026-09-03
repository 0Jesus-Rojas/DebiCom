/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.DetalleSolicitudCredito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class DetalleSolicitudCreditoDAO {
    private Conexion conect = new Conexion();

    public DetalleSolicitudCredito consultarDetalle(int idDetalleSolicitud) {
        Connection conn = conect.getconn();
        DetalleSolicitudCredito miDetalle = null;
        
        try {
            String querySql = "SELECT id_solicitud, id_producto, cantidad, precio_unitario, subtotal FROM detalle_solicitud_credito WHERE id_detalle_solicitud = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idDetalleSolicitud);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miDetalle = new DetalleSolicitudCredito();
                miDetalle.setIdDetalleSolicitud(idDetalleSolicitud);
                miDetalle.setIdSolicitud(rs.getInt("id_solicitud"));
                miDetalle.setIdProducto(rs.getInt("id_producto"));
                miDetalle.setCantidad(rs.getInt("cantidad"));
                miDetalle.setPrecioUnitario(rs.getFloat("precio_unitario"));
                miDetalle.setSubtotal(rs.getFloat("subtotal"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miDetalle;
        }
        return miDetalle;
    }

    public boolean insertarDetalle(DetalleSolicitudCredito miDetalle) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO detalle_solicitud_credito(id_solicitud, id_producto, cantidad, precio_unitario, subtotal) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miDetalle.getIdSolicitud());
            ps.setInt(2, miDetalle.getIdProducto());
            ps.setInt(3, miDetalle.getCantidad());
            ps.setFloat(4, miDetalle.getPrecioUnitario());
            ps.setFloat(5, miDetalle.getSubtotal());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Detalle de solicitud registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarDetalle(DetalleSolicitudCredito miDetalle) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE detalle_solicitud_credito SET id_solicitud = ?, id_producto = ?, cantidad = ?, precio_unitario = ?, subtotal = ? WHERE id_detalle_solicitud = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miDetalle.getIdSolicitud());
            ps.setInt(2, miDetalle.getIdProducto());
            ps.setInt(3, miDetalle.getCantidad());
            ps.setFloat(4, miDetalle.getPrecioUnitario());
            ps.setFloat(5, miDetalle.getSubtotal());
            ps.setInt(6, miDetalle.getIdDetalleSolicitud());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Detalle de solicitud actualizado");
            } else {
                System.out.println("No se encontró el detalle de la solicitud");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarDetalle(int idDetalleSolicitud) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM detalle_solicitud_credito WHERE id_detalle_solicitud = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idDetalleSolicitud);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Detalle de solicitud eliminado");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
}