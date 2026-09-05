package Servlet;

import Controlador.ReporteCompradorDAO;
import Modelo.dto.PagoHistorialDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "HistorialPagosServlet", urlPatterns = {"/comprador/historial-pagos"})
public class HistorialPagosServlet extends HttpServlet {
    private final ReporteCompradorDAO dao = new ReporteCompradorDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer idUsuario = AuthUtil.getIdUsuario(request);
        if (idUsuario == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        try {
            List<PagoHistorialDTO> pagos = dao.listarPagosPorUsuario(idUsuario);
            BigDecimal total = pagos.stream().map(PagoHistorialDTO::getMontoPagado).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
            request.setAttribute("pagos", pagos); request.setAttribute("totalPagado", total);
            request.getRequestDispatcher("/historial-pagos.jsp").forward(request, response);
        } catch (RuntimeException e) { log("Error consultando historial", e); response.sendError(500, "No fue posible consultar el historial."); }
    }
}
