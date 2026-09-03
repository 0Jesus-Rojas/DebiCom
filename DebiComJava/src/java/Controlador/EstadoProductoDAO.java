/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.EstadosProducto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class EstadoProductoDAO {
    private Conexion conect = new Conexion();

    // Método para consultar un Estado de Producto por su ID
    public EstadosProducto consultarEstadoProducto(int idEstadoProducto) {
        Connection conn = conect.getconn();
        EstadosProducto miEstado = null;
        
        try {
            // Ajusta el nombre de la tabla y columnas a los de tu base de datos
            String querySql = "SELECT nombre_estado FROM estados_producto WHERE id_estado_producto = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idEstadoProducto);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miEstado = new EstadosProducto();
                miEstado.setIdEstadoProducto(idEstadoProducto);
                miEstado.setNomrebEstadpo(rs.getString("nombre_estado"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miEstado;
        }
        return miEstado;
    }

    // Método para insertar un nuevo Estado de Producto
    public boolean insertarEstadoProducto(EstadosProducto miEstado) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            // Asumimos que el id_estado_producto es autoincrementable en la base de datos
            String querySql = "INSERT INTO estados_producto(nombre_estado) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miEstado.getNomrebEstadpo());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Estado de producto registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    // Método para actualizar un Estado de Producto existente
    public boolean actualizarEstadoProducto(EstadosProducto miEstado) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE estados_producto SET nombre_estado = ? WHERE id_estado_producto = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miEstado.getNomrebEstadpo());
            ps.setInt(2, miEstado.getIdEstadoProducto());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el estado del producto");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    // Método para eliminar un Estado de Producto
    public boolean eliminarEstadoProducto(int idEstadoProducto) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM estados_producto WHERE id_estado_producto = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idEstadoProducto);
            
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