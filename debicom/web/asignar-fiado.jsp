<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.CompradorDTO"%>
<%@page import="Modelo.dto.ProductoDTO"%>
<%@page import="Modelo.dto.TiendaDTO"%>
<%@page import="Modelo.dto.SolicitudCreditoDTO"%>
<%@page import="java.util.List"%>
<%
if(session.getAttribute("idUsuario")==null){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
List<TiendaDTO> tiendas=(List<TiendaDTO>)request.getAttribute("tiendas");
List<CompradorDTO> compradores=(List<CompradorDTO>)request.getAttribute("compradores");
List<ProductoDTO> productos=(List<ProductoDTO>)request.getAttribute("productosFiado");
List<SolicitudCreditoDTO> fiados=(List<SolicitudCreditoDTO>)request.getAttribute("fiadosPendientes");
Integer idTienda=(Integer)request.getAttribute("idTienda");
String error=(String)request.getAttribute("error");
String ok=(String)request.getAttribute("ok");
String idFiado=(String)request.getAttribute("idFiado");
%>
<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Asignar fiado | DebiCom</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css"></head>
<body class="app-body"><div class="app-shell"><%@include file="WEB-INF/jspf/sidebar.jspf" %><main class="app-main"><section class="page-content">
<div class="page-heading"><div><h1>Asignar fiado</h1><p>El vendedor selecciona comprador, productos y cantidades. El fiado tiene un plazo máximo de 7 días.</p></div></div>
<%if("fiado-creado".equals(ok)){%><article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;"><strong>Fiado #<%=idFiado%> asignado correctamente.</strong> El inventario fue actualizado y la deuda quedó registrada.</article><%}%>
<%if(error!=null&&!error.isBlank()){%><article class="card" style="margin-bottom:16px;border-left:4px solid #e74c3c;"><strong>Error:</strong> <%=error%></article><%}%>
<form method="post" action="<%=request.getContextPath()%>/vendedor/fiados/asignar" id="fiadoForm">
<article class="card"><div class="dark-grid">
<label>Tienda<select name="idTienda" onchange="window.location='<%=request.getContextPath()%>/vendedor/fiados/asignar?idTienda='+this.value" required>
<%if(tiendas!=null)for(TiendaDTO t:tiendas){%><option value="<%=t.getIdTienda()%>" <%=idTienda!=null&&idTienda==t.getIdTienda()?"selected":""%>><%=t.getNombreTienda()%></option><%}%></select></label>
<label>Comprador<select name="idCliente" required><option value="">Seleccione un comprador</option>
<%if(compradores!=null)for(CompradorDTO c:compradores){%><option value="<%=c.getIdCliente()%>"><%=c.getNombreCompleto()%> — <%=c.getIdentificacion()%></option><%}%>
</select></label>
<label>Fecha máxima de pago<input type="date" name="fechaVencimiento" min="<%=java.time.LocalDate.now()%>" max="<%=java.time.LocalDate.now().plusDays(7)%>" value="<%=java.time.LocalDate.now().plusDays(7)%>" required></label>
<label class="full">Observaciones<textarea name="observaciones" maxlength="255" placeholder="Opcional"></textarea></label>
</div></article>

<article class="card"><div class="card-title"><div><h2>Productos que se entregan fiados</h2><p>Selecciona únicamente los productos que el comprador se llevará en esta asignación.</p></div></div>
<div class="table-wrap"><table><thead><tr><th>Agregar</th><th>Producto</th><th>Stock</th><th>Precio</th><th>Cantidad</th><th>Subtotal</th></tr></thead><tbody>
<%if(productos==null||productos.isEmpty()){%><tr><td colspan="6">No hay productos activos con stock disponible en esta tienda.</td></tr><%}
else for(ProductoDTO p:productos){%><tr>
<td><input type="checkbox" class="cart-check" name="idProducto" value="<%=p.getIdProducto()%>" data-id="<%=p.getIdProducto()%>"></td>
<td><b><%=p.getNombre()%></b><br><small><%=p.getDescripcion()==null?"":p.getDescripcion()%></small></td><td><%=p.getStock()%></td>
<td class="unit-price" data-price="<%=p.getPrecioUnitario()%>">$<%=p.getPrecioUnitario()%></td>
<td><input type="number" class="cart-qty" name="cantidad_<%=p.getIdProducto()%>" data-id="<%=p.getIdProducto()%>" min="1" max="<%=p.getStock()%>" value="1" disabled style="max-width:110px"></td>
<td>$<span class="line-total" data-id="<%=p.getIdProducto()%>">0.00</span></td></tr><%}%>
</tbody></table></div>
<div class="metric-row"><article class="metric"><span class="label">Productos seleccionados</span><strong id="count">0</strong></article><article class="metric success"><span class="label">Total del fiado</span><strong>$<span id="grandTotal">0.00</span></strong></article></div>
<div class="form-actions"><button class="button primary" type="submit">Asignar fiado</button><a class="button secondary" href="<%=request.getContextPath()%>/vendedor/dashboard">Volver al panel</a></div>
</article></form>

<article class="card"><h2>Fiados pendientes</h2><div class="table-wrap"><table><thead><tr><th>Fiado</th><th>Comprador</th><th>Tienda</th><th>Total</th><th>Saldo</th><th>Vencimiento</th></tr></thead><tbody>
<%if(fiados==null||fiados.isEmpty()){%><tr><td colspan="6">No hay fiados pendientes.</td></tr><%}
else for(SolicitudCreditoDTO f:fiados){%><tr><td>#<%=f.getIdSolicitud()%></td><td><%=f.getCliente()%></td><td><%=f.getTienda()%></td><td>$<%=f.getMontoTotal()%></td><td><b>$<%=f.getSaldoPendiente()%></b></td><td><%=f.getFechaVencimiento()%></td></tr><%}%>
</tbody></table></div></article>

<script>
(function(){
 const checks=[...document.querySelectorAll('.cart-check')], qtys=[...document.querySelectorAll('.cart-qty')];
 const total=document.getElementById('grandTotal'),count=document.getElementById('count');
 function update(){
   let sum=0,n=0;
   checks.forEach(c=>{
     const q=qtys.find(x=>x.dataset.id===c.dataset.id);
     const line=document.querySelector('.line-total[data-id="'+c.dataset.id+'"]');
     if(c.checked){
       q.disabled=false;n++;
       const price=parseFloat(document.querySelector('.unit-price',c.closest('tr')).dataset.price)||0;
       const val=Math.max(1,parseInt(q.value||'1'));q.value=val;
       sum+=price*val;line.textContent=(price*val).toFixed(2);
     }else{q.disabled=true;line.textContent='0.00';}
   });
   total.textContent=sum.toFixed(2);count.textContent=n;
 }
 checks.forEach(c=>c.addEventListener('change',update));qtys.forEach(q=>q.addEventListener('input',update));update();
 document.getElementById('fiadoForm').addEventListener('submit',function(e){
   if(!checks.some(c=>c.checked)){e.preventDefault();alert('Selecciona al menos un producto para asignar el fiado.');}
 });
})();
</script>
</section></main></div></body></html>
