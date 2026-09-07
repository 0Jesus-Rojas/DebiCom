package Servlet;

import Controlador.VendedorDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Pantalla de administración para aprobar o rechazar solicitudes.
 * Esta URL siempre trabaja únicamente con solicitudes PENDIENTES.
 */
@WebServlet(name = "SolicitudesCreditoVendedorServlet",
        urlPatterns = {"/vendedor/creditos"})
public class SolicitudesCreditoVendedorServlet extends HttpServlet {

    private final VendedorDAO dao = new VendedorDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Integer idVendedor = AuthUtil.getIdUsuario(request);

        if (!AuthUtil.isStaff(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder a la administración.");
            return;
        }

        if (idVendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // El filtro PENDIENTE se aplica dentro del DAO y no depende
            // de ningún parámetro enviado por el navegador.
            request.setAttribute(
                    "solicitudes",
                    dao.listarSolicitudesPendientes(idVendedor));

            request.getRequestDispatcher("/aprobar-creditos.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            log("Error listando solicitudes pendientes", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible cargar las solicitudes pendientes.");
        }
    }
}
