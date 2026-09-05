package Servlet;

import Controlador.TiendaDAO;
import Controlador.UsuarioDAO;
import Modelo.dto.TiendaDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TiendasDisponiblesServlet", urlPatterns = {"/comprador/tiendas"})
public class TiendasDisponiblesServlet extends HttpServlet {
    private final TiendaDAO tiendaDAO = new TiendaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer idUsuario = getIdUsuario(request);
        if (idUsuario == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
        Integer idCliente=usuarioDAO.obtenerIdClientePorUsuario(idUsuario);
        if(idCliente==null){response.sendError(403,"El usuario autenticado no tiene un perfil de comprador.");return;}
        try{List<TiendaDTO> tiendas=tiendaDAO.listarTiendasDisponibles();request.setAttribute("tiendas",tiendas);request.setAttribute("idCliente",idCliente);request.getRequestDispatcher("/solicitar-credito.jsp").forward(request,response);}
        catch(RuntimeException e){log("Error al cargar las tiendas disponibles",e);response.sendError(500,"No fue posible cargar las tiendas.");}
    }
    private Integer getIdUsuario(HttpServletRequest request){Object v=request.getSession(false)==null?null:request.getSession(false).getAttribute("idUsuario");if(v instanceof Integer i)return i;if(v instanceof Number n)return n.intValue();try{return v==null?null:Integer.valueOf(v.toString());}catch(Exception e){return null;}}
}
