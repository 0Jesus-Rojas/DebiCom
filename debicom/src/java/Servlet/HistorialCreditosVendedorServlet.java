package Servlet;

import Controlador.VendedorDAO;
import Modelo.dto.SolicitudCreditoDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Historial de créditos efectivamente otorgados por el vendedor autenticado.
 */
@WebServlet(name = "HistorialCreditosVendedorServlet",
        urlPatterns = {"/vendedor/historial-creditos"})
public class HistorialCreditosVendedorServlet extends HttpServlet {

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
            List<SolicitudCreditoDTO> historial =
                    dao.listarHistorialCreditos(idVendedor);

            request.setAttribute("historialCreditos", historial);
            request.getRequestDispatcher("/historial-creditos.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            log("Error consultando historial de créditos", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar el historial de créditos.");
        }
    }
}
