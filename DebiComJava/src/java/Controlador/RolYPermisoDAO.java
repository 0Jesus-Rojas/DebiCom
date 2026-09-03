/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.RolesYPermisos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class RolYPermisoDAO {
    private Conexion conect = new Conexion();

    public RolesYPermisos consultarRolYPermiso(int idRol, int idRolPermiso) {
        Connection conn = conect.getconn();
        RolesYPermisos miRolYPermiso = null;
        
        try {
            String querySql = "SELECT id_rol, id_rol_permiso FROM roles_y_permisos WHERE id_rol = ? AND id_rol_permiso = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idRol);
            ps.setInt(2, idRolPermiso);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miRolYPermiso = new RolesYPermisos();
                miRolYPermiso.setIdRol(rs.getInt("id_rol"));
                miRolYPermiso.setIdRolPermiso(rs.getInt("id_rol_permiso"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miRolYPermiso;
        }
        return miRolYPermiso;
    }

    public boolean insertarRolYPermiso(RolesYPermisos miRolYPermiso) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO roles_y_permisos(id_rol, id_rol_permiso) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miRolYPermiso.getIdRol());
            ps.setInt(2, miRolYPermiso.getIdRolPermiso());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Rol y Permiso registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarRolYPermiso(RolesYPermisos miRolYPermiso) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            // Nota: Al ser una tabla intermedia con PK compuesta, 
            // normalmente se actualiza o se maneja mediante borrado e inserción.
            String querySql = "UPDATE roles_y_permisos SET id_rol_permiso = ? WHERE id_rol = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miRolYPermiso.getIdRolPermiso());
            ps.setInt(2, miRolYPermiso.getIdRol());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el registro");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarRolYPermiso(int idRol, int idRolPermiso) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM roles_y_permisos WHERE id_rol = ? AND id_rol_permiso = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idRol);
            ps.setInt(2, idRolPermiso);
            
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