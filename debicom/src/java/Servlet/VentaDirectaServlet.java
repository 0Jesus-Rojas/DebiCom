package Servlet;

import Controlador.VendedorDAO;
import Controlador.VentaDirectaDAO;
import Modelo.TipoPago;
import Modelo.dto.CompradorDTO;
import Modelo.dto.ItemVentaDirectaDTO;
import Modelo.dto.ProductoDTO;
import Modelo.dto.TiendaDTO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Alta de ventas directas de contado para vendedores. */
@WebServlet(name = "VentaDirectaServlet", urlPatterns = {"/vendedor/ventas-directas"})
public class VentaDirectaServlet extends HttpServlet {
    private final VendedorDAO vendedorDAO = new VendedorDAO();
    private final VentaDirectaDAO ventaDAO = new VentaDirectaDAO();

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
                    "No tiene permisos para registrar ventas directas.");
            return;
        }

        String error = request.getParameter("error");
        String ok = request.getParameter("ok");
        int idTienda = parseInt(request.getParameter("idTienda"));
        int idFactura = parseInt(request.getParameter("idFactura"));

        cargarVista(request, response, idVendedor, idTienda, idFactura, error, ok);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idVendedor = AuthUtil.getIdUsuario(request);
        if (idVendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (!AuthUtil.isStaff(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para registrar ventas directas.");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        int idTienda = parseInt(request.getParameter("idTienda"));
        int idCliente = parseInt(request.getParameter("idCliente"));
        int idTipoPago = parseInt(request.getParameter("idTipoPago"));
        String[] ids = request.getParameterValues("idProducto");

        try {
            List<ItemVentaDirectaDTO> items = new ArrayList<>();
            if (ids != null) {
                for (String id : ids) {
                    int idProducto = parseInt(id);
                    int cantidad = parseInt(request.getParameter("cantidad_" + id));
                    items.add(new ItemVentaDirectaDTO(idProducto, cantidad));
                }
            }

            int idFactura = ventaDAO.registrarVentaDirecta(
                    idVendedor,
                    idTienda,
                    idCliente,
                    idTipoPago,
                    request.getParameter("observaciones"),
                    items);

            response.sendRedirect(request.getContextPath()
                    + "/vendedor/ventas-directas?ok=venta-registrada&idFactura=" + idFactura
                    + "&idTienda=" + idTienda);
        } catch (NumberFormatException | ArithmeticException e) {
            cargarVista(request, response, idVendedor, idTienda, 0,
                    "Los identificadores y cantidades deben ser válidos.", null);
        } catch (SecurityException | IllegalArgumentException e) {
            cargarVista(request, response, idVendedor, idTienda, 0,
                    e.getMessage(), null);
        } catch (RuntimeException e) {
            log("Error registrando venta directa", e);
            cargarVista(request, response, idVendedor, idTienda, 0,
                    "No fue posible registrar la venta directa. Verifica los datos e inténtalo de nuevo.", null);
        }
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response,
                             int idVendedor, int idTienda, int idFactura,
                             String error, String ok) throws ServletException, IOException {
        List<TiendaDTO> tiendas = vendedorDAO.listarTiendasDelVendedor(idVendedor);
        if (idTienda <= 0 && !tiendas.isEmpty()) {
            idTienda = tiendas.get(0).getIdTienda();
        }
        boolean tiendaValida = false;
        for (TiendaDTO tienda : tiendas) {
            if (tienda.getIdTienda() == idTienda) {
                tiendaValida = true;
                break;
            }
        }
        if (idTienda > 0 && !tiendaValida) {
            idTienda = tiendas.isEmpty() ? 0 : tiendas.get(0).getIdTienda();
        }

        List<ProductoDTO> productos = idTienda > 0
                ? ventaDAO.listarProductosParaVenta(idTienda, idVendedor)
                : List.of();
        List<CompradorDTO> compradores = vendedorDAO.listarCompradores();
        List<TipoPago> tiposPago = ventaDAO.listarTiposPago();

        request.setAttribute("tiendas", tiendas);
        request.setAttribute("productosVenta", productos);
        request.setAttribute("compradores", compradores);
        request.setAttribute("tiposPago", tiposPago);
        request.setAttribute("idTienda", idTienda);
        request.setAttribute("idFactura", idFactura);
        request.setAttribute("error", error);
        request.setAttribute("ok", ok);
        request.getRequestDispatcher("/ventas-directas.jsp").forward(request, response);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
