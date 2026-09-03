/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.TiposMovimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class TipoMovimientoDAO {
    private Conexion conect = new Conexion();

    public TiposMovimiento consultarTipoMovimiento(int idTipoMovimiento) {
        Connection conn = conect.getconn();
        TiposMovimiento tipoMov = null;
        
        try {
            String querySql = "SELECT nombre_tipo FROM tipos_movimiento WHERE id_tipo_movimiento = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idTipoMovimiento);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tipoMov = new TiposMovimiento();
                tipoMov.setIdTipoMovimiento(idTipoMovimiento);
                tipoMov.setNombreTipo(rs.getString("nombre_tipo"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return tipoMov;
        }
        return tipoMov;
    }

    public boolean insertarTipoMovimiento(TiposMovimiento tipoMovimiento) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO tipos_movimiento(nombre_tipo) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, tipoMovimiento.getNombreTipo());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Tipo de movimiento registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarTipoMovimiento(TiposMovimiento tipoMovimiento) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE tipos_movimiento SET nombre_tipo = ? WHERE id_tipo_movimiento = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, tipoMovimiento.getNombreTipo());
            ps.setInt(2, tipoMovimiento.getIdTipoMovimiento());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el tipo de movimiento");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarTipoMovimiento(int idTipoMovimiento) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM tipos_movimiento WHERE id_tipo_movimiento = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idTipoMovimiento);
            
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