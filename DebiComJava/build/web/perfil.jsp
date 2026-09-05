<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@page import="Modelo.dto.PerfilDTO"%>
<%if(session.getAttribute("idUsuario")==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;} PerfilDTO p=(PerfilDTO)request.getAttribute("perfil");%>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Perfil | DebiCom</title><link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css"></head><body class="app-body"><div class="app-shell"><%@include file="WEB-INF/jspf/sidebar.jspf" %><main class="app-main"><section class="page-content"><div class="page-heading"><div><h1>Perfil de usuario</h1><p>Consulta y actualiza tu información personal.</p></div></div>
<%
    boolean puedeRegistrarseVendedor = Boolean.TRUE.equals(request.getAttribute("puedeRegistrarseVendedor"));
    String mensajeVendedor = (String) session.getAttribute("mensajeVendedor");
    String mensajeErrorVendedor = (String) session.getAttribute("mensajeErrorVendedor");
    session.removeAttribute("mensajeVendedor");
    session.removeAttribute("mensajeErrorVendedor");
%>
<% if (mensajeVendedor != null) { %>
<article class="card">
    <p><%=mensajeVendedor%></p>
</article>
<% } %>
<% if (mensajeErrorVendedor != null) { %>
<article class="card">
    <p><%=mensajeErrorVendedor%></p>
</article>
<% } %>
<% if (puedeRegistrarseVendedor) { %>
<article class="card">
    <h2>¿Quieres vender en DebiCom?</h2>
    <p>Regístrate como vendedor para administrar una tienda, productos y solicitudes de crédito.</p>
    <form method="post" action="<%=request.getContextPath()%>/comprador/registrarse-vendedor"
          onsubmit="return confirm('¿Deseas registrarte como vendedor?');">
        <button class="button primary" type="submit">Registrarme como vendedor</button>
    </form>
</article>
<% } %>
<article class="card"><form class="dark-grid" method="post" action="<%=request.getContextPath()%>/comprador/perfil"><label>Nombre<input name="nombre" value="<%=p==null?"":p.getNombre()%>" required></label><label>Apellido<input name="apellido" value="<%=p==null?"":p.getApellido()%>" required></label><label>Identificación<input name="identificacion" value="<%=p==null?"":p.getIdentificacion()%>" required></label><label>Tipo de identificación<input value="<%=p==null?"":p.getTipoIdentificacion()%>" readonly></label><label>Fecha de nacimiento<input type="date" name="fechaNacimiento" value="<%=p==null||p.getFechaNacimiento()==null?"":p.getFechaNacimiento()%>" required></label><label>Correo<input type="email" name="correo" value="<%=p==null?"":p.getCorreo()%>" required></label><label>Teléfono<input name="telefono" value="<%=p==null?"":p.getTelefono()%>" required></label><label>Dirección<input name="direccion" value="<%=p==null?"":p.getDireccion()%>" required></label><label><input type="checkbox" name="autorizaDatos" <%=p!=null&&p.isAutorizaDatos()?"checked":""%>> Autorizo el tratamiento de mis datos</label><div class="form-actions"><button class="button primary" type="submit">Guardar cambios</button></div></form></article>
</section></main></div></body></html>
