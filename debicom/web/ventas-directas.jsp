<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.TipoPago"%>
<%@page import="Modelo.dto.CompradorDTO"%>
<%@page import="Modelo.dto.ProductoDTO"%>
<%@page import="Modelo.dto.TiendaDTO"%>
<%@page import="java.util.List"%>
<%!
    private String html(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
if (session.getAttribute("idUsuario") == null) {
    response.sendRedirect(request.getContextPath() + "/index.jsp");
    return;
}
List<TiendaDTO> tiendas = (List<TiendaDTO>) request.getAttribute("tiendas");
List<CompradorDTO> compradores = (List<CompradorDTO>) request.getAttribute("compradores");
List<ProductoDTO> productos = (List<ProductoDTO>) request.getAttribute("productosVenta");
List<TipoPago> tiposPago = (List<TipoPago>) request.getAttribute("tiposPago");
Integer idTienda = (Integer) request.getAttribute("idTienda");
Integer idFactura = (Integer) request.getAttribute("idFactura");
String error = (String) request.getAttribute("error");
String ok = (String) request.getAttribute("ok");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ventas directas | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/venta-directa.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <div class="topbar">
            <span class="topbar-context">Ventas directas</span>
        </div>
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Venta directa</h1>
                    <p>Registra una venta de contado a un comprador. Esta operación no crea ni modifica créditos.</p>
                </div>
            </div>

            <% if (error != null && !error.isBlank()) { %>
                <article class="card venta-alert error"><strong>Error:</strong> <%=html(error)%></article>
            <% } %>

            <% if ("venta-registrada".equals(ok) && idFactura != null && idFactura > 0) { %>
                <article class="card venta-alert success">
                    <div>
                        <strong>Venta registrada correctamente.</strong>
                        <p>La factura <b>#<%=idFactura%></b> quedó pagada de contado y fue sincronizada con el historial del comprador.</p>
                    </div>
                    <a class="button primary" href="<%=request.getContextPath()%>/vendedor/ventas-directas/factura?id=<%=idFactura%>">Descargar factura PDF</a>
                </article>
            <% } %>

            <% if (tiendas == null || tiendas.isEmpty()) { %>
                <article class="card venta-alert error">El vendedor no tiene tiendas asignadas. Registra una tienda antes de realizar ventas.</article>
            <% } else { %>
                <form method="post" action="<%=request.getContextPath()%>/vendedor/ventas-directas" id="ventaDirectaForm" autocomplete="off">
                    <article class="card">
                        <div class="card-title">
                            <div>
                                <h2>Datos de la venta</h2>
                                <p>El servidor vuelve a validar tienda, comprador, precios y existencias al guardar.</p>
                            </div>
                        </div>
                        <div class="dark-grid">
                            <label> Tienda
                                <select name="idTienda" id="idTienda" onchange="cambiarTienda(this.value)" required>
                                    <% for (TiendaDTO tienda : tiendas) { %>
                                        <option value="<%=tienda.getIdTienda()%>" <%=idTienda != null && idTienda == tienda.getIdTienda() ? "selected" : ""%>>
                                            <%=html(tienda.getNombreTienda())%>
                                        </option>
                                    <% } %>
                                </select>
                            </label>
                            <label> Comprador
                                <select name="idCliente" required>
                                    <option value="">Seleccione un comprador</option>
                                    <% if (compradores != null) for (CompradorDTO comprador : compradores) { %>
                                        <option value="<%=comprador.getIdCliente()%>">
                                            <%=html(comprador.getNombreCompleto())%> — <%=html(comprador.getIdentificacion())%>
                                        </option>
                                    <% } %>
                                </select>
                            </label>
                            <label> Método de pago
                                <select name="idTipoPago" required>
                                    <option value="">Seleccione un método</option>
                                    <% if (tiposPago != null) for (TipoPago tipo : tiposPago) { %>
                                        <option value="<%=tipo.getIdTipoPago()%>"><%=html(tipo.getNombrePago())%></option>
                                    <% } %>
                                </select>
                            </label>
                            <label class="full"> Observaciones
                                <textarea name="observaciones" maxlength="255" placeholder="Opcional"></textarea>
                            </label>
                        </div>
                    </article>

                    <article class="card">
                        <div class="card-title">
                            <div>
                                <h2>Productos</h2>
                                <p>Selecciona los productos y la cantidad. El precio usado en la venta siempre se toma de la base de datos.</p>
                            </div>
                            <div class="venta-search">
                                <label for="productoBusqueda">Buscar</label>
                                <input id="productoBusqueda" type="search" placeholder="Nombre del producto">
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table id="productosTabla">
                                <thead>
                                <tr>
                                    <th>Agregar</th>
                                    <th>Producto</th>
                                    <th>Stock</th>
                                    <th>Precio</th>
                                    <th>Cantidad</th>
                                    <th>Subtotal</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% if (productos == null || productos.isEmpty()) { %>
                                    <tr><td colspan="6">No hay productos activos con stock disponible en esta tienda.</td></tr>
                                <% } else { for (ProductoDTO producto : productos) { %>
                                    <tr data-producto-nombre="<%=html(producto.getNombre()).toLowerCase()%>">
                                        <td>
                                            <input type="checkbox" class="venta-check" name="idProducto"
                                                   value="<%=producto.getIdProducto()%>"
                                                   data-id="<%=producto.getIdProducto()%>">
                                        </td>
                                        <td>
                                            <b><%=html(producto.getNombre())%></b>
                                            <% if (producto.getDescripcion() != null && !producto.getDescripcion().isBlank()) { %>
                                                <br><small><%=html(producto.getDescripcion())%></small>
                                            <% } %>
                                        </td>
                                        <td class="stock-cell"><%=producto.getStock()%></td>
                                        <td class="unit-price" data-price="<%=producto.getPrecioUnitario()%>">$<%=producto.getPrecioUnitario()%></td>
                                        <td>
                                            <input type="number" class="venta-qty" name="cantidad_<%=producto.getIdProducto()%>"
                                                   data-id="<%=producto.getIdProducto()%>" min="1" max="<%=producto.getStock()%>" value="1" disabled>
                                        </td>
                                        <td>$<span class="line-total" data-id="<%=producto.getIdProducto()%>">0.00</span></td>
                                    </tr>
                                <% }} %>
                                <tr id="sinResultados" hidden><td colspan="6">No hay productos que coincidan con la búsqueda.</td></tr>
                                </tbody>
                            </table>
                        </div>

                        <div class="metric-row">
                            <article class="metric"><span class="label">Productos seleccionados</span><strong id="productoCount">0</strong></article>
                            <article class="metric success"><span class="label">Total de contado</span><strong>$<span id="grandTotal">0.00</span></strong></article>
                        </div>

                        <div class="venta-actions">
                            <button class="button primary" type="button" id="confirmarVentaBtn">Registrar venta de contado</button>
                            <a class="button secondary" href="<%=request.getContextPath()%>/vendedor/dashboard">Cancelar</a>
                        </div>
                    </article>

                    <div class="modal" id="confirmModal" aria-hidden="true">
                        <div class="modal-card" role="dialog" aria-modal="true" aria-labelledby="confirmTitle">
                            <button type="button" class="modal-close" id="cerrarConfirmacion" aria-label="Cerrar">&times;</button>
                            <h2 id="confirmTitle">Confirmar venta</h2>
                            <p>Se registrará una venta de contado por un total de <strong>$<span id="modalTotal">0.00</span></strong>.</p>
                            <p class="modal-note">La operación descuenta inventario, crea la factura y registra el pago. No se genera crédito.</p>
                            <div class="venta-actions">
                                <button type="submit" class="button primary">Confirmar y guardar</button>
                                <button type="button" class="button secondary" id="cancelarConfirmacion">Volver</button>
                            </div>
                        </div>
                    </div>
                </form>
            <% } %>
        </section>
    </main>
</div>

<script>
(function () {
    const form = document.getElementById('ventaDirectaForm');
    if (!form) return;

    const checks = Array.from(form.querySelectorAll('.venta-check'));
    const quantities = Array.from(form.querySelectorAll('.venta-qty'));
    const totalNode = document.getElementById('grandTotal');
    const countNode = document.getElementById('productoCount');
    const modal = document.getElementById('confirmModal');
    const modalTotal = document.getElementById('modalTotal');
    const search = document.getElementById('productoBusqueda');
    const empty = document.getElementById('sinResultados');

    function updateTotal() {
        let total = 0;
        let count = 0;
        checks.forEach(function (check) {
            const quantity = quantities.find(function (node) { return node.dataset.id === check.dataset.id; });
            const line = form.querySelector('.line-total[data-id="' + check.dataset.id + '"]');
            const row = check.closest('tr');
            const price = parseFloat(row.querySelector('.unit-price').dataset.price || '0');
            if (check.checked) {
                quantity.disabled = false;
                const max = parseInt(quantity.max || '1', 10);
                let value = parseInt(quantity.value || '1', 10);
                if (!Number.isFinite(value) || value < 1) value = 1;
                if (value > max) value = max;
                quantity.value = value;
                const subtotal = price * value;
                line.textContent = subtotal.toFixed(2);
                total += subtotal;
                count++;
            } else {
                quantity.disabled = true;
                line.textContent = '0.00';
            }
        });
        totalNode.textContent = total.toFixed(2);
        countNode.textContent = String(count);
    }

    function openModal() {
        if (!checks.some(function (check) { return check.checked; })) {
            alert('Selecciona al menos un producto.');
            return;
        }
        const buyer = form.querySelector('[name="idCliente"]');
        const payment = form.querySelector('[name="idTipoPago"]');
        const store = form.querySelector('[name="idTienda"]');
        if (!buyer.value || !payment.value || !store.value) {
            alert('Completa tienda, comprador y método de pago.');
            return;
        }
        modalTotal.textContent = totalNode.textContent;
        modal.classList.add('active');
        modal.setAttribute('aria-hidden', 'false');
    }

    function closeModal() {
        modal.classList.remove('active');
        modal.setAttribute('aria-hidden', 'true');
    }

    document.getElementById('confirmarVentaBtn').addEventListener('click', openModal);
    document.getElementById('cerrarConfirmacion').addEventListener('click', closeModal);
    document.getElementById('cancelarConfirmacion').addEventListener('click', closeModal);
    modal.addEventListener('click', function (event) {
        if (event.target === modal) closeModal();
    });
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && modal.classList.contains('active')) closeModal();
    });

    checks.forEach(function (check) { check.addEventListener('change', updateTotal); });
    quantities.forEach(function (quantity) { quantity.addEventListener('input', updateTotal); });

    search.addEventListener('input', function () {
        const term = search.value.trim().toLocaleLowerCase('es');
        let visible = 0;
        form.querySelectorAll('#productosTabla tbody tr[data-producto-nombre]').forEach(function (row) {
            const show = !term || row.dataset.productoNombre.includes(term);
            row.hidden = !show;
            if (show) visible++;
        });
        empty.hidden = visible !== 0;
    });

    updateTotal();
})();

function cambiarTienda(idTienda) {
    const url = new URL(window.location.href);
    url.searchParams.set('idTienda', idTienda);
    url.searchParams.delete('idFactura');
    url.searchParams.delete('ok');
    window.location.replace(url.toString());
}
</script>
</body>
</html>
