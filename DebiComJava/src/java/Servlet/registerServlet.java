package Servlet;

import Controlador.UsuarioDAO;
import Modelo.Usuarios;
import java.io.IOException;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String tipo = request.getParameter("tipo_id");
        String identificacion = request.getParameter("num_id");
        String fecha = request.getParameter("fecha_nac");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");

        boolean autoriza = "true".equalsIgnoreCase(request.getParameter("autoriza"))
                || "on".equalsIgnoreCase(request.getParameter("autoriza"));

        if (blank(nombre) || blank(apellido) || blank(tipo) || blank(identificacion)
                || blank(fecha) || blank(correo) || blank(clave)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Todos los campos obligatorios deben estar diligenciados.");
            return;
        }

        final int idTipo;
        final Date fechaNacimiento;

        try {
            idTipo = Integer.parseInt(tipo);
            fechaNacimiento = Date.valueOf(fecha);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Tipo de identificación o fecha de nacimiento inválidos.");
            return;
        }

        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setIdentificacion(identificacion.trim());
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono == null ? "" : telefono.trim());
        usuario.setDireccion(direccion == null ? "" : direccion.trim());
        usuario.setPassword(clave);
        usuario.setAutorizaDatos(autoriza);
        usuario.setIdTipoIdentificacion(idTipo);
        usuario.setIdRol(0);

        try {
            /*
             * UsuarioDAO.registrarUsuarioComoComprador() crea usuarios y clientes
             * dentro de la misma transacción. Si falla clientes, usuarios se revierte.
             */
            int idUsuario = usuarioDAO.registrarUsuarioComoComprador(usuario);

            request.getSession(true).setAttribute("idUsuario", idUsuario);
            request.getSession().setAttribute("correo", usuario.getCorreo());
            request.getSession().setAttribute("nombre", usuario.getNombre());
            request.getSession().setAttribute("apellido", usuario.getApellido());
            request.getSession().setAttribute("idRol", 0);
            request.getSession().setAttribute("idCliente",
                    usuarioDAO.obtenerIdClientePorUsuario(idUsuario));

            response.sendRedirect(request.getContextPath() + "/comprador/estado-credito");

        } catch (RuntimeException e) {
            log("Error registrando usuario/comprador", e);
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "No fue posible registrar el usuario. Verifique que el correo y la identificación no estén registrados.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
