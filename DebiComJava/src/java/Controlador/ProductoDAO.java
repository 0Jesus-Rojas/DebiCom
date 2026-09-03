/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to edit this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Productos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class ProductoDAO {
    private Conexion conect = new Conexion();

    public Productos consultarProducto(int idProducto) {
        Connection conn = conect.getconn();
        Productos miProducto = null;
        
        try {
            String querySql = "SELECT nombre, descripcion, precio_unitario, stock, id_tienda, id_unidad, id_estado_producto FROM productos WHERE id_producto = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idProducto);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miProducto = new Productos();
                miProducto.setIdProducto(idProducto);
                miProducto.setNombre(rs.getString("nombre"));
                miProducto.setDescripcion(rs.getString("descripcion"));
                miProducto.setPrecioUnitario(rs.getFloat("precio_unitario"));
                miProducto.setStock(rs.getInt("stock"));
                miProducto.setIdTienda(rs.getInt("id_tienda"));
                miProducto.setIdUnidad(rs.getInt("id_unidad"));
                miProducto.setIdEstadoProducto(rs.getInt("id_estado_producto"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miProducto;
        }
        return miProducto;
    }

    public boolean insertarProducto(Productos miProducto) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO productos(nombre, descripcion, precio_unitario, stock, id_tienda, id_unidad, id_estado_producto) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miProducto.getNombre());
            ps.setString(2, miProducto.getDescripcion());
            ps.setFloat(3, miProducto.getPrecioUnitario());
            ps.setInt(4, miProducto.getStock());
            ps.setInt(5, miProducto.getIdTienda());
            ps.setInt(6, miProducto.getIdUnidad());
            ps.setInt(7, miProducto.getIdEstadoProducto());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Producto registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarProducto(Productos miProducto) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE productos SET nombre = ?, descripcion = ?, precio_unitario = ?, stock = ?, id_tienda = ?, id_unidad = ?, id_estado_producto = ? WHERE id_producto = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miProducto.getNombre());
            ps.setString(2, miProducto.getDescripcion());
            ps.setFloat(3, miProducto.getPrecioUnitario());
            ps.setInt(4, miProducto.getStock());
            ps.setInt(5, miProducto.getIdTienda());
            ps.setInt(6, miProducto.getIdUnidad());
            ps.setInt(7, miProducto.getIdEstadoProducto());
            ps.setInt(8, miProducto.getIdProducto());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el producto");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarProducto(int idProducto) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM productos WHERE id_producto = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idProducto);
            
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