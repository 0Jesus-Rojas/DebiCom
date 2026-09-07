<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.dto.TiendaDTO"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<TiendaDTO> tiendas = (List<TiendaDTO>) request.getAttribute("tiendas");
    String error = (String) request.getAttribute("error");
    String mensaje = (String) request.getAttribute("mensaje");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Solicitar crédito | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Solicitar Crédito</h1>
                    <p>Complete el formulario para solicitar un nuevo crédito.</p>
                </div>
            </div>

            <% if (error != null) { %>
                <div class="card" style="margin-bottom:16px; border-left:4px solid #e74c3c;">
                    <strong>Error:</strong> <%=error%>
                </div>
            <% } %>

            <% if (mensaje != null) { %>
                <div class="card" style="margin-bottom:16px; border-left:4px solid #2ecc71;">
                    <strong><%=mensaje%></strong>
                    <% if (request.getAttribute("idSolicitud") != null) { %>
                        <span> ID de solicitud: <%=request.getAttribute("idSolicitud")%></span>
                    <% } %>
                </div>
            <% } %>

            <form class="card" action="<%=request.getContextPath()%>/comprador/solicitar-credito" method="post">
                <div class="dark-grid">
                    <label>Nombre completo
                        <input value="<%=session.getAttribute("nombre") != null ? session.getAttribute("nombre") + " " + session.getAttribute("apellido") : ""%>" readonly>
                    </label>

                    <label>Cliente
                        <input value="<%=request.getAttribute("idCliente") != null ? request.getAttribute("idCliente") : ""%>" readonly>
                    </label>

                    <label class="full">Nombre de la tienda
                        <input
                            type="search"
                            id="tiendaBusqueda"
                            class="store-search"
                            placeholder="Buscar por nombre o dirección..."
                            autocomplete="off"
                            aria-label="Buscar tienda">

                        <select name="idTienda" id="idTienda" required>
                            <option value="">Seleccione una tienda</option>
                            <% if (tiendas != null) {
                                for (TiendaDTO tienda : tiendas) { %>
                                    <option
                                        value="<%=tienda.getIdTienda()%>"
                                        data-store>
                                        <%=tienda.getNombreTienda()%> — <%=tienda.getDireccion()%>
                                    </option>
                            <%  }
                               } %>
                            <option value="" data-empty-search hidden disabled>
                                No hay tiendas que coincidan con la búsqueda
                            </option>
                        </select>
                    </label>

                    <label>Monto solicitado
                        <input type="number" name="monto" min="0.01" step="0.01" placeholder="0.00" required>
                    </label>

                    <label>Plazo para pagar
                        <select name="plazo">
                            <option value="1 mes">1 mes</option>
                            <option value="2 meses">2 meses</option>
                            <option value="3 meses">3 meses</option>
                            <option value="4 meses">4 meses</option>
                            <option value="5 meses">5 meses</option>
                            <option value="6 meses">6 meses</option>
                        </select>
                    </label>

                    <label class="full">Motivo del crédito
                        <textarea name="descripcion" maxlength="255" placeholder="Describa el motivo de su solicitud"></textarea>
                    </label>
                </div>

                <div class="credit-summary">
                    <div><span>Estado inicial</span><b>Pendiente</b></div>
                    <div><span>Fecha de solicitud</span><b>Automática</b></div>
                    <div><span>Vencimiento</span><b>Según plazo seleccionado</b></div>
                </div>

                <div class="form-actions">
                    <button class="button primary" type="submit">Enviar solicitud</button>
                    <a class="button secondary" href="<%=request.getContextPath()%>/comprador/perfil">Cancelar</a>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
