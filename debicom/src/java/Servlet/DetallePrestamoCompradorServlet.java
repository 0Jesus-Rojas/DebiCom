package Servlet;

import Controlador.SolicitudCreditoDAO;
import Controlador.UsuarioDAO;
import Modelo.dto.SolicitudCreditoDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DetallePrestamoCompradorServlet", urlPatterns = {"/comprador/estado-credito/detalle"})
public class DetallePrestamoCompradorServlet extends HttpServlet {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SolicitudCreditoDAO solicitudDAO = new SolicitudCreditoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idUsuario = AuthUtil.getIdUsuario(request);
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        int id = parse(request.getParameter("id"));
        if (id <= 0) {
            response.sendError(400, "ID de préstamo inválido.");
            return;
        }
        try {
            Integer idCliente = usuarioDAO.obtenerIdClientePorUsuario(idUsuario);
            if (idCliente == null) {
                response.sendError(404, "El usuario no tiene perfil de cliente.");
                return;
            }
            SolicitudCreditoDTO prestamo = solicitudDAO.consultarPorIdYCliente(id, idCliente);
            if (prestamo == null) {
                response.sendError(404, "Préstamo no encontrado.");
                return;
            }
            request.setAttribute("prestamo", prestamo);
            request.setAttribute("productosPrestamo", solicitudDAO.listarProductosSolicitud(id));
            request.getRequestDispatcher("/detalle-prestamo.jsp").forward(request, response);
        } catch (RuntimeException e) {
            log("Error consultando detalle del préstamo del comprador", e);
            response.sendError(500,
                    "No fue posible consultar el detalle del préstamo.");
        }
    }

    private int parse(String value) {
        try { return Integer.parseInt(value); }
        catch (Exception e) { return 0; }
    }
}
