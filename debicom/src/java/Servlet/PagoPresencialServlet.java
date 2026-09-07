package Servlet;

import Controlador.PagoDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Registra pagos presenciales realizados por un vendedor sobre un crédito concreto. */
@WebServlet(name = "PagoPresencialServlet", urlPatterns = {"/vendedor/pagos/registrar"})
public class PagoPresencialServlet extends HttpServlet {
    private final PagoDAO pagoDAO = new PagoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idVendedor = AuthUtil.getIdUsuario(request);
        if (idVendedor == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        if (!AuthUtil.isStaff(request)) { response.sendError(HttpServletResponse.SC_FORBIDDEN,"No tiene permisos para registrar pagos."); return; }
        request.setCharacterEncoding("UTF-8");
        int idSolicitud = parse(request.getParameter("idSolicitud"));
        BigDecimal monto;
        try { monto = new BigDecimal(request.getParameter("monto")); }
        catch (Exception e) { redirigirError(request,response,idSolicitud,"El monto ingresado no es válido."); return; }
        try {
            pagoDAO.registrarPagoPresencial(idSolicitud,idVendedor,monto);
            response.sendRedirect(request.getContextPath()+"/vendedor/creditos/detalle?id="+idSolicitud+"&ok=pago");
        } catch (IllegalArgumentException e) {
            redirigirError(request,response,idSolicitud,e.getMessage());
        } catch (RuntimeException e) {
            log("Error registrando pago presencial",e);
            redirigirError(request,response,idSolicitud,"No fue posible registrar el pago presencial.");
        }
    }

    private void redirigirError(HttpServletRequest request,HttpServletResponse response,int idSolicitud,String mensaje) throws IOException {
        response.sendRedirect(request.getContextPath()+"/vendedor/creditos/detalle?id="+idSolicitud+"&error="
                +URLEncoder.encode(mensaje==null?"Error desconocido":mensaje,StandardCharsets.UTF_8));
    }
    private int parse(String value){try{return Integer.parseInt(value);}catch(Exception e){return 0;}}
}
