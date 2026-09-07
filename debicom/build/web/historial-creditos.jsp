<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.SolicitudCreditoDTO"%>
<%@page import="java.util.List"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<SolicitudCreditoDTO> historial =
            (List<SolicitudCreditoDTO>) request.getAttribute("historialCreditos");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Historial de créditos | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Historial de créditos</h1>
                    <p>Consulta todos los créditos otorgados históricamente por tus tiendas.</p>
                </div>
                <a class="button secondary"
                   href="<%=request.getContextPath()%>/vendedor/creditos">
                    Solicitudes pendientes
                </a>
            </div>

            <article class="card">
                <div class="filter-toolbar">
                    <input
                        type="search"
                        class="filter-input"
                        data-table-filter="#tablaHistorialCreditos"
                        placeholder="Buscar por número de crédito..."
                        aria-label="Buscar crédito por número">
                </div>

                <div class="history-summary">
                    <span>Créditos otorgados</span>
                    <strong><%=historial == null ? 0 : historial.size()%></strong>
                </div>

                <div class="table-wrap">
                    <table id="tablaHistorialCreditos">
                        <thead>
                        <tr>
                            <th>Crédito</th>
                            <th>Cliente</th>
                            <th>Tienda</th>
                            <th>Monto</th>
                            <th>Saldo pendiente</th>
                            <th>Fecha otorgamiento</th>
                            <th>Vencimiento</th>
                            <th>Estado</th>
                            <th>Detalle</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            if (historial == null || historial.isEmpty()) {
                        %>
                        <tr data-filter-empty>
                            <td colspan="9">No hay créditos otorgados para mostrar.</td>
                        </tr>
                        <%
                            } else {
                                for (SolicitudCreditoDTO c : historial) {
                        %>
                        <tr data-filter-value="<%=c.getIdSolicitud()%>">
                            <td>#<%=c.getIdSolicitud()%></td>
                            <td><%=c.getCliente()%></td>
                            <td><%=c.getTienda()%></td>
                            <td>$<%=c.getMontoTotal()%></td>
                            <td><strong>$<%=c.getSaldoPendiente()%></strong></td>
                            <td><%=c.getFechaAprobacion() != null
                                    ? c.getFechaAprobacion()
                                    : c.getFechaSolicitud()%></td>
                            <td><%=c.getFechaVencimiento() == null
                                    ? "" : c.getFechaVencimiento()%></td>
                            <td>
                                <span class="badge <%=c.getEstado() == null
                                        ? "" : c.getEstado().toLowerCase()%>">
                                    <%=c.getEstado()%>
                                </span>
                            </td>
                            <td>
                                <a class="button secondary tiny"
                                   href="<%=request.getContextPath()%>/vendedor/creditos/detalle?id=<%=c.getIdSolicitud()%>">
                                    Ver
                                </a>
                            </td>
                        </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </article>
        </section>
    </main>
</div>
</body>
</html>
