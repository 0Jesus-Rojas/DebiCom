<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.SolicitudCreditoDTO"%>
<%@page import="Modelo.dto.ProductoDTO"%>
<%@page import="java.util.List"%>
<%
if (session.getAttribute("idUsuario") == null) {
    response.sendRedirect(request.getContextPath() + "/index.jsp");
    return;
}
SolicitudCreditoDTO p = (SolicitudCreditoDTO) request.getAttribute("prestamo");
List<ProductoDTO> productos = (List<ProductoDTO>) request.getAttribute("productosPrestamo");
if (p == null) { response.sendError(404, "Préstamo no encontrado."); return; }
boolean esCredito = "CREDITO".equalsIgnoreCase(p.getTipoPrestamo());
java.math.BigDecimal cupo = p.getCupoAprobado() == null ? java.math.BigDecimal.ZERO : p.getCupoAprobado();
java.math.BigDecimal saldo = p.getSaldoPendiente() == null ? java.math.BigDecimal.ZERO : p.getSaldoPendiente();
java.math.BigDecimal disponible = cupo.subtract(saldo);
if (disponible.signum() < 0) disponible = java.math.BigDecimal.ZERO;
%>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Detalle del préstamo | DebiCom</title><link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css"></head>
<body class="app-body"><div class="app-shell"><%@include file="WEB-INF/jspf/sidebar.jspf" %><main class="app-main"><section class="page-content">
<div class="page-heading"><div><h1>Detalle del préstamo #<%=p.getIdSolicitud()%></h1><p>Consulta la información completa del crédito o fiado registrado a tu nombre.</p></div><a class="button secondary" href="<%=request.getContextPath()%>/comprador/estado-credito">Volver</a></div>
<article class="card"><div class="metric-row"><article class="metric"><span class="label">Tipo</span><strong><%=p.getTipoPrestamo()%></strong></article><article class="metric"><span class="label">Tienda</span><strong><%=p.getTienda()%></strong></article><article class="metric"><span class="label">Estado</span><strong><%=p.getEstado()%></strong></article><article class="metric"><span class="label">Vencimiento</span><strong><%=p.getFechaVencimiento()%></strong></article></div>
<div class="metric-row"><article class="metric"><span class="label"><%=esCredito ? "Cupo aprobado" : "Valor fiado"%></span><strong>$<%= (esCredito ? cupo : (p.getMontoTotal()==null?java.math.BigDecimal.ZERO:p.getMontoTotal())).setScale(2) %></strong></article><article class="metric warning"><span class="label">Saldo pendiente</span><strong>$<%=saldo.setScale(2)%></strong></article><%if(esCredito){%><article class="metric success"><span class="label">Cupo disponible</span><strong>$<%=disponible.setScale(2)%></strong></article><%}%><article class="metric"><span class="label">Fecha de solicitud</span><strong><%=p.getFechaSolicitud()%></strong></article></div>
<p><b>Fecha de aprobación/asignación:</b> <%=p.getFechaAprobacion()==null?"—":p.getFechaAprobacion()%></p><p><b>Observaciones:</b> <%=p.getObservaciones()==null||p.getObservaciones().isBlank()?"—":p.getObservaciones()%></p></article>
<article class="card"><h2>Productos asociados</h2><div class="table-wrap"><table><thead><tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th></tr></thead><tbody>
<%if(productos==null||productos.isEmpty()){%><tr><td colspan="4"><%=esCredito?"Este crédito todavía no registra productos consumidos.":"Este fiado no tiene productos registrados."%></td></tr><%}else for(ProductoDTO x:productos){%><tr><td><b><%=x.getNombre()%></b><%if(x.getDescripcion()!=null&&!x.getDescripcion().isBlank()){%><br><small><%=x.getDescripcion()%></small><%}%></td><td><%=x.getCantidad()%></td><td>$<%=x.getPrecioUnitario()%></td><td>$<%=x.getSubtotal()%></td></tr><%}%>
</tbody></table></div></article>
</section></main></div></body></html>
