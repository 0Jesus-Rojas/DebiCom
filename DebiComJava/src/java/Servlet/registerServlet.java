package Servlet;

import Controlador.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener parámetros del formulario
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String tipoIdStr = request.getParameter("tipo_id");
        String numId = request.getParameter("num_id");
        String fechaNac = request.getParameter("fecha_nac");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");
        String autorizaStr = request.getParameter("autoriza");

        // Validar campos obligatorios
        if (nombre == null || apellido == null || tipoIdStr == null || numId == null ||
            fechaNac == null || correo == null || clave == null ||
            nombre.trim().isEmpty() || apellido.trim().isEmpty() || 
            correo.trim().isEmpty() || clave.trim().isEmpty()) {
            
            response.sendRedirect("index.html");
            return;
        }

        int idTipoIdentificacion = Integer.parseInt(tipoIdStr);
        int autorizaDatos = (autorizaStr != null && (autorizaStr.equals("true") || autorizaStr.equals("on"))) ? 1 : 0;

        Conexion conect = new Conexion();

        // Consulta SQL para insertar el nuevo usuario
        // Nota: id_usuario es AI PK, fecha_registro usa NOW(), y fecha_vencimiento_clave se asigna a 6 meses
        String querySql = "INSERT INTO usuarios ("
                + "nombre, apellido, identificacion, fecha_nacimiento, correo, telefono, "
                + "direccion, password, fecha_vencimiento_clave, autoriza_datos, "
                + "id_tipo_identificacion, fecha_registro"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATE_ADD(NOW(), INTERVAL 180 DAY), ?, ?, NOW())";

        try (Connection conn = conect.getconn();
             PreparedStatement ps = conn.prepareStatement(querySql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, numId);
            ps.setString(4, fechaNac); // Formato de input HTML date es YYYY-MM-DD
            ps.setString(5, correo);
            ps.setString(6, telefono);
            ps.setString(7, direccion);
            ps.setString(8, clave);
            ps.setInt(9, autorizaDatos);
            ps.setInt(10, idTipoIdentificacion);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Iniciar sesión tras registro exitoso
                request.getSession().setAttribute("correo", correo);
                response.sendRedirect("panel.jsp");
            } else {
                response.sendRedirect("index.html");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Revisa los logs de tu servidor para ver fallos
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al registrar el usuario en la base de datos.");
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
        return "Servlet para el registro de nuevos usuarios";
    }
}