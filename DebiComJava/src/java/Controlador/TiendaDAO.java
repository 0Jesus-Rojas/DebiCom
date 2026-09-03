/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Tiendas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class TiendaDAO {
    private Conexion conect = new Conexion();

    public Tiendas consultarTienda(int idTienda) {
        Connection conn = conect.getconn();
        Tiendas miTienda = null;
        
        try {
            String querySql = "SELECT nombre_tienda, nit, direccion, telefono, id_vendedor, fecha_registro FROM tiendas WHERE id_tienda = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idTienda);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miTienda = new Tiendas();
                miTienda.setIdTienda(idTienda);
                miTienda.setNombreTienda(rs.getString("nombre_tienda"));
                miTienda.setNit(rs.getString("nit"));
                miTienda.setDireccion(rs.getString("direccion"));
                miTienda.setTelefono(rs.getString("telefono"));
                miTienda.setIdVendedor(rs.getInt("id_vendedor"));
                miTienda.setFechaRegistro(rs.getDate("fecha_registro"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miTienda;
        }
        return miTienda;
    }

    public boolean insertarTienda(Tiendas miTienda) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO tiendas(nombre_tienda, nit, direccion, telefono, id_vendedor, fecha_registro) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miTienda.getNombreTienda());
            ps.setString(2, miTienda.getNit());
            ps.setString(3, miTienda.getDireccion());
            ps.setString(4, miTienda.getTelefono());
            ps.setInt(5, miTienda.getIdVendedor());
            ps.setDate(6, miTienda.getFechaRegistro());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Tienda registrada");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarTienda(Tiendas miTienda) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE tiendas SET nombre_tienda = ?, nit = ?, direccion = ?, telefono = ?, id_vendedor = ?, fecha_registro = ? WHERE id_tienda = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miTienda.getNombreTienda());
            ps.setString(2, miTienda.getNit());
            ps.setString(3, miTienda.getDireccion());
            ps.setString(4, miTienda.getTelefono());
            ps.setInt(5, miTienda.getIdVendedor());
            ps.setDate(6, miTienda.getFechaRegistro());
            ps.setInt(7, miTienda.getIdTienda());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró la tienda");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarTienda(int idTienda) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM tiendas WHERE id_tienda = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idTienda);
            
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