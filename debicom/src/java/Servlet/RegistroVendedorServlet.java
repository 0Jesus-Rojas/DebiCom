package Servlet;

import Servicio.RegistroVendedorService;
import Util.AuthUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RegistroVendedorServlet", urlPatterns = {"/comprador/registrarse-vendedor"})
public class RegistroVendedorServlet extends HttpServlet {

    private final RegistroVendedorService service = new RegistroVendedorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUsuario = AuthUtil.getIdUsuario(request);
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if (!service.puedeRegistrarseComoVendedor(idUsuario)) {
            request.setAttribute("error",
                    "Tu usuario ya tiene un rol asignado o ya está registrado como vendedor.");
        }

        request.getRequestDispatcher("/registrarse-vendedor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUsuario = AuthUtil.getIdUsuario(request);
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            int idRol = service.registrarComoVendedor(idUsuario);
            if (idRol <= 0) {
                request.setAttribute("error",
                        "No fue posible completar el registro. Verifica que tu cuenta sea un comprador.");
                request.getRequestDispatcher("/registrarse-vendedor.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("idRol", idRol);
            session.setAttribute("mensajeVendedor",
                    "Tu cuenta ahora está registrada como vendedor.");

            response.sendRedirect(request.getContextPath() + "/vendedor/dashboard");

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (RuntimeException e) {
            log("Error registrando vendedor para usuario " + idUsuario, e);
            request.setAttribute("error",
                    "Ocurrió un error al registrar tu cuenta como vendedor.");
            request.getRequestDispatcher("/registrarse-vendedor.jsp").forward(request, response);
        }
    }
}
