package Servlet;

import Controlador.VendedorDAO;
import Modelo.dto.PagoVendedorHistorialDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Historial de pagos recibidos por el vendedor sobre sus créditos.
 */
@WebServlet(name = "HistorialPagosVendedorServlet",
        urlPatterns = {"/vendedor/historial-pagos"})
public class HistorialPagosVendedorServlet extends HttpServlet {

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
            List<PagoVendedorHistorialDTO> pagos =
                    dao.listarHistorialPagos(idVendedor);

            BigDecimal total = pagos.stream()
                    .map(PagoVendedorHistorialDTO::getMontoPagado)
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            request.setAttribute("pagosVendedor", pagos);
            request.setAttribute("totalPagado", total);

            request.getRequestDispatcher("/historial-pagos-vendedor.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            log("Error consultando historial de pagos del vendedor", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar el historial de pagos.");
        }
    }
}
