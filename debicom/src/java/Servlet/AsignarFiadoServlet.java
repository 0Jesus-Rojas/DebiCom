package Servlet;

import Controlador.FiadoDAO;
import Controlador.VendedorDAO;
import Modelo.dto.CompradorDTO;
import Modelo.dto.ItemCreditoDTO;
import Modelo.dto.ProductoDTO;
import Modelo.dto.SolicitudCreditoDTO;
import Modelo.dto.TiendaDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="AsignarFiadoServlet", urlPatterns={"/vendedor/fiados/asignar"})
public class AsignarFiadoServlet extends HttpServlet {

    private final VendedorDAO vendedorDAO = new VendedorDAO();
    private final FiadoDAO fiadoDAO = new FiadoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(403,"No tiene permisos para administrar fiados."); return; }
        cargarVista(request, response, vendedor, request.getParameter("error"), request.getParameter("ok"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(403,"No tiene permisos para administrar fiados."); return; }
        request.setCharacterEncoding("UTF-8");

        try {
            int idTienda = parseInt(request.getParameter("idTienda"));
            int idCliente = parseInt(request.getParameter("idCliente"));
            LocalDate fecha = LocalDate.parse(request.getParameter("fechaVencimiento"));

            String[] ids = request.getParameterValues("idProducto");
            List<ItemCreditoDTO> items = new ArrayList<>();
            if (ids != null) {
                for (String id : ids) {
                    int idProducto = parseInt(id);
                    int cantidad = parseInt(request.getParameter("cantidad_"+id));
                    if (idProducto > 0 && cantidad > 0) {
                        items.add(new ItemCreditoDTO(idProducto, cantidad));
                    }
                }
            }

            int idFiado = fiadoDAO.asignarFiado(vendedor, idTienda, idCliente, fecha,
                    request.getParameter("observaciones"), items);

            response.sendRedirect(request.getContextPath()
                    + "/vendedor/fiados/asignar?ok=fiado-creado&id=" + idFiado);
        } catch (java.time.format.DateTimeParseException | NumberFormatException e) {
            cargarVista(request, response, vendedor, "El comprador, fecha y cantidades deben ser válidos.", null);
        } catch (SecurityException | IllegalArgumentException e) {
            cargarVista(request, response, vendedor, e.getMessage(), null);
        } catch (RuntimeException e) {
            log("Error asignando fiado", e);
            cargarVista(request, response, vendedor, "No fue posible asignar el fiado.", null);
        }
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response,
            int vendedor, String error, String ok) throws ServletException, IOException {
        List<TiendaDTO> tiendas = vendedorDAO.listarTiendasDelVendedor(vendedor);
        int idTienda = parseInt(request.getParameter("idTienda"));
        if (idTienda <= 0 && !tiendas.isEmpty()) idTienda = tiendas.get(0).getIdTienda();

        request.setAttribute("tiendas", tiendas);
        request.setAttribute("compradores", vendedorDAO.listarCompradores());
        request.setAttribute("productosFiado", idTienda > 0
                ? fiadoDAO.listarProductos(idTienda, vendedor)
                : List.<ProductoDTO>of());
        request.setAttribute("fiadosPendientes", fiadoDAO.listarFiadosPendientes(vendedor));
        request.setAttribute("idTienda", idTienda);
        request.setAttribute("error", error);
        request.setAttribute("ok", ok);
        request.setAttribute("idFiado", request.getParameter("id"));
        request.getRequestDispatcher("/asignar-fiado.jsp").forward(request, response);
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception e) { return 0; }
    }
}
