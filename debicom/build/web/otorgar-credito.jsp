<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.CompradorDTO"%>
<%@page import="Modelo.dto.TiendaDTO"%>
<%@page import="java.util.List"%>
<%
if(session.getAttribute("idUsuario")==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
List<TiendaDTO> tiendas=(List<TiendaDTO>)request.getAttribute("tiendas");
List<CompradorDTO> compradores=(List<CompradorDTO>)request.getAttribute("compradores");
Integer idTienda=(Integer)request.getAttribute("idTienda");
String error=(String)request.getAttribute("error");
%>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Asignar crédito | DebiCom</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css"></head>
<body class="app-body"><div class="app-shell"><%@include file="WEB-INF/jspf/sidebar.jspf" %><main class="app-main"><section class="page-content">
<div class="page-heading"><div><h1>Asignar crédito</h1><p>Asigna un cupo aprobado. No se entregan productos en este paso; el saldo aparece únicamente cuando el cupo se consume.</p></div></div>
<%if(error!=null&&!error.isBlank()){%><article class="card" style="margin-bottom:16px;border-left:4px solid #e74c3c;"><strong>Error:</strong> <%=error%></article><%}%>
<form method="post" action="<%=request.getContextPath()%>/vendedor/creditos/otorgar">
<article class="card"><div class="dark-grid">
<label>Tienda<select name="idTienda" required onchange="window.location='<%=request.getContextPath()%>/vendedor/creditos/otorgar?idTienda='+this.value">
<%if(tiendas!=null)for(TiendaDTO t:tiendas){%><option value="<%=t.getIdTienda()%>" <%=idTienda!=null&&idTienda==t.getIdTienda()?"selected":""%>><%=t.getNombreTienda()%></option><%}%></select></label>
<label>Comprador<select name="idCliente" required><option value="">Seleccione un comprador</option>
<%if(compradores!=null)for(CompradorDTO c:compradores){%><option value="<%=c.getIdCliente()%>"><%=c.getNombreCompleto()%> — <%=c.getIdentificacion()%></option><%}%>
</select></label>
<label>Cupo aprobado<input type="number" name="cupo" min="0.01" step="0.01" required placeholder="0.00"></label>
<label>Fecha límite<input type="date" name="fechaVencimiento" min="<%=java.time.LocalDate.now().plusDays(15)%>" max="<%=java.time.LocalDate.now().plusMonths(1)%>" value="<%=java.time.LocalDate.now().plusMonths(1)%>" required></label>
<label class="full">Observaciones<textarea name="observaciones" maxlength="255" placeholder="Opcional"></textarea></label>
</div>
<div class="form-actions"><button class="button primary" type="submit">Asignar cupo de crédito</button><a class="button secondary" href="<%=request.getContextPath()%>/vendedor/dashboard">Volver al panel</a></div>
</article></form>

<article class="card"><h2>Regla de negocio</h2><p>El crédito se maneja como un cupo aprobado con vigencia de 15 días a 1 mes. Esta acción no selecciona productos ni modifica inventario.</p></article>
</section></main></div></body></html>
