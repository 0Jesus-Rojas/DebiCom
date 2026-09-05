package Servlet;

import Controlador.VendedorDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="CreditosPendientesServlet", urlPatterns={"/vendedor/creditos-pendientes"})
public class CreditosPendientesServlet extends HttpServlet {
    private final VendedorDAO dao=new VendedorDAO();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        Integer vendedor=AuthUtil.getIdUsuario(request); if(!AuthUtil.isStaff(request)){response.sendError(403,"No tiene permisos para acceder a la administración.");return;}if(vendedor==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
        try{
            request.setAttribute("creditosPendientes",dao.obtenerCreditosPendientes(vendedor));
            request.setAttribute("ok", request.getParameter("ok"));
            request.setAttribute("error", request.getParameter("error"));
            request.getRequestDispatcher("/creditos-pendientes.jsp").forward(request,response);
        }
        catch(RuntimeException e){log("Error consultando créditos pendientes",e);response.sendError(500,"No fue posible consultar los créditos pendientes.");}
    }
}
