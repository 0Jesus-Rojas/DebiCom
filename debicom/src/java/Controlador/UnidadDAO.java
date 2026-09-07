/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Unidades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jesus
 */
public class UnidadDAO {
    private Conexion conect = new Conexion();


    public List<Unidades> listarUnidades() {
        List<Unidades> unidades = new ArrayList<>();
        String sql = "SELECT id_unidad, nombre_unidad FROM unidades ORDER BY nombre_unidad";
        try (Connection conn = conect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Unidades unidad = new Unidades();
                unidad.setIdUnidad(rs.getInt("id_unidad"));
                unidad.setNombreUnidad(rs.getString("nombre_unidad"));
                unidades.add(unidad);
            }
            return unidades;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar las unidades.", e);
        }
    }

    public Unidades consultarUnidad(int idUnidad) {
        Connection conn = conect.getconn();
        Unidades miUnidad = null;
        
        try {
            String querySql = "SELECT nombre_unidad FROM unidades WHERE id_unidad = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idUnidad);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miUnidad = new Unidades();
                miUnidad.setIdUnidad(idUnidad);
                miUnidad.setNombreUnidad(rs.getString("nombre_unidad"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miUnidad;
        }
        return miUnidad;
    }

    public boolean insertarUnidad(Unidades miUnidad) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO unidades(nombre_unidad) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miUnidad.getNombreUnidad());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Unidad registrada");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarUnidad(Unidades miUnidad) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE unidades SET nombre_unidad = ? WHERE id_unidad = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setString(1, miUnidad.getNombreUnidad());
            ps.setInt(2, miUnidad.getIdUnidad());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró la unidad");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarUnidad(int idUnidad) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM unidades WHERE id_unidad = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idUnidad);
            
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