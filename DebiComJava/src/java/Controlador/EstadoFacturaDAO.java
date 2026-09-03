/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.EstadosFactura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class EstadoFacturaDAO {
    private Conexion conect = new Conexion();

    // Método para consultar un Estado de Factura por su ID
    public EstadosFactura consultarEstadoFactura(int idEstadoFactura) {
        Connection conn = conect.getconn();
        EstadosFactura miEstado = null;
        
        try {
            String querySql = "SELECT nombre_estado FROM estados_factura WHERE id_estado_factura = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idEstadoFactura);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miEstado = new EstadosFactura();
                miEstado.setIdEstadoFactura(idEstadoFactura);
                miEstado.setNombreEstado(rs.getString("nombre_estado"));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
            return miEstado;
        }
        return miEstado;
    }

    // Método para insertar un nuevo Estado de Factura
    public boolean insertarEstadoFactura(EstadosFactura miEstado) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            // Se asume que id_estado_factura es autoincrementable, por lo que solo se inserta el nombre
            String querySql = "INSERT INTO estados_factura(nombre_estado) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miEstado.getNombreEstado());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Estado de factura registrado");
            
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
        return insertar;
    }

    // Método para actualizar un Estado de Factura existente
    public boolean actualizarEstadoFactura(EstadosFactura miEstado) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE estados_factura SET nombre_estado = ? WHERE id_estado_factura = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miEstado.getNombreEstado());
            ps.setInt(2, miEstado.getIdEstadoFactura());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Estado de factura actualizado");
            } else {
                System.out.println("No se encontró el estado de factura");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
        return actualizar;
    }

    // Método para eliminar un Estado de Factura
    public boolean eliminarEstadoFactura(int idEstadoFactura) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM estados_factura WHERE id_estado_factura = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idEstadoFactura);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Estado de factura eliminado");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
        return eliminar;
    }
}