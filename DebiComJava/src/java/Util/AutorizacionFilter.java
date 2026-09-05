package Util;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AutorizacionFilter", urlPatterns = {"/vendedor/*", "/comprador/*"})
public class AutorizacionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);
        Integer idUsuario = AuthUtil.getIdUsuario(request);

        if (idUsuario == null) {
            if (session != null) {
                session.setAttribute("mensajeAcceso", "Tu sesión ha expirado. Inicia sesión nuevamente.");
            }
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.startsWith("/vendedor/")) {
            Integer idRol = AuthUtil.getIdRol(request);

            if (idRol == null || idRol == 0) {
                response.sendRedirect(request.getContextPath()
                        + "/acceso-denegado.jsp?motivo=sin-rol");
                return;
            }

            if (!AuthUtil.isStaff(request)) {
                response.sendRedirect(request.getContextPath()
                        + "/acceso-denegado.jsp?motivo=rol");
                return;
            }
        }

        chain.doFilter(servletRequest, servletResponse);
    }
}
