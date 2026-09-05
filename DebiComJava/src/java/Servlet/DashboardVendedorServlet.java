package Servlet;

import Controlador.VendedorDAO;
import Modelo.dto.DashboardVendedorDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="DashboardVendedorServlet", urlPatterns={"/vendedor/dashboard"})
public class DashboardVendedorServlet extends HttpServlet {
    private final VendedorDAO dao = new VendedorDAO();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer idVendedor = AuthUtil.getIdUsuario(request);
        if (idVendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        try {
            DashboardVendedorDTO dto = dao.obtenerDashboard(idVendedor);
            request.setAttribute("dashboard", dto);
            request.setAttribute("solicitudesNuevas", dao.listarSolicitudes(idVendedor, "PENDIENTE"));
            request.setAttribute("tiendas", dao.listarTiendasDelVendedor(idVendedor));
            request.getRequestDispatcher("/panel.jsp").forward(request, response);
        } catch (RuntimeException e) { log("Error consultando dashboard",e); response.sendError(500,"No fue posible cargar el dashboard."); }
    }
}
