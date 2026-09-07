/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.RolPermisos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class RolPermisoDAO {
    private Conexion conect = new Conexion();

    public RolPermisos consultarRolPermiso(int idRolPermiso) {
        Connection conn = conect.getconn();
        RolPermisos miRolPermiso = null;
        
        try {
            String querySql = "SELECT codigo, descripcion FROM rol_permisos WHERE id_rol_permiso = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idRolPermiso);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miRolPermiso = new RolPermisos();
                miRolPermiso.setIdRolPermiso(idRolPermiso);
                miRolPermiso.setCodigo(rs.getString("codigo"));
                miRolPermiso.setDescripcion(rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miRolPermiso;
        }
        return miRolPermiso;
    }

    public boolean insertarRolPermiso(RolPermisos miRolPermiso) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO rol_permisos(codigo, descripcion) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miRolPermiso.getCodigo());
            ps.setString(2, miRolPermiso.getDescripcion());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Rol permiso registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarRolPermiso(RolPermisos miRolPermiso) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE rol_permisos SET codigo = ?, descripcion = ? WHERE id_rol_permiso = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miRolPermiso.getCodigo());
            ps.setString(2, miRolPermiso.getDescripcion());
            ps.setInt(3, miRolPermiso.getIdRolPermiso());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el rol permiso");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarRolPermiso(int idRolPermiso) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM rol_permisos WHERE id_rol_permiso = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idRolPermiso);
            
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