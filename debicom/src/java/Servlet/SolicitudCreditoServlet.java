package Servlet;

import Controlador.UsuarioDAO;
import Controlador.SolicitudCreditoDAO;
import Controlador.TiendaDAO;
import Modelo.dto.TiendaDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador MVC para solicitar crédito.
 *
 * GET: muestra el formulario con las tiendas.
 * POST: registra la solicitud en estado PENDIENTE.
 */
@WebServlet(name = "SolicitudCreditoServlet", urlPatterns = {"/comprador/solicitar-credito"})
public class SolicitudCreditoServlet extends HttpServlet {

    private final SolicitudCreditoDAO solicitudDAO = new SolicitudCreditoDAO();
    private final TiendaDAO tiendaDAO = new TiendaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUsuario = getIdUsuario(request);
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            Integer idCliente = buscarIdCliente(idUsuario);
            if (idCliente == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "El usuario autenticado no tiene un perfil de comprador.");
                return;
            }

            cargarFormulario(request, idCliente);
            request.getRequestDispatcher("/solicitar-credito.jsp").forward(request, response);
        } catch (RuntimeException e) {
            log("Error al preparar la solicitud de crédito", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible preparar el formulario de crédito.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUsuario = getIdUsuario(request);
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            Integer idCliente = buscarIdCliente(idUsuario);
            if (idCliente == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "El usuario autenticado no tiene un perfil de comprador.");
                return;
            }

            int idTienda = parsePositiveInt(request.getParameter("idTienda"), "La tienda es obligatoria.");
            BigDecimal monto = parseMonto(request.getParameter("monto"));
            String observaciones = trim(request.getParameter("descripcion"));
            if (observaciones == null) {
                // Permitimos también "observaciones" por si el frontend usa ese nombre.
                observaciones = trim(request.getParameter("observaciones"));
            }

            LocalDate fechaVencimiento = parseFechaVencimiento(request);

            int idSolicitud = solicitudDAO.registrarSolicitud(
                    idCliente,
                    idTienda,
                    monto,
                    fechaVencimiento,
                    observaciones);

            response.sendRedirect(request.getContextPath()
                    + "/comprador/solicitar-credito?ok=1&id=" + idSolicitud);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());

            try {
                Integer idCliente = buscarIdCliente(idUsuario);
                cargarFormulario(request, idCliente == null ? 0 : idCliente);
            } catch (RuntimeException ignored) {
                // Conservamos el mensaje de validación original.
            }

            request.getRequestDispatcher("/solicitar-credito.jsp").forward(request, response);

        } catch (RuntimeException e) {
            log("Error al registrar solicitud de crédito", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible registrar la solicitud de crédito.");
        }
    }

    private void cargarFormulario(HttpServletRequest request, int idCliente) {
        List<TiendaDTO> tiendas = tiendaDAO.listarTiendasDisponibles();
        request.setAttribute("tiendas", tiendas);
        request.setAttribute("idCliente", idCliente);

        String ok = request.getParameter("ok");
        if ("1".equals(ok)) {
            request.setAttribute("mensaje", "Solicitud registrada correctamente.");
            request.setAttribute("idSolicitud", request.getParameter("id"));
        }
    }

    private Integer buscarIdCliente(int idUsuario) {
        return usuarioDAO.obtenerIdClientePorUsuario(idUsuario);
    }

    private Integer getIdUsuario(HttpServletRequest request) {
        Object value = request.getSession().getAttribute("idUsuario");
        return value instanceof Integer ? (Integer) value : null;
    }

    private int parsePositiveInt(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message);
        }
    }

    private BigDecimal parseMonto(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El monto solicitado es obligatorio.");
        }

        try {
            BigDecimal monto = new BigDecimal(value.replace("$", "").replace(",", "").trim());
            if (monto.signum() <= 0) {
                throw new NumberFormatException();
            }
            return monto.setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El monto solicitado no es válido.");
        }
    }

    private LocalDate parseFechaVencimiento(HttpServletRequest request) {
        String fecha = trim(request.getParameter("fechaVencimiento"));
        if (fecha != null) {
            try {
                LocalDate value = LocalDate.parse(fecha);
                if (value.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("La fecha de vencimiento no puede estar en el pasado.");
                }
                return value;
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("La fecha de vencimiento no es válida.");
            }
        }

        // Para mantener compatibilidad con el formulario actual, que usa plazo,
        // si no llega una fecha calculamos un plazo por defecto de 30 días.
        String plazo = trim(request.getParameter("plazo"));
        if (plazo != null) {
            int meses = extraerMeses(plazo);
            return LocalDate.now().plusMonths(meses);
        }

        return LocalDate.now().plusDays(30);
    }

    private int extraerMeses(String plazo) {
        String digits = plazo.replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            return 1;
        }
        int meses = Integer.parseInt(digits);
        return meses > 0 ? meses : 1;
    }

    private String trim(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
