package Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utilidades para obtener información del usuario autenticado desde la sesión.
 */
public final class AuthUtil {

    private AuthUtil() {
    }

    public static Integer getIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute("idUsuario");
        return toInteger(value);
    }

    public static Integer getIdCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return toInteger(session.getAttribute("idCliente"));
    }

    public static Integer getIdRol(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return toInteger(session.getAttribute("idRol"));
    }

    private static Integer toInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static boolean isStaff(HttpServletRequest request) {
        Integer rol = getIdRol(request);
        return rol != null && (rol == 1 || rol == 2 || rol == 3);
    }

    public static boolean authenticated(HttpServletRequest request) {
        return getIdUsuario(request) != null;
    }

    public static void ensureAuthenticated(HttpServletRequest request) {
        if (!authenticated(request)) {
            throw new SecurityException("Sesión no válida o expirada.");
        }
    }
}
