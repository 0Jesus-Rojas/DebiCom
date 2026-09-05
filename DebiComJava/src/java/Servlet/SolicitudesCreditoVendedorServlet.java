package Servlet;

import Controlador.VendedorDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="SolicitudesCreditoVendedorServlet", urlPatterns={"/vendedor/creditos"})
public class SolicitudesCreditoVendedorServlet extends HttpServlet {
    private final VendedorDAO dao = new VendedorDAO();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        Integer idVendedor=AuthUtil.getIdUsuario(request); if(!AuthUtil.isStaff(request)){response.sendError(403,"No tiene permisos para acceder a la administración.");return;} if(idVendedor==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
        String estado=request.getParameter("estado");
        try{request.setAttribute("solicitudes",dao.listarSolicitudes(idVendedor,estado));request.setAttribute("estadoFiltro",estado);request.getRequestDispatcher("/aprobar-creditos.jsp").forward(request,response);}
        catch(RuntimeException e){log("Error listando créditos",e);response.sendError(500,"No fue posible cargar las solicitudes.");}
    }
}
