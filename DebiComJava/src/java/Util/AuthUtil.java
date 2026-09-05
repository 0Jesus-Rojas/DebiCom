package Util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Utilidades para obtener el usuario autenticado de la sesión. */
public final class AuthUtil {
    private AuthUtil() { }

    public static Integer getIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute("idUsuario");
        if (value instanceof Integer i) return i;
        if (value instanceof Long l) return l.intValue();
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) {
            try { return Integer.valueOf(s); } catch (NumberFormatException ignored) { }
        }
        return null;
    }

    public static Integer getIdCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute("idCliente");
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) {
            try { return Integer.valueOf(s); } catch (NumberFormatException ignored) { }
        }
        return null;
    }

    public static Integer getIdRol(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute("idRol");
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) { try { return Integer.valueOf(s); } catch (NumberFormatException ignored) { } }
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
