package Servlet;

import Controlador.UsuarioDAO;
import Modelo.Usuarios;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "loginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");

        // Validar que los parámetros no vengan nulos
        if (correo == null || clave == null || correo.trim().isEmpty() || clave.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            Usuarios usuario = usuarioDAO.autenticar(correo, clave);
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }

            var session = request.getSession(true);
            session.setAttribute("idUsuario", usuario.getIdUsuario());
            session.setAttribute("correo", usuario.getCorreo());
            session.setAttribute("nombre", usuario.getNombre());
            session.setAttribute("apellido", usuario.getApellido());
            session.setAttribute("idRol", usuario.getIdRol());

            Integer idCliente = new UsuarioDAO().obtenerIdClientePorUsuario(usuario.getIdUsuario());
            if (idCliente != null) session.setAttribute("idCliente", idCliente);

            if (usuario.getIdRol() == 1 || usuario.getIdRol() == 2 || usuario.getIdRol() == 3) {
                response.sendRedirect(request.getContextPath() + "/vendedor/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/comprador/estado-credito");
            }
        } catch (RuntimeException e) {
            log("Error de autenticación", e);
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