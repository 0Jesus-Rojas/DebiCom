<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.ProductoDTO"%>
<%@page import="Modelo.dto.TiendaDTO"%>
<%@page import="Modelo.Unidades"%>
<%@page import="Modelo.EstadosProducto"%>
<%@page import="Controlador.VendedorDAO"%>
<%@page import="java.util.*"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    VendedorDAO.InventarioData m = (VendedorDAO.InventarioData) request.getAttribute("metricasInventario");
    List<ProductoDTO> productos = (List<ProductoDTO>) request.getAttribute("productos");
    List<TiendaDTO> tiendas = (List<TiendaDTO>) request.getAttribute("tiendas");
    List<Unidades> unidades = (List<Unidades>) request.getAttribute("unidades");
    List<EstadosProducto> estadosProducto = (List<EstadosProducto>) request.getAttribute("estadosProducto");
    String search = (String) request.getAttribute("search");
    Integer idTienda = (Integer) request.getAttribute("idTienda");
    String error = (String) request.getAttribute("error");
    String ok = (String) request.getAttribute("ok");
    Modelo.Productos productoEditar = (Modelo.Productos) request.getAttribute("productoEditar");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Inventario | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Inventario</h1>
                    <p>Productos de la tienda seleccionada.</p>
                </div>
            </div>

            <% if ("producto".equals(ok)) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;">
                    <strong>El producto fue registrado correctamente.</strong>
                </article>
            <% } %>

            <% if ("actualizado".equals(ok)) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;">
                    <strong>El producto fue actualizado correctamente.</strong>
                </article>
            <% } %>

            <% if (error != null) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #e74c3c;">
                    <strong>Error:</strong> <%=error%>
                </article>
            <% } %>

            <form method="get" class="card" action="<%=request.getContextPath()%>/vendedor/inventario">
                <div class="dark-grid">
                    <label>Tienda
                        <select name="idTienda" onchange="this.form.submit()" required>
                            <% if (tiendas != null) for (TiendaDTO t : tiendas) { %>
                                <option value="<%=t.getIdTienda()%>"
                                    <%=idTienda != null && idTienda == t.getIdTienda() ? "selected" : ""%>>
                                    <%=t.getNombreTienda()%>
                                </option>
                            <% } %>
                        </select>
                    </label>
                    <label>Buscar por nombre
                        <input name="search" value="<%=search == null ? "" : search%>" placeholder="Ej: arroz">
                    </label>
                    <div class="form-actions">
                        <button class="button primary" type="submit">Buscar</button>
                    </div>
                </div>
            </form>

            <% if (idTienda != null && idTienda > 0) { %>
            <article class="card">
                <h2>Agregar producto</h2>
                <form class="dark-grid" method="post" action="<%=request.getContextPath()%>/vendedor/inventario">
                    <input type="hidden" name="idTienda" value="<%=idTienda%>">
                    <label>Nombre
                        <input name="nombre" maxlength="100" required placeholder="Ej. Arroz 1 kg">
                    </label>
                    <label>Precio unitario
                        <input type="number" name="precio" min="0.01" step="0.01" required placeholder="0.00">
                    </label>
                    <label>Stock inicial
                        <input type="number" name="stock" min="0" step="1" value="0" required>
                    </label>
                    <label>Unidad
                        <select name="idUnidad" required>
                            <option value="">Seleccione</option>
                            <% if (unidades != null) for (Unidades u : unidades) { %>
                                <option value="<%=u.getIdUnidad()%>"><%=u.getNombreUnidad()%></option>
                            <% } %>
                        </select>
                    </label>
                    <label>Estado
                        <select name="idEstadoProducto" required>
                            <% if (estadosProducto != null) for (EstadosProducto e : estadosProducto) { %>
                                <option value="<%=e.getIdEstadoProducto()%>"
                                    <%=e.getIdEstadoProducto() == 1 ? "selected" : ""%>>
                                    <%=e.getNomrebEstadpo()%>
                                </option>
                            <% } %>
                        </select>
                    </label>
                    <label class="full">Descripción
                        <textarea name="descripcion" maxlength="255" placeholder="Descripción del producto"></textarea>
                    </label>
                    <div class="form-actions">
                        <button class="button primary" type="submit">Agregar producto</button>
                    </div>
                </form>
            </article>

            <% if (productoEditar != null) { %>
            <article class="card" style="margin-top:16px;">
                <h2>Editar producto</h2>
                <form class="dark-grid" method="post" action="<%=request.getContextPath()%>/vendedor/inventario">
                    <input type="hidden" name="accion" value="actualizar">
                    <input type="hidden" name="idProducto" value="<%=productoEditar.getIdProducto()%>">
                    <input type="hidden" name="idTienda" value="<%=productoEditar.getIdTienda()%>">

                    <label>Nombre
                        <input name="nombre" maxlength="100" required value="<%=productoEditar.getNombre()%>">
                    </label>
                    <label>Precio unitario
                        <input type="number" name="precio" min="0.01" step="0.01" required value="<%=productoEditar.getPrecioUnitario()%>">
                    </label>
                    <label>Stock
                        <input type="number" name="stock" min="0" step="1" required value="<%=productoEditar.getStock()%>">
                    </label>
                    <label>Unidad
                        <select name="idUnidad" required>
                            <% if (unidades != null) for (Unidades u : unidades) { %>
                                <option value="<%=u.getIdUnidad()%>" <%=u.getIdUnidad() == productoEditar.getIdUnidad() ? "selected" : ""%>><%=u.getNombreUnidad()%></option>
                            <% } %>
                        </select>
                    </label>
                    <label>Estado
                        <select name="idEstadoProducto" required>
                            <% if (estadosProducto != null) for (EstadosProducto e : estadosProducto) { %>
                                <option value="<%=e.getIdEstadoProducto()%>" <%=e.getIdEstadoProducto() == productoEditar.getIdEstadoProducto() ? "selected" : ""%>><%=e.getNomrebEstadpo()%></option>
                            <% } %>
                        </select>
                    </label>
                    <label class="full">Descripción
                        <textarea name="descripcion" maxlength="255"><%=productoEditar.getDescripcion() == null ? "" : productoEditar.getDescripcion()%></textarea>
                    </label>
                    <div class="form-actions">
                        <button class="button primary" type="submit">Guardar cambios</button>
                        <a class="button secondary" href="<%=request.getContextPath()%>/vendedor/inventario?idTienda=<%=idTienda%>">Cancelar</a>
                    </div>
                </form>
            </article>
            <% } %>

            <div class="metric-row">
                <article class="metric"><span class="label">Productos activos</span><strong><%=m == null ? 0 : m.activos%></strong></article>
                <article class="metric success"><span class="label">Disponibles</span><strong><%=m == null ? 0 : m.disponibles%></strong></article>
                <article class="metric warning"><span class="label">A punto de agotarse</span><strong><%=m == null ? 0 : m.bajoStock%></strong></article>
                <article class="metric danger"><span class="label">Agotados</span><strong><%=m == null ? 0 : m.agotados%></strong></article>
            </div>

            <article class="card">
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr><th>Producto</th><th>Stock</th><th>Unidad</th><th>Estado</th><th>Precio</th><th>Acción</th></tr>
                        </thead>
                        <tbody>
                        <% if (productos == null || productos.isEmpty()) { %>
                            <tr><td colspan="6">No hay productos registrados en esta tienda.</td></tr>
                        <% } else {
                            for (ProductoDTO p : productos) { %>
                            <tr>
                                <td><b><%=p.getNombre()%></b><br><small><%=p.getDescripcion() == null ? "" : p.getDescripcion()%></small></td>
                                <td><%=p.getStock()%></td>
                                <td><%=p.getUnidad() == null ? "" : p.getUnidad()%></td>
                                <td><%=p.getEstado() == null ? "" : p.getEstado()%></td>
                                <td>$<%=p.getPrecioUnitario()%></td>
                                <td><a class="button secondary tiny" href="<%=request.getContextPath()%>/vendedor/inventario?idTienda=<%=idTienda%>&editarId=<%=p.getIdProducto()%>">Editar</a></td>
                            </tr>
                        <% }} %>
                        </tbody>
                    </table>
                </div>
            </article>
            <% } %>
        </section>
    </main>
</div>
</body>
</html>
