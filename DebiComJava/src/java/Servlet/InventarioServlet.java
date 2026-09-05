package Servlet;

import Modelo.EstadosProducto;
import Modelo.Unidades;
import Controlador.VendedorDAO;
import Modelo.dto.TiendaDTO;
import Servicio.InventarioService;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "InventarioServlet", urlPatterns = {"/vendedor/inventario"})
public class InventarioServlet extends HttpServlet {

    private final InventarioService service = new InventarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrarVista(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (!AuthUtil.isStaff(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para administrar inventario.");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        try {
            int idTienda = parse(request.getParameter("idTienda"));

            if ("actualizar".equalsIgnoreCase(accion)) {
                int idProducto = parse(request.getParameter("idProducto"));
                BigDecimal precio = new BigDecimal(request.getParameter("precio"));
                int stock = Integer.parseInt(request.getParameter("stock"));
                int idUnidad = Integer.parseInt(request.getParameter("idUnidad"));
                int idEstado = Integer.parseInt(request.getParameter("idEstadoProducto"));

                service.actualizarProducto(
                        vendedor, idProducto, idTienda,
                        request.getParameter("nombre"),
                        request.getParameter("descripcion"),
                        precio, stock, idUnidad, idEstado);

                response.sendRedirect(request.getContextPath()
                        + "/vendedor/inventario?idTienda=" + idTienda + "&ok=actualizado");
                return;
            }

            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            int idUnidad = Integer.parseInt(request.getParameter("idUnidad"));
            int idEstado = Integer.parseInt(request.getParameter("idEstadoProducto"));

            service.crearProducto(
                    vendedor, idTienda,
                    request.getParameter("nombre"),
                    request.getParameter("descripcion"),
                    precio, stock, idUnidad, idEstado);

            response.sendRedirect(request.getContextPath()
                    + "/vendedor/inventario?idTienda=" + idTienda + "&ok=producto");

        } catch (NumberFormatException e) {
            mostrarVista(request, response,
                    "Precio, stock, unidad, estado y producto deben ser valores válidos.");
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarVista(request, response, e.getMessage());
        } catch (RuntimeException e) {
            log("Error procesando producto", e);
            mostrarVista(request, response,
                    "No fue posible procesar el producto. Verifica los datos ingresados.");
        }
    }

    private void mostrarVista(HttpServletRequest request, HttpServletResponse response,
                              String error) throws ServletException, IOException {

        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (!AuthUtil.isStaff(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder al inventario.");
            return;
        }

        int idTienda = parse(request.getParameter("idTienda"));
        String search = request.getParameter("search");

        try {
            List<TiendaDTO> tiendas = service.listarTiendas(vendedor);

            if (idTienda <= 0 && !tiendas.isEmpty()) {
                idTienda = tiendas.get(0).getIdTienda();
            }

            request.setAttribute("tiendas", tiendas);
            request.setAttribute("idTienda", idTienda);
            request.setAttribute("search", search);
            request.setAttribute("unidades", service.listarUnidades());
            request.setAttribute("estadosProducto", service.listarEstadosProducto());

            int editarId = parse(request.getParameter("editarId"));
            if (editarId > 0) {
                Modelo.Productos productoEditar = new Controlador.ProductoDAO().consultarProducto(editarId);
                if (productoEditar == null) {
                    request.setAttribute("error", "El producto seleccionado no existe.");
                } else if (productoEditar.getIdTienda() != idTienda) {
                    request.setAttribute("error", "El producto no pertenece a la tienda seleccionada.");
                } else {
                    request.setAttribute("productoEditar", productoEditar);
                }
            }

            if (error != null) {
                request.setAttribute("error", error);
            } else {
                request.setAttribute("ok", request.getParameter("ok"));
            }

            if (idTienda > 0) {
                VendedorDAO.InventarioData data =
                        service.obtenerInventario(idTienda, vendedor, search);
                request.setAttribute("metricasInventario", data);
                request.setAttribute("productos", data.productos);
            } else {
                request.setAttribute("error",
                        "El vendedor no tiene una tienda registrada.");
            }

            request.getRequestDispatcher("/inventario.jsp").forward(request, response);

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (RuntimeException e) {
            log("Error consultando inventario", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible cargar el inventario.");
        }
    }

    private int parse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
