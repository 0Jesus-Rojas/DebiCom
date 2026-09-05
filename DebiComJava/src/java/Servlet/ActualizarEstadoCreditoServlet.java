package Servlet;

import Controlador.VendedorDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="ActualizarEstadoCreditoServlet", urlPatterns={"/vendedor/creditos/estado"})
public class ActualizarEstadoCreditoServlet extends HttpServlet {
    private final VendedorDAO dao=new VendedorDAO();
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        Integer vendedor=AuthUtil.getIdUsuario(request); if(!AuthUtil.isStaff(request)){response.sendError(403,"No tiene permisos para acceder a la administración.");return;}if(vendedor==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
        int id=parse(request.getParameter("idSolicitud"));String estado=request.getParameter("estado");
        if(id<=0 || estado==null){response.sendError(400,"Parámetros inválidos.");return;}
        try{boolean ok=dao.actualizarEstadoCredito(id,vendedor,estado);if(!ok){response.sendError(409,"La solicitud no existe, no pertenece a la tienda o ya fue procesada.");return;}response.sendRedirect(request.getContextPath()+"/vendedor/creditos?ok=1");}
        catch(IllegalArgumentException e){response.sendError(400,e.getMessage());}
        catch(RuntimeException e){log("Error actualizando crédito",e);response.sendError(500,"No fue posible actualizar el crédito.");}
    }
    @Override protected void doPut(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{doPost(request,response);}
    private int parse(String s){try{return Integer.parseInt(s);}catch(Exception e){return 0;}}
}
