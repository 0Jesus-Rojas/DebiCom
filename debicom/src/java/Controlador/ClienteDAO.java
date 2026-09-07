/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class ClienteDAO {
    private Conexion conect = new Conexion();

    public Cliente consultarCliente(int idCliente) {
        Connection conn = conect.getconn();
        Cliente miCliente = null;
        
        try {
            String querySql = "SELECT credito_actual, id_usuario FROM clientes WHERE id_cliente = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idCliente);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miCliente = new Cliente();
                miCliente.setIdCliente(idCliente);
                miCliente.setCreditoActual(rs.getFloat("credito_actual"));
                miCliente.setIdUsuario(rs.getInt("id_usuario"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return miCliente;
        }
        return miCliente;
    }

    public boolean insertarCliente(Cliente miCliente) {
        boolean insertar = false;
        Connection conn = conect.getconn();
        try {
            String querySql = "INSERT INTO clientes(credito_actual, id_usuario) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setFloat(1, miCliente.getCreditoActual());
            ps.setInt(2, miCliente.getIdUsuario());
            
            ps.executeUpdate();
            insertar = true;
            System.out.println("Cliente registrado");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarCliente(Cliente miCliente) {
        boolean actualizar = false;
        Connection conn = conect.getconn();
        
        try {
            String querySql = "UPDATE clientes SET credito_actual = ?, id_usuario = ? WHERE id_cliente = ?";
            PreparedStatement ps = conn.prepareStatement(querySql);
            
            ps.setFloat(1, miCliente.getCreditoActual());
            ps.setInt(2, miCliente.getIdUsuario());
            ps.setInt(3, miCliente.getIdCliente());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                actualizar = true;
            } else {
                System.out.println("No se encontró el cliente");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarCliente(int idCliente) {
        boolean eliminar = false;
        
        String querySql = "DELETE FROM clientes WHERE id_cliente = ?";
        Connection conn = conect.getconn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySql);
            ps.setInt(1, idCliente);
            
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