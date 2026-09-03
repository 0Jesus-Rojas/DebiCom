/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Roles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class RolDAO {
    private Conexion conect = new Conexion();

    public Roles consultarRol(int idRol) {
        Connection conn = conect.getconn();
        Roles miRol = null;
        
        try {
            String querySql = "SELECT nombre_rol FROM roles WHERE id_rol = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idRol);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miRol = new Roles();
                miRol.setIdRol(idRol);
                miRol.setNombreRol(rs.getString("nombre_rol"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miRol;
        }
        return miRol;
    }

    public boolean insertarRol(Roles miRol) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO roles(nombre_rol) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miRol.getNombreRol());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Rol registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarRol(Roles miRol) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE roles SET nombre_rol = ? WHERE id_rol = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miRol.getNombreRol());
            ps.setInt(2, miRol.getIdRol());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el rol");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarRol(int idRol) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM roles WHERE id_rol = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idRol);
            
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