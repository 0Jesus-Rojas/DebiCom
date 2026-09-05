package Servlet;

import Controlador.ReporteCompradorDAO;
import Modelo.dto.DeudaPendienteDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "DeudasPendientesServlet", urlPatterns = {"/comprador/deudas"})
public class DeudasPendientesServlet extends HttpServlet {
    private final ReporteCompradorDAO dao = new ReporteCompradorDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer idUsuario = AuthUtil.getIdUsuario(request);
        if (idUsuario == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        try {
            List<DeudaPendienteDTO> deudas = dao.listarDeudasPorUsuario(idUsuario);
            BigDecimal total = deudas.stream().map(DeudaPendienteDTO::getSaldoPendiente).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
            long vencidas = deudas.stream().filter(d -> d.getFechaVencimiento()!=null && d.getFechaVencimiento().isBefore(LocalDate.now())).count();
            request.setAttribute("deudas", deudas); request.setAttribute("deudaTotal", total); request.setAttribute("pagosAlDia", deudas.size()-vencidas); request.setAttribute("pagosVencidos", vencidas);
            request.getRequestDispatcher("/consultar-deudas.jsp").forward(request, response);
        } catch (RuntimeException e) { log("Error consultando deudas", e); response.sendError(500, "No fue posible consultar las deudas."); }
    }
}
