package Servlet;

import Modelo.dto.TiendaDTO;
import Servicio.TiendaService;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TiendaServlet", urlPatterns = {"/vendedor/tienda"})
public class TiendaServlet extends HttpServlet {

    private final TiendaService service = new TiendaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer vendedor = AuthUtil.getIdUsuario(request);
        if (vendedor == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (!AuthUtil.isStaff(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para administrar tiendas.");
            return;
        }

        try {
            cargarVista(request, vendedor);
            request.getRequestDispatcher("/gestionar-tienda.jsp").forward(request, response);
        } catch (RuntimeException e) {
            log("Error gestionando tiendas", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible cargar las tiendas.");
        }
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
                    "No tiene permisos para administrar tiendas.");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("crear".equalsIgnoreCase(action)) {
                int idTienda = service.crearTienda(
                        vendedor,
                        request.getParameter("nombreTienda"),
                        request.getParameter("nit"),
                        request.getParameter("direccion"),
                        request.getParameter("telefono"));

                if (idTienda <= 0) {
                    throw new IllegalStateException("No se generó el identificador de la tienda.");
                }

                response.sendRedirect(request.getContextPath()
                        + "/vendedor/tienda?idTienda=" + idTienda + "&ok=creada");
                return;
            }

            int idTienda = parseId(request.getParameter("idTienda"));
            if (idTienda <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tienda inválida.");
                return;
            }

            service.actualizarTienda(
                    vendedor,
                    idTienda,
                    request.getParameter("nombreTienda"),
                    request.getParameter("nit"),
                    request.getParameter("direccion"),
                    request.getParameter("telefono"));

            response.sendRedirect(request.getContextPath()
                    + "/vendedor/tienda?idTienda=" + idTienda + "&ok=actualizada");

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            cargarVista(request, vendedor);
            request.getRequestDispatcher("/gestionar-tienda.jsp").forward(request, response);
        } catch (RuntimeException e) {
            log("Error procesando tienda", e);
            request.setAttribute("error",
                    "No fue posible guardar la tienda. Verifica que el NIT no esté registrado.");
            cargarVista(request, vendedor);
            request.getRequestDispatcher("/gestionar-tienda.jsp").forward(request, response);
        }
    }

    private void cargarVista(HttpServletRequest request, int vendedor) {
        List<TiendaDTO> tiendas = service.listarTiendas(vendedor);
        int idTienda = parseId(request.getParameter("idTienda"));

        if (idTienda <= 0 && !tiendas.isEmpty()) {
            idTienda = tiendas.get(0).getIdTienda();
        }

        TiendaDTO tienda = idTienda > 0
                ? service.obtenerTienda(idTienda, vendedor)
                : null;

        request.setAttribute("tiendas", tiendas);
        request.setAttribute("tienda", tienda);
        request.setAttribute("idTiendaSeleccionada", idTienda);
        request.setAttribute("ok", request.getParameter("ok"));
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
