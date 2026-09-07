package Servlet;

import Controlador.UsuarioDAO;
import Modelo.dto.PerfilDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@WebServlet(name = "PerfilServlet", urlPatterns = {"/comprador/perfil"})
public class PerfilServlet extends HttpServlet {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idUsuario = obtenerIdUsuario(request.getSession(false));
        if (idUsuario == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        PerfilDTO perfil = usuarioDAO.consultarPerfil(idUsuario);
        if (perfil == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
            return;
        }

        request.setAttribute("perfil", perfil);
        request.setAttribute("puedeRegistrarseVendedor",
                usuarioDAO.puedeRegistrarseComoVendedor(idUsuario));
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idUsuario = obtenerIdUsuario(request.getSession(false));
        if (idUsuario == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        PerfilDTO perfil = new PerfilDTO();
        perfil.setIdUsuario(idUsuario);
        perfil.setNombre(request.getParameter("nombre"));
        perfil.setApellido(request.getParameter("apellido"));
        perfil.setIdentificacion(request.getParameter("identificacion"));
        perfil.setCorreo(request.getParameter("correo"));
        perfil.setTelefono(request.getParameter("telefono"));
        perfil.setDireccion(request.getParameter("direccion"));
        perfil.setAutorizaDatos("on".equalsIgnoreCase(request.getParameter("autorizaDatos"))
                || "true".equalsIgnoreCase(request.getParameter("autorizaDatos"))
                || "1".equals(request.getParameter("autorizaDatos")));

        String fecha = request.getParameter("fechaNacimiento");
        if (fecha != null && !fecha.isBlank()) {
            try {
                perfil.setFechaNacimiento(LocalDate.parse(fecha));
            } catch (java.time.format.DateTimeParseException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fecha de nacimiento inválida");
                return;
            }
        }

        if (!datosValidos(perfil)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de perfil incompletos");
            return;
        }

        try {
            if (!usuarioDAO.actualizarPerfil(perfil)) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "No se pudo actualizar el perfil");
                return;
            }
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error actualizando el perfil");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("correo", perfil.getCorreo());
        session.setAttribute("nombre", perfil.getNombre());
        session.setAttribute("apellido", perfil.getApellido());

        response.sendRedirect(request.getContextPath() + "/comprador/perfil");
    }

    private Integer obtenerIdUsuario(HttpSession session) {
        if (session == null) return null;

        Object id = session.getAttribute("idUsuario");
        if (id instanceof Integer i) return i;
        if (id instanceof Long l) return l.intValue();
        if (id instanceof String s) {
            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private boolean datosValidos(PerfilDTO p) {
        return noVacio(p.getNombre())
                && noVacio(p.getApellido())
                && noVacio(p.getIdentificacion())
                && noVacio(p.getCorreo())
                && noVacio(p.getTelefono())
                && noVacio(p.getDireccion())
                && p.getFechaNacimiento() != null;
    }

    private boolean noVacio(String value) {
        return value != null && !value.isBlank();
    }
}
