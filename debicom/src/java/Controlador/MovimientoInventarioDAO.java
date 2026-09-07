/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.MovimientosInventario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

/**
 *
 * @author Jesus
 */
public class MovimientoInventarioDAO {
    private Conexion conect = new Conexion();

    // Método para consultar un movimiento por su ID
    public MovimientosInventario consultarMovimiento(int idMovimiento) {
        Connection conn = conect.getconn();
        MovimientosInventario miMovimiento = null;
        
        try {
            String querySql = "SELECT id_producto, id_tipo_movimiento, cantidad, motivo, fecha_movimiento FROM movimientos_inventario WHERE id_movimiento = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idMovimiento);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miMovimiento = new MovimientosInventario();
                miMovimiento.setIdMovimiento(idMovimiento);
                miMovimiento.setIdProducto(rs.getInt("id_producto"));
                miMovimiento.setIdTipoMovimiento(rs.getInt("id_tipo_movimiento"));
                miMovimiento.setCantidad(rs.getInt("cantidad"));
                miMovimiento.setMotivo(rs.getString("motivo"));
                miMovimiento.setFechaMovimiento(rs.getDate("fecha_movimiento"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return miMovimiento;
    }

    // Método para insertar un nuevo movimiento
    public boolean insertarMovimiento(MovimientosInventario miMovimiento) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            // No incluimos id_movimiento en el INSERT porque es Autoincrementable (AI)
            String querySql = "INSERT INTO movimientos_inventario(id_producto, id_tipo_movimiento, cantidad, motivo, fecha_movimiento) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miMovimiento.getIdProducto());
            ps.setInt(2, miMovimiento.getIdTipoMovimiento());
            ps.setInt(3, miMovimiento.getCantidad());
            ps.setString(4, miMovimiento.getMotivo());
            ps.setDate(5, miMovimiento.getFechaMovimiento());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Movimiento registrado con éxito");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    // Método para actualizar un movimiento existente
    public boolean actualizarMovimiento(MovimientosInventario miMovimiento) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE movimientos_inventario SET id_producto = ?, id_tipo_movimiento = ?, cantidad = ?, motivo = ?, fecha_movimiento = ? WHERE id_movimiento = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setInt(1, miMovimiento.getIdProducto());
            ps.setInt(2, miMovimiento.getIdTipoMovimiento());
            ps.setInt(3, miMovimiento.getCantidad());
            ps.setString(4, miMovimiento.getMotivo());
            ps.setDate(5, miMovimiento.getFechaMovimiento());
            ps.setInt(6, miMovimiento.getIdMovimiento());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
                System.out.println("Movimiento actualizado con éxito");
            } else {
                System.out.println("No se encontró el movimiento para actualizar");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    // Método para eliminar un movimiento
    public boolean eliminarMovimiento(int idMovimiento) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM movimientos_inventario WHERE id_movimiento = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idMovimiento);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
                System.out.println("Movimiento eliminado con éxito");
            } else {
                System.out.println("No se encontró el movimiento para eliminar");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }
}