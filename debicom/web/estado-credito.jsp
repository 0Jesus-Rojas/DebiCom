<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.SolicitudCreditoDTO"%>
<%@page import="Modelo.dto.DetalleCreditoDTO"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.dto.ProductoDTO"%>
<%
if(session.getAttribute("idUsuario")==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
SolicitudCreditoDTO solicitud=(SolicitudCreditoDTO)request.getAttribute("solicitud");
List<SolicitudCreditoDTO> solicitudes=(List<SolicitudCreditoDTO>)request.getAttribute("solicitudes");
DetalleCreditoDTO detalle=(DetalleCreditoDTO)request.getAttribute("detalleCredito");
String ok=(String)request.getAttribute("ok"); String error=(String)request.getAttribute("error");
java.math.BigDecimal disponible=java.math.BigDecimal.ZERO;
if(detalle!=null && detalle.getSolicitud()!=null){
    SolicitudCreditoDTO baseSolicitud=detalle.getSolicitud();
    java.math.BigDecimal baseCupo=baseSolicitud.getCupoAprobado()==null?java.math.BigDecimal.ZERO:baseSolicitud.getCupoAprobado();
    java.math.BigDecimal baseSaldo=baseSolicitud.getSaldoPendiente()==null?java.math.BigDecimal.ZERO:baseSolicitud.getSaldoPendiente();
    disponible=baseCupo.subtract(baseSaldo);
    if(disponible.signum()<0) disponible=java.math.BigDecimal.ZERO;
}
boolean staff=session.getAttribute("idRol")!=null && (((Integer)session.getAttribute("idRol"))==1 || ((Integer)session.getAttribute("idRol"))==2 || ((Integer)session.getAttribute("idRol"))==3);
%>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Estado del crédito | DebiCom</title><link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css"></head>
<body class="app-body"><div class="app-shell"><%@include file="WEB-INF/jspf/sidebar.jspf" %><main class="app-main"><section class="page-content">
<div class="page-heading"><div><h1><%=detalle!=null?"Detalle de crédito":"Estado de mis créditos"%></h1><p><%=detalle!=null?"Administra el cupo, agrega productos y consulta el saldo acumulado.":"Consulta el estado de tus solicitudes."%></p></div></div>
<%if("pago".equals(ok)){%><article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;"><strong>El pago fue aplicado correctamente.</strong></article><%}%>
<%if("credito-creado".equals(ok)){%><article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;"><strong>El crédito fue otorgado y quedó aprobado correctamente.</strong></article><%}%>
<%if("productos-agregados".equals(ok)){%><article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;"><strong>Los productos se agregaron al crédito y el inventario fue actualizado.</strong></article><%}%>
<%if(error!=null&&!error.isBlank()){%><article class="card" style="margin-bottom:16px;border-left:4px solid #e74c3c;"><strong>Error:</strong> <%=error%></article><%}%>
<%if(detalle!=null){SolicitudCreditoDTO s=detalle.getSolicitud(); java.math.BigDecimal cupo=s.getCupoAprobado()==null?java.math.BigDecimal.ZERO:s.getCupoAprobado(); java.math.BigDecimal saldo=s.getSaldoPendiente()==null?java.math.BigDecimal.ZERO:s.getSaldoPendiente();%>
<article class="card"><div class="metric-row"><article class="metric"><span class="label">Crédito</span><strong>#<%=s.getIdSolicitud()%></strong></article><article class="metric"><span class="label">Cliente</span><strong><%=s.getCliente()%></strong></article><article class="metric"><span class="label">Tienda</span><strong><%=s.getTienda()%></strong></article><article class="metric"><span class="label">Estado</span><strong><%=s.getEstado()%></strong></article></div>
<div class="metric-row"><article class="metric"><span class="label">Cupo aprobado</span><strong>$<%=cupo.setScale(2)%></strong></article><article class="metric warning"><span class="label">Saldo usado</span><strong>$<%=saldo.setScale(2)%></strong></article><article class="metric success"><span class="label">Cupo disponible</span><strong>$<%=disponible.setScale(2)%></strong></article><article class="metric"><span class="label">Vencimiento</span><strong><%=s.getFechaVencimiento()%></strong></article></div>
<p><b>Fecha creación:</b> <%=s.getFechaSolicitud()%> · <b>Aprobación:</b> <%=s.getFechaAprobacion()==null?"—":s.getFechaAprobacion()%></p><p><b>Observaciones:</b> <%=s.getObservaciones()==null?"":s.getObservaciones()%></p></article>

<%if(staff && "CREDITO".equalsIgnoreCase(s.getTipoPrestamo()) && "APROBADO".equalsIgnoreCase(s.getEstado()) && disponible.signum()>0 && s.getFechaVencimiento()!=null && !s.getFechaVencimiento().isBefore(java.time.LocalDate.now())){%>
<article class="card"><div class="card-title"><div><h2>Agregar productos al crédito</h2><p>Selecciona lo que el comprador llevará ahora. El total se acumula al saldo sin superar el cupo disponible de <b>$<%=disponible.setScale(2)%></b>.</p></div></div>
<form method="post" action="<%=request.getContextPath()%>/vendedor/creditos/consumir" id="creditoConsumoForm">
<input type="hidden" name="idSolicitud" value="<%=s.getIdSolicitud()%>">
<div class="table-wrap"><table><thead><tr><th>Agregar</th><th>Producto</th><th>Stock</th><th>Precio</th><th>Cantidad</th><th>Subtotal</th></tr></thead><tbody>
<%List<ProductoDTO> disponiblesProductos=(List<ProductoDTO>)request.getAttribute("productosCredito"); if(disponiblesProductos==null||disponiblesProductos.isEmpty()){%><tr><td colspan="6">No hay productos activos con stock disponible en esta tienda.</td></tr><%} else for(ProductoDTO p:disponiblesProductos){%><tr><td><input type="checkbox" class="credito-check" name="idProducto" value="<%=p.getIdProducto()%>" data-id="<%=p.getIdProducto()%>"></td><td><b><%=p.getNombre()%></b><br><small><%=p.getDescripcion()==null?"":p.getDescripcion()%></small></td><td><%=p.getStock()%></td><td class="credito-price" data-price="<%=p.getPrecioUnitario()%>">$<%=p.getPrecioUnitario()%></td><td><input type="number" class="credito-qty" name="cantidad_<%=p.getIdProducto()%>" data-id="<%=p.getIdProducto()%>" min="1" max="<%=p.getStock()%>" value="1" disabled style="max-width:110px"></td><td>$<span class="credito-line" data-id="<%=p.getIdProducto()%>">0.00</span></td></tr><%}%>
</tbody></table></div>
<div class="metric-row"><article class="metric"><span class="label">Cupo disponible</span><strong>$<%=disponible.setScale(2)%></strong></article><article class="metric success"><span class="label">Total de esta compra</span><strong>$<span id="creditoGrandTotal">0.00</span></strong></article><article class="metric"><span class="label">Cupo restante</span><strong>$<span id="creditoRemaining"><%=disponible.setScale(2)%></span></strong></article></div>
<div class="form-actions"><button class="button primary" type="submit">Agregar productos al crédito</button><a class="button secondary" href="<%=request.getContextPath()%>/vendedor/dashboard">Volver al panel</a></div>
</form></article>
<%}%>

<article class="card"><h2>Productos consumidos con este crédito</h2><div class="table-wrap"><table><thead><tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th><th>Stock actual</th></tr></thead><tbody><%if(detalle.getProductos()==null||detalle.getProductos().isEmpty()){%><tr><td colspan="5">Todavía no se han agregado productos a este crédito.</td></tr><%}else for(ProductoDTO p:detalle.getProductos()){%><tr><td><%=p.getNombre()%></td><td><%=p.getCantidad()%></td><td>$<%=p.getPrecioUnitario()%></td><td>$<%=p.getSubtotal()%></td><td><%=p.getStock()%></td></tr><%}%></tbody></table></div></article>
<%if(staff && saldo.compareTo(java.math.BigDecimal.ZERO)>0){%><article class="card"><h2>Registrar pago</h2><p>El pago reduce el saldo acumulado del crédito.</p><form class="dark-grid" method="post" action="<%=request.getContextPath()%>/vendedor/pagos/registrar"><input type="hidden" name="idSolicitud" value="<%=s.getIdSolicitud()%>"><label>Monto a pagar<input type="number" name="monto" min="0.01" max="<%=saldo%>" step="0.01" required placeholder="0.00"></label><div class="form-actions"><button class="button primary" type="submit">Registrar pago</button><a class="button secondary" href="<%=request.getContextPath()%>/vendedor/creditos-pendientes">Volver</a></div></form></article><%}%>
<%}else{%><article class="card"><div class="table-wrap"><table><thead><tr><th>Préstamo</th><th>Tipo</th><th>Tienda</th><th>Cupo / valor</th><th>Saldo pendiente</th><th>Solicitud</th><th>Vencimiento</th><th>Estado</th><th>Detalle</th></tr></thead><tbody><%if(solicitudes==null||solicitudes.isEmpty()){%><tr><td colspan="9">No hay créditos ni fiados registrados.</td></tr><%}else for(SolicitudCreditoDTO s:solicitudes){%><tr><td>#<%=s.getIdSolicitud()%></td><td><span class="badge <%=s.getTipoPrestamo()==null?"":s.getTipoPrestamo().toLowerCase()%>"><%=s.getTipoPrestamo()==null?"—":s.getTipoPrestamo()%></span></td><td><%=s.getTienda()%></td><td>$<%= ("CREDITO".equalsIgnoreCase(s.getTipoPrestamo()) ? (s.getCupoAprobado()==null?java.math.BigDecimal.ZERO:s.getCupoAprobado()) : (s.getMontoTotal()==null?java.math.BigDecimal.ZERO:s.getMontoTotal())) %></td><td><strong>$<%=s.getSaldoPendiente()%></strong></td><td><%=s.getFechaSolicitud()%></td><td><%=s.getFechaVencimiento()%></td><td><span class="badge <%=s.getEstado()==null?"":s.getEstado().toLowerCase()%>"><%=s.getEstado()%></span></td><td><a class="button secondary tiny" href="<%=request.getContextPath()%>/comprador/estado-credito/detalle?id=<%=s.getIdSolicitud()%>">Ver detalle</a></td></tr><%}%></tbody></table></div></article><%}%>
<script>
(function(){
 const checks=[...document.querySelectorAll('.credito-check')], qtys=[...document.querySelectorAll('.credito-qty')];
 const total=document.getElementById('creditoGrandTotal'), remaining=document.getElementById('creditoRemaining');
 const disponible=total?parseFloat('<%=detalle!=null?disponible.toPlainString():"0"%>'):0;
 function update(){let sum=0; checks.forEach(c=>{const q=qtys.find(x=>x.dataset.id===c.dataset.id), line=document.querySelector('.credito-line[data-id="'+c.dataset.id+'"]'); if(!q)return; if(c.checked){q.disabled=false;let n=Math.max(1,parseInt(q.value||'1'));q.value=n;let price=parseFloat(document.querySelector('.credito-price',c.closest('tr')).dataset.price)||0;let sub=price*n;sum+=sub;line.textContent=sub.toFixed(2);}else{q.disabled=true;line.textContent='0.00';}}); if(total)total.textContent=sum.toFixed(2); if(remaining)remaining.textContent=Math.max(0,disponible-sum).toFixed(2);}
 checks.forEach(c=>c.addEventListener('change',update)); qtys.forEach(q=>q.addEventListener('input',update)); update();
 const form=document.getElementById('creditoConsumoForm'); if(form)form.addEventListener('submit',e=>{if(!checks.some(c=>c.checked)){e.preventDefault();alert('Selecciona al menos un producto.');return;} const v=parseFloat(total.textContent||'0'); if(v<=0||v>disponible+0.0001){e.preventDefault();alert('El total supera el cupo disponible.');}});
})();
</script>
</section></main></div></body></html>
