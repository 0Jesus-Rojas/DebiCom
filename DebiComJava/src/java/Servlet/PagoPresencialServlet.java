package Servlet;

import Controlador.PagoDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Registra pagos presenciales realizados por un vendedor. */
@WebServlet(name = "PagoPresencialServlet", urlPatterns = {"/vendedor/pagos/registrar"})
public class PagoPresencialServlet extends HttpServlet {

    private final PagoDAO pagoDAO = new PagoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idVendedor = AuthUtil.getIdUsuario(request);
        Integer idRol = AuthUtil.getIdRol(request);

        if (idVendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if (idRol == null || idRol != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo un usuario con rol vendedor puede registrar pagos.");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try {
            int idCliente = Integer.parseInt(request.getParameter("idCliente"));
            BigDecimal monto = new BigDecimal(request.getParameter("monto"));

            pagoDAO.registrarPagoPresencial(idCliente, idVendedor, monto);

            response.sendRedirect(request.getContextPath()
                    + "/vendedor/creditos-pendientes?ok=pago");

        } catch (NumberFormatException e) {
            redirigirError(request, response,
                    "El ID del comprador y el monto deben ser válidos.");
        } catch (IllegalArgumentException e) {
            redirigirError(request, response, e.getMessage());
        } catch (RuntimeException e) {
            log("Error registrando pago presencial", e);
            redirigirError(request, response,
                    "No fue posible registrar el pago presencial.");
        }
    }

    private void redirigirError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        response.sendRedirect(request.getContextPath()
                + "/vendedor/creditos-pendientes?error="
                + URLEncoder.encode(mensaje == null ? "Error desconocido" : mensaje, StandardCharsets.UTF_8));
    }
}
