<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.PagoVendedorHistorialDTO"%>
<%@page import="java.util.List"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<PagoVendedorHistorialDTO> pagos =
            (List<PagoVendedorHistorialDTO>) request.getAttribute("pagosVendedor");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Historial de pagos | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Historial de pagos</h1>
                    <p>Pagos recibidos de clientes sobre los créditos otorgados por tus tiendas.</p>
                </div>
                <a class="button secondary"
                   href="<%=request.getContextPath()%>/vendedor/historial-creditos">
                    Historial de créditos
                </a>
            </div>

            <article class="card history-total">
                <span>Total recibido</span>
                <strong>$<%=request.getAttribute("totalPagado") == null
                        ? "0.00" : request.getAttribute("totalPagado")%></strong>
            </article>

            <article class="card">
                <div class="filter-toolbar">
                    <input
                        type="search"
                        class="filter-input"
                        data-table-filter="#tablaHistorialPagos"
                        placeholder="Buscar por número de pago..."
                        aria-label="Buscar pago por número">
                </div>

                <div class="table-wrap">
                    <table id="tablaHistorialPagos">
                        <thead>
                        <tr>
                            <th>Fecha</th>
                            <th>Pago</th>
                            <th>Crédito</th>
                            <th>Cliente</th>
                            <th>Tienda</th>
                            <th>Monto</th>
                            <th>Referencia</th>
                            <th>Estado crédito</th>
                            <th>Observaciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            if (pagos == null || pagos.isEmpty()) {
                        %>
                        <tr data-filter-empty>
                            <td colspan="9">No hay pagos registrados para tus créditos.</td>
                        </tr>
                        <%
                            } else {
                                for (PagoVendedorHistorialDTO p : pagos) {
                        %>
                        <tr data-filter-value="<%=p.getIdPago()%>">
                            <td><%=p.getFechaPago() == null ? "" : p.getFechaPago()%></td>
                            <td>#<%=p.getIdPago()%></td>
                            <td>
                                <a href="<%=request.getContextPath()%>/vendedor/creditos/detalle?id=<%=p.getIdSolicitud()%>">
                                    #<%=p.getIdSolicitud()%>
                                </a>
                            </td>
                            <td><%=p.getCliente()%></td>
                            <td><%=p.getTienda()%></td>
                            <td><strong>$<%=p.getMontoPagado()%></strong></td>
                            <td><%=p.getNumeroReferencia() == null
                                    ? "" : p.getNumeroReferencia()%></td>
                            <td><span class="badge"><%=p.getEstadoCredito()%></span></td>
                            <td><%=p.getObservaciones() == null
                                    ? "" : p.getObservaciones()%></td>
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
