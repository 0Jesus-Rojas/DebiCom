package Servlet;

import Controlador.VendedorDAO;
import Modelo.dto.ItemCreditoDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="ConsumirCreditoServlet", urlPatterns={"/vendedor/creditos/consumir"})
public class ConsumirCreditoServlet extends HttpServlet {
    private final VendedorDAO dao = new VendedorDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) {
            response.sendRedirect(request.getContextPath()+"/index.jsp");
            return;
        }
        if (!AuthUtil.isStaff(request)) {
            response.sendError(403, "No tiene permisos para administrar créditos.");
            return;
        }
        request.setCharacterEncoding("UTF-8");
        int idSolicitud = parseInt(request.getParameter("idSolicitud"));
        try {
            if (idSolicitud <= 0) throw new IllegalArgumentException("El crédito seleccionado no es válido.");
            String[] ids = request.getParameterValues("idProducto");
            List<ItemCreditoDTO> items = new ArrayList<>();
            if (ids != null) {
                for (String id : ids) {
                    int idProducto = parseInt(id);
                    int cantidad = parseInt(request.getParameter("cantidad_" + id));
                    if (idProducto > 0 && cantidad > 0) items.add(new ItemCreditoDTO(idProducto, cantidad));
                }
            }
            dao.consumirCredito(vendedor, idSolicitud, items);
            response.sendRedirect(request.getContextPath()+"/vendedor/creditos/detalle?id="+idSolicitud+"&ok=productos-agregados");
        } catch (IllegalArgumentException | SecurityException e) {
            response.sendRedirect(request.getContextPath()+"/vendedor/creditos/detalle?id="+idSolicitud+"&error="+java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            log("Error agregando productos al crédito", e);
            response.sendRedirect(request.getContextPath()+"/vendedor/creditos/detalle?id="+idSolicitud+"&error="+java.net.URLEncoder.encode("No fue posible agregar los productos al crédito.", java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception e) { return 0; }
    }
}
