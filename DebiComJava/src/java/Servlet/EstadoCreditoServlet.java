package Servlet;

import Controlador.SolicitudCreditoDAO;
import Util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="EstadoCreditoServlet", urlPatterns={"/comprador/estado-credito"})
public class EstadoCreditoServlet extends HttpServlet {
    private final Controlador.UsuarioDAO usuarioDAO=new Controlador.UsuarioDAO();
    private final SolicitudCreditoDAO solicitudDAO=new SolicitudCreditoDAO();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        Integer usuario=AuthUtil.getIdUsuario(request);if(usuario==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
        try{Integer cliente=usuarioDAO.obtenerIdClientePorUsuario(usuario);if(cliente==null){response.sendError(404,"El usuario no tiene perfil de cliente.");return;}request.setAttribute("solicitudes",solicitudDAO.listarPorCliente(cliente));request.getRequestDispatcher("/estado-credito.jsp").forward(request,response);}
        catch(RuntimeException e){log("Error consultando estado de créditos",e);response.sendError(500,"No fue posible consultar el estado de los créditos.");}
    }
}
