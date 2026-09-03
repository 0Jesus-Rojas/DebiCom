/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.TipoIdentificaciones;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Jesus
 */
public class TipoIdentificacionDAO {

    private Conexion conect = new Conexion();

    public TipoIdentificaciones consultarTipoIdentificacion(int idTipoIdentificacion) {
        Connection conn = conect.getconn();
        TipoIdentificaciones miTipo = null;

        try {
            String querySql = "SELECT nombre_tipo FROM tipo_identificaciones WHERE id_tipo_identificacion = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idTipoIdentificacion);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miTipo = new TipoIdentificaciones();
                miTipo.setIdTipoIdentificacion(idTipoIdentificacion);
                miTipo.setNombreTipo(rs.getString("nombre_tipo"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miTipo;
        }
        return miTipo;
    }

    public boolean insertarTipoIdentificacion(TipoIdentificaciones miTipo) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO tipo_identificaciones(nombre_tipo) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(querySql);

            ps.setString(1, miTipo.getNombreTipo());

            ps.executeUpdate();
            insertar = true;
            System.out.println("Tipo de identificación registrado");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarTipoIdentificacion(TipoIdentificaciones miTipo) {
        boolean actualizar = false;
        Connection conn = conect.getconn();

        try {
            String querySql = "UPDATE tipo_identificaciones SET nombre_tipo = ? WHERE id_tipo_identificacion = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);

            ps.setString(1, miTipo.getNombreTipo());
            ps.setInt(2, miTipo.getIdTipoIdentificacion());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el tipo de identificación");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarTipoIdentificacion(int idTipoIdentificacion) {
        boolean eliminar = false;

        String querySql = "DELETE FROM tipo_identificaciones WHERE id_tipo_identificacion = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idTipoIdentificacion);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                eliminar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return eliminar;
    }

    public ArrayList<TipoIdentificaciones> consultarTiposIdentificacion() {

        ArrayList<TipoIdentificaciones> lista = new ArrayList<>();

        Connection conn = conect.getconn();

        try {
            String querySql = "SELECT id_tipo_identificacion, nombre_tipo "
                    + "FROM tipo_identificaciones "
                    + "ORDER BY nombre_tipo";

            PreparedStatement ps = conn.prepareStatement(querySql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                TipoIdentificaciones tipo = new TipoIdentificaciones();

                tipo.setIdTipoIdentificacion(
                        rs.getInt("id_tipo_identificacion")
                );

                tipo.setNombreTipo(
                        rs.getString("nombre_tipo")
                );

                lista.add(tipo);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return lista;
    }
}
