package Servlet;

import Controlador.VendedorDAO;
import Modelo.dto.ItemCreditoDTO;
import Modelo.dto.TiendaDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Flujo de venta a crédito directa desde el vendedor. */
@WebServlet(name="OtorgarCreditoServlet", urlPatterns={"/vendedor/creditos/otorgar"})
public class OtorgarCreditoServlet extends HttpServlet {
    private final VendedorDAO dao = new VendedorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(403,"No tiene permisos para otorgar créditos."); return; }
        cargarVista(request, response, vendedor, request.getParameter("error"), request.getParameter("ok"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(403,"No tiene permisos para otorgar créditos."); return; }
        request.setCharacterEncoding("UTF-8");
        try {
            int idTienda = parseInt(request.getParameter("idTienda"));
            int idCliente = parseInt(request.getParameter("idCliente"));
            LocalDate vencimiento = LocalDate.parse(request.getParameter("fechaVencimiento"));
            String[] ids = request.getParameterValues("idProducto");
            List<ItemCreditoDTO> items = new ArrayList<>();
            if (ids != null) {
                for (String id : ids) {
                    int idProducto = parseInt(id);
                    int cantidad = parseInt(request.getParameter("cantidad_"+id));
                    items.add(new ItemCreditoDTO(idProducto, cantidad));
                }
            }
            int idSolicitud = dao.otorgarCreditoDirecto(vendedor,idTienda,idCliente,vencimiento,request.getParameter("observaciones"),items);
            response.sendRedirect(request.getContextPath()+"/vendedor/creditos/detalle?id="+idSolicitud+"&ok=credito-creado");
        } catch (java.time.format.DateTimeParseException | NumberFormatException e) {
            cargarVista(request,response,vendedor,"La fecha, comprador, tienda y cantidades deben ser válidos.",null);
        } catch (SecurityException | IllegalArgumentException e) {
            cargarVista(request,response,vendedor,e.getMessage(),null);
        } catch (RuntimeException e) {
            log("Error otorgando crédito",e);
            cargarVista(request,response,vendedor,"No fue posible otorgar el crédito. Verifica los datos e inténtalo de nuevo.",null);
        }
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response, int vendedor, String error, String ok) throws ServletException, IOException {
        List<TiendaDTO> tiendas = dao.listarTiendasDelVendedor(vendedor);
        int idTienda = parseInt(request.getParameter("idTienda"));
        if (idTienda <= 0 && !tiendas.isEmpty()) idTienda = tiendas.get(0).getIdTienda();
        request.setAttribute("tiendas",tiendas);
        request.setAttribute("compradores",dao.listarCompradores());
        request.setAttribute("productosCredito",idTienda>0?dao.listarProductosParaCredito(idTienda,vendedor):List.of());
        request.setAttribute("idTienda",idTienda);
        request.setAttribute("error",error);
        request.setAttribute("ok",ok);
        request.getRequestDispatcher("/otorgar-credito.jsp").forward(request,response);
    }

    private int parseInt(String value) { try { return Integer.parseInt(value); } catch(Exception e) { return 0; } }
}
