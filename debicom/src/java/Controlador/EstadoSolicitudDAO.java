/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.EstadosSolicitud;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class EstadoSolicitudDAO {
    private Conexion conect = new Conexion();

    // Método para consultar un Estado de Solicitud por su ID
    public EstadosSolicitud consultarEstadoSolicitud(int idEstadoSolicitud) {
        Connection conn = conect.getconn();
        EstadosSolicitud miEstado = null;
        
        try {
            // Se asume que la tabla se llama 'estados_solicitud' y la columna 'nombre_estado'
            String querySql = "SELECT nombre_estado FROM estados_solicitud WHERE id_estado_solicitud = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idEstadoSolicitud);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miEstado = new EstadosSolicitud();
                miEstado.setIdEstadoSolicitud(idEstadoSolicitud);
                miEstado.setNombreEstado(rs.getString("nombre_estado"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miEstado;
        }
        return miEstado;
    }

    // Método para insertar un nuevo Estado de Solicitud
    public boolean insertarEstadoSolicitud(EstadosSolicitud miEstado) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            // Se asume que el ID es autoincrementable en la base de datos
            String querySql = "INSERT INTO estados_solicitud(nombre_estado) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miEstado.getNombreEstado());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Estado de solicitud registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    // Método para actualizar un Estado de Solicitud existente
    public boolean actualizarEstadoSolicitud(EstadosSolicitud miEstado) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE estados_solicitud SET nombre_estado = ? WHERE id_estado_solicitud = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miEstado.getNombreEstado());
            ps.setInt(2, miEstado.getIdEstadoSolicitud());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Estado de solicitud actualizado");
            } else {
                System.out.println("No se encontró el estado de solicitud");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    // Método para eliminar un Estado de Solicitud
    public boolean eliminarEstadoSolicitud(int idEstadoSolicitud) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM estados_solicitud WHERE id_estado_solicitud = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idEstadoSolicitud);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Estado de solicitud eliminado");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
}