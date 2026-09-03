/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.SolicitudesCredito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class SolicitudCreditoDAO {
    private Conexion conect = new Conexion();

    public SolicitudesCredito consultarSolicitudCredito(int idSolicitud) {
        Connection conn = conect.getconn();
        SolicitudesCredito miSolicitud = null;
        
        try {
            String querySql = "SELECT id_cliente, id_tienda, monto_total, saldo_pendiente, id_estado_solicitud, fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones FROM solicitudes_credito WHERE id_solicitud = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idSolicitud);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miSolicitud = new SolicitudesCredito();
                miSolicitud.setIdSolicitud(idSolicitud);
                miSolicitud.setIdCliente(rs.getInt("id_cliente"));
                miSolicitud.setIdTienda(rs.getInt("id_tienda"));
                miSolicitud.setMontoTotal(rs.getFloat("monto_total"));
                miSolicitud.setSaldoPendiente(rs.getFloat("saldo_pendiente"));
                miSolicitud.setIdEstadoSolicitud(rs.getInt("id_estado_solicitud"));
                miSolicitud.setFechaSolicitud(rs.getDate("fecha_solicitud"));
                miSolicitud.setFechaAprovacion(rs.getDate("fecha_aprobacion"));
                miSolicitud.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                miSolicitud.setObservaciones(rs.getString("observaciones"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miSolicitud;
        }
        return miSolicitud;
    }

    public boolean insertarSolicitudCredito(SolicitudesCredito miSolicitud) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO solicitudes_credito(id_cliente, id_tienda, monto_total, saldo_pendiente, id_estado_solicitud, fecha_solicitud, fecha_aprobacion, fecha_vencimiento, observaciones) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miSolicitud.getIdCliente());
            ps.setInt(2, miSolicitud.getIdTienda());
            ps.setFloat(3, miSolicitud.getMontoTotal());
            ps.setFloat(4, miSolicitud.getSaldoPendiente());
            ps.setInt(5, miSolicitud.getIdEstadoSolicitud());
            ps.setDate(6, miSolicitud.getFechaSolicitud());
            ps.setDate(7, miSolicitud.getFechaAprovacion());
            ps.setDate(8, miSolicitud.getFechaVencimiento());
            ps.setString(9, miSolicitud.getObservaciones());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Solicitud de crédito registrada");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarSolicitudCredito(SolicitudesCredito miSolicitud) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE solicitudes_credito SET id_cliente = ?, id_tienda = ?, monto_total = ?, saldo_pendiente = ?, id_estado_solicitud = ?, fecha_solicitud = ?, fecha_aprobacion = ?, fecha_vencimiento = ?, observaciones = ? WHERE id_solicitud = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miSolicitud.getIdCliente());
            ps.setInt(2, miSolicitud.getIdTienda());
            ps.setFloat(3, miSolicitud.getMontoTotal());
            ps.setFloat(4, miSolicitud.getSaldoPendiente());
            ps.setInt(5, miSolicitud.getIdEstadoSolicitud());
            ps.setDate(6, miSolicitud.getFechaSolicitud());
            ps.setDate(7, miSolicitud.getFechaAprovacion());
            ps.setDate(8, miSolicitud.getFechaVencimiento());
            ps.setString(9, miSolicitud.getObservaciones());
            ps.setInt(10, miSolicitud.getIdSolicitud());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró la solicitud de crédito");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarSolicitudCredito(int idSolicitud) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM solicitudes_credito WHERE id_solicitud = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idSolicitud);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
}