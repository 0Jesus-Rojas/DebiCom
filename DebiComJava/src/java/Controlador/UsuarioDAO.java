/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Usuarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

/**
 *
 * @author Jesus
 */
public class UsuarioDAO {
    private Conexion conect = new Conexion();

    public Usuarios consultarUsuario(int idUsuario) {
        Connection conn = conect.getconn();
        Usuarios miUsuario = null;
        
        try {
            String querySql = "SELECT nombre, apellido, identificacion, fecha_nacimiento, correo, telefono, direccion, password, fecha_vencimiento_clave, autoriza_datos, id_tipo_identificacion, id_rol, fecha_registro FROM usuarios WHERE id_usuario = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idUsuario);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miUsuario = new Usuarios();
                miUsuario.setIdUsuario(idUsuario);
                miUsuario.setNombre(rs.getString("nombre"));
                miUsuario.setApellido(rs.getString("apellido"));
                miUsuario.setIdentificacion(rs.getString("identificacion"));
                miUsuario.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                miUsuario.setCorreo(rs.getString("correo"));
                miUsuario.setTelefono(rs.getString("telefono"));
                miUsuario.setDireccion(rs.getString("direccion"));
                miUsuario.setPassword(rs.getString("password"));
                miUsuario.setFechaVencimientoClave(rs.getDate("fecha_vencimiento_clave"));
                miUsuario.setAutorizaDatos(rs.getBoolean("autoriza_datos"));
                miUsuario.setIdTipoIdentificacion(rs.getInt("id_tipo_identificacion"));
                miUsuario.setIdRol(rs.getInt("id_rol"));
                miUsuario.setFechaRegistro(rs.getDate("fecha_registro"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return miUsuario;
    }

    public boolean insertarUsuario(Usuarios miUsuario) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO usuarios(nombre, apellido, identificacion, fecha_nacimiento, correo, telefono, direccion, password, fecha_vencimiento_clave, autoriza_datos, id_tipo_identificacion, id_rol, fecha_registro) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miUsuario.getNombre());
            ps.setString(2, miUsuario.getApellido());
            ps.setString(3, miUsuario.getIdentificacion());
            ps.setDate(4, miUsuario.getFechaNacimiento());
            ps.setString(5, miUsuario.getCorreo());
            ps.setString(6, miUsuario.getTelefono());
            ps.setString(7, miUsuario.getDireccion());
            ps.setString(8, miUsuario.getPassword());
            ps.setDate(9, miUsuario.getFechaVencimientoClave());
            ps.setBoolean(10, miUsuario.isAutorizaDatos());
            ps.setInt(11, miUsuario.getIdTipoIdentificacion());
            ps.setInt(12, miUsuario.getIdRol());
            ps.setDate(13, miUsuario.getFechaRegistro());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Usuario registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarUsuario(Usuarios miUsuario) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE usuarios SET nombre = ?, apellido = ?, identificacion = ?, fecha_nacimiento = ?, correo = ?, telefono = ?, direccion = ?, password = ?, fecha_vencimiento_clave = ?, autoriza_datos = ?, id_tipo_identificacion = ?, id_rol = ?, fecha_registro = ? WHERE id_usuario = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miUsuario.getNombre());
            ps.setString(2, miUsuario.getApellido());
            ps.setString(3, miUsuario.getIdentificacion());
            ps.setDate(4, miUsuario.getFechaNacimiento());
            ps.setString(5, miUsuario.getCorreo());
            ps.setString(6, miUsuario.getTelefono());
            ps.setString(7, miUsuario.getDireccion());
            ps.setString(8, miUsuario.getPassword());
            ps.setDate(9, miUsuario.getFechaVencimientoClave());
            ps.setBoolean(10, miUsuario.isAutorizaDatos());
            ps.setInt(11, miUsuario.getIdTipoIdentificacion());
            ps.setInt(12, miUsuario.getIdRol());
            ps.setDate(13, miUsuario.getFechaRegistro());
            ps.setInt(14, miUsuario.getIdUsuario());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el usuario");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarUsuario(int idUsuario) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM usuarios WHERE id_usuario = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idUsuario);
            
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