package Servlet;

import Controlador.UsuarioDAO;
import Modelo.Usuarios;
import Util.PasswordUtil;
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

        if (correo == null || correo.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
            request.setAttribute("authTab", "login");
            request.setAttribute("authMessage", "Debes ingresar el correo y la contraseña.");
            request.setAttribute("authType", "warning");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        correo = correo.trim();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            Usuarios usuario = usuarioDAO.buscarPorCorreo(correo);

            if (usuario == null) {
                request.setAttribute("authTab", "login");
                request.setAttribute("authMessage", "No existe un usuario registrado con ese correo.");
                request.setAttribute("authType", "error");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            String passwordGuardada = usuario.getPassword();
            boolean passwordCorrecta;

            if (PasswordUtil.isHashed(passwordGuardada)) {
                // Usuario ya migrado al formato seguro.
                passwordCorrecta = PasswordUtil.verify(clave, passwordGuardada);
            } else {
                // Compatibilidad temporal con instalaciones antiguas.
                passwordCorrecta = java.util.Objects.equals(passwordGuardada, clave);

                if (passwordCorrecta) {
                    // Migración automática al primer inicio de sesión correcto.
                    usuarioDAO.actualizarPassword(
                            usuario.getIdUsuario(),
                            PasswordUtil.hash(clave));
                }
            }

            if (!passwordCorrecta) {
                request.setAttribute("authTab", "login");
                request.setAttribute("authMessage", "La contraseña ingresada es incorrecta.");
                request.setAttribute("authType", "error");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
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
            request.setAttribute("authTab", "login");
            request.setAttribute("authMessage",
                    "No fue posible consultar la base de datos. Intenta nuevamente.");
            request.setAttribute("authType", "error");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
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