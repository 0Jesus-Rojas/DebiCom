package Servlet;

import Controlador.VendedorDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Muestra el detalle de un crédito para el vendedor autenticado. */
@WebServlet(name="DetalleCreditoServlet", urlPatterns={"/vendedor/creditos/detalle"})
public class DetalleCreditoServlet extends HttpServlet {
    private final VendedorDAO dao=new VendedorDAO();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        Integer vendedor=AuthUtil.getIdUsuario(request);
        if(!AuthUtil.isStaff(request)){response.sendError(403,"No tiene permisos para acceder a la administración.");return;}
        if(vendedor==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
        int id=parse(request.getParameter("id"));
        if(id<=0){response.sendError(400,"ID de solicitud inválido.");return;}
        try{
            var detalle=dao.obtenerDetalleCredito(id,vendedor);
            if(detalle==null){response.sendError(404,"Crédito no encontrado.");return;}
            request.setAttribute("detalleCredito",detalle);
            request.setAttribute("ok",request.getParameter("ok"));
            request.setAttribute("error",request.getParameter("error"));
            request.getRequestDispatcher("/estado-credito.jsp").forward(request,response);
        }catch(RuntimeException e){log("Error consultando detalle",e);response.sendError(500,"No fue posible consultar el detalle.");}
    }
    private int parse(String s){try{return Integer.parseInt(s);}catch(Exception e){return 0;}}
}
