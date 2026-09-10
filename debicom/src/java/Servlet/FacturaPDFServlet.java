package Servlet;

import Controlador.VentaDirectaDAO;
import Modelo.dto.FacturaPDFDTO;
import Servicio.FacturaPDFService;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/** Genera y descarga el PDF de una venta directa perteneciente al vendedor autenticado. */
@WebServlet(name = "FacturaPDFServlet", urlPatterns = {"/vendedor/ventas-directas/factura"})
public class FacturaPDFServlet extends HttpServlet {
    private final VentaDirectaDAO dao = new VentaDirectaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idVendedor = AuthUtil.getIdUsuario(request);
        if (idVendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (!AuthUtil.isStaff(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para descargar facturas.");
            return;
        }

        int idFactura = parseInt(request.getParameter("id"));
        if (idFactura <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Factura inválida.");
            return;
        }

        try {
            FacturaPDFDTO factura = dao.obtenerFacturaDirectaParaPDF(idFactura, idVendedor);
            if (factura == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "La factura no existe o no pertenece al vendedor autenticado.");
                return;
            }

            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + sanitizeFilename(factura.getNumeroFactura()) + ".pdf\"");
            response.setHeader("X-Content-Type-Options", "nosniff");

            try (OutputStream out = response.getOutputStream()) {
                FacturaPDFService.generar(factura, out);
            }
        } catch (RuntimeException e) {
            log("Error generando PDF de la factura " + idFactura, e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No fue posible generar la factura PDF.");
            }
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private String sanitizeFilename(String value) {
        if (value == null || value.isBlank()) return "factura-venta-directa";
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
