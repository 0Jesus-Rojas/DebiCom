<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.TiendaDTO"%>
<%@page import="java.util.List"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    TiendaDTO tienda = (TiendaDTO) request.getAttribute("tienda");
    List<TiendaDTO> tiendas = (List<TiendaDTO>) request.getAttribute("tiendas");
    String error = (String) request.getAttribute("error");
    String ok = (String) request.getAttribute("ok");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Gestionar tienda | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Gestionar tiendas</h1>
                    <p>Crea varias tiendas y administra cada una de forma independiente.</p>
                </div>
            </div>

            <% if ("creada".equals(ok)) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;">
                    <strong>La tienda fue creada correctamente.</strong>
                </article>
            <% } else if ("actualizada".equals(ok)) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #2ecc71;">
                    <strong>La tienda fue actualizada correctamente.</strong>
                </article>
            <% } %>

            <% if (error != null) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #e74c3c;">
                    <strong>Error:</strong> <%=error%>
                </article>
            <% } %>

            <% if (tienda != null) { %>
            <div class="metric-row">
                <article class="metric">
                    <span class="label">Ventas totales</span>
                    <strong>$<%=tienda.getVentasTotales().setScale(2).toPlainString()%></strong>
                </article>
                <article class="metric">
                    <span class="label">Créditos otorgados</span>
                    <strong><%=tienda.getCreditosOtorgados()%></strong>
                </article>
                <article class="metric success">
                    <span class="label">Clientes activos</span>
                    <strong><%=tienda.getClientesActivos()%></strong>
                </article>
                <article class="metric danger">
                    <span class="label">Productos</span>
                    <strong><%=tienda.getTotalProductos()%></strong>
                </article>
            </div>

            <% } %>

            <article class="card">
                <h2>Mis tiendas</h2>
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Tienda</th>
                                <th>NIT</th>
                                <th>Dirección</th>
                                <th>Teléfono</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% if (tiendas == null || tiendas.isEmpty()) { %>
                            <tr><td colspan="5">Aún no tienes tiendas registradas.</td></tr>
                        <% } else {
                            for (TiendaDTO t : tiendas) { %>
                            <tr>
                                <td><b><%=t.getNombreTienda()%></b></td>
                                <td><%=t.getNit()%></td>
                                <td><%=t.getDireccion()%></td>
                                <td><%=t.getTelefono()%></td>
                                <td>
                                    <a class="button secondary tiny"
                                       href="<%=request.getContextPath()%>/vendedor/tienda?idTienda=<%=t.getIdTienda()%>">Gestionar</a>
                                    <a class="button secondary tiny"
                                       href="<%=request.getContextPath()%>/vendedor/inventario?idTienda=<%=t.getIdTienda()%>">Inventario</a>
                                </td>
                            </tr>
                        <% }} %>
                        </tbody>
                    </table>
                </div>
            </article>

            <div class="store-create-actions">
                <button
                    class="button primary store-create-toggle"
                    id="toggleCrearTienda"
                    type="button"
                    aria-expanded="false"
                    aria-controls="formCrearTienda">
                    + Crear Tienda
                </button>
            </div>

            <article class="card store-create" id="formCrearTienda" aria-hidden="true">
                <div class="page-heading" style="margin-bottom:16px;">
                    <div>
                        <h2>Crear nueva tienda</h2>
                        <p>Registra una nueva tienda y luego administra su inventario.</p>
                    </div>
                </div>
                <form class="dark-grid" method="post" action="<%=request.getContextPath()%>/vendedor/tienda">
                    <input type="hidden" name="action" value="crear">
                    <label>Nombre de la tienda
                        <input name="nombreTienda" maxlength="100" required placeholder="Ej. DebiCom Centro">
                    </label>
                    <label>NIT
                        <input name="nit" maxlength="45" required placeholder="900123456-1">
                    </label>
                    <label>Dirección
                        <input name="direccion" maxlength="150" required>
                    </label>
                    <label>Teléfono
                        <input name="telefono" maxlength="30" required>
                    </label>
                    <div class="form-actions">
                        <button class="button primary" type="submit">Crear tienda</button>
                    </div>
                </form>
            </article>


            <% if (tienda != null) { %>
            <article class="card">
                <h2>Configuración: <%=tienda.getNombreTienda()%></h2>
                <form class="dark-grid" method="post" action="<%=request.getContextPath()%>/vendedor/tienda">
                    <input type="hidden" name="action" value="actualizar">
                    <input type="hidden" name="idTienda" value="<%=tienda.getIdTienda()%>">
                    <label>Nombre de la tienda
                        <input name="nombreTienda" maxlength="100" value="<%=tienda.getNombreTienda()%>" required>
                    </label>
                    <label>NIT
                        <input name="nit" maxlength="45" value="<%=tienda.getNit()%>" required>
                    </label>
                    <label>Dirección
                        <input name="direccion" maxlength="150" value="<%=tienda.getDireccion()%>" required>
                    </label>
                    <label>Teléfono
                        <input name="telefono" maxlength="30" value="<%=tienda.getTelefono()%>" required>
                    </label>
                    <div class="form-actions">
                        <button class="button primary" type="submit">Guardar cambios</button>
                    </div>
                </form>
            </article>
            <% } %>
        </section>
    </main>
</div>


<script>
document.addEventListener("DOMContentLoaded", function () {
    const button = document.getElementById("toggleCrearTienda");
    const form = document.getElementById("formCrearTienda");

    if (!button || !form) return;

    button.addEventListener("click", function () {
        const open = form.classList.toggle("is-open");
        form.setAttribute("aria-hidden", String(!open));
        button.setAttribute("aria-expanded", String(open));
        button.textContent = open ? "× Ocultar formulario" : "+ Crear Tienda";

        if (open) {
            form.scrollIntoView({behavior:"smooth", block:"start"});
        }
    });
});
</script>

</body>
</html>
