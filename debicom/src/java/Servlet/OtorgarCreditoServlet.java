package Servlet;

import Controlador.VendedorDAO;
import Modelo.dto.TiendaDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name="OtorgarCreditoServlet", urlPatterns={"/vendedor/creditos/otorgar"})
public class OtorgarCreditoServlet extends HttpServlet {
    private final VendedorDAO dao = new VendedorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(403,"No tiene permisos para otorgar créditos."); return; }
        cargarVista(request, response, vendedor, request.getParameter("error"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(403,"No tiene permisos para otorgar créditos."); return; }
        request.setCharacterEncoding("UTF-8");

        try {
            int idTienda = parseInt(request.getParameter("idTienda"));
            int idCliente = parseInt(request.getParameter("idCliente"));
            java.math.BigDecimal cupo = new java.math.BigDecimal(request.getParameter("cupo").trim());
            LocalDate vencimiento = LocalDate.parse(request.getParameter("fechaVencimiento"));

            LocalDate hoy = LocalDate.now();
            if (vencimiento.isBefore(hoy.plusDays(15)) || vencimiento.isAfter(hoy.plusMonths(1))) {
                throw new IllegalArgumentException("El plazo del crédito debe estar entre 15 días y 1 mes.");
            }
            if (cupo.signum() <= 0) {
                throw new IllegalArgumentException("El cupo debe ser mayor que cero.");
            }

            int idSolicitud = dao.asignarCreditoCupo(
                    vendedor, idTienda, idCliente, cupo, vencimiento, request.getParameter("observaciones"));

            response.sendRedirect(request.getContextPath()
                    + "/vendedor/creditos/detalle?id=" + idSolicitud + "&ok=credito-creado");
        } catch (java.time.format.DateTimeParseException | NumberFormatException e) {
            cargarVista(request, response, vendedor, "El comprador, cupo y fecha deben ser válidos.");
        } catch (SecurityException | IllegalArgumentException e) {
            cargarVista(request, response, vendedor, e.getMessage());
        } catch (RuntimeException e) {
            log("Error asignando cupo de crédito", e);
            cargarVista(request, response, vendedor, "No fue posible asignar el cupo de crédito.");
        }
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response,
            int vendedor, String error) throws ServletException, IOException {
        List<TiendaDTO> tiendas = dao.listarTiendasDelVendedor(vendedor);
        int idTienda = parseInt(request.getParameter("idTienda"));
        if (idTienda <= 0 && !tiendas.isEmpty()) idTienda = tiendas.get(0).getIdTienda();

        request.setAttribute("tiendas", tiendas);
        request.setAttribute("compradores", dao.listarCompradores());
        request.setAttribute("idTienda", idTienda);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/otorgar-credito.jsp").forward(request, response);
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception e) { return 0; }
    }
}
