package Servlet;

import Controlador.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "loginServlet", urlPatterns = {"/login"})
public class loginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");

        // Validar que los parámetros no vengan nulos
        if (correo == null || clave == null || correo.trim().isEmpty() || clave.trim().isEmpty()) {
            response.sendRedirect("index.html");
            return;
        }

        Conexion conect = new Conexion();
        String querySql = "SELECT * FROM usuarios WHERE correo = ? AND password = ?";

        try (Connection conn = conect.getconn();
             PreparedStatement ps = conn.prepareStatement(querySql)) {
            
            ps.setString(1, correo);
            ps.setString(2, clave);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");

                    request.getSession().setAttribute("correo", correo);
                    request.getSession().setAttribute("nombre", nombre);
                    request.getSession().setAttribute("apellido", apellido);

                    response.sendRedirect("panel.jsp");
                } else {
                    response.sendRedirect("index.html");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Revisa los logs de tu servidor para ver fallos
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet de autenticación de usuarios";
    }
}