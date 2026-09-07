<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Modelo.dto.SolicitudCreditoDTO"%>
<%@page import="java.util.List"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<SolicitudCreditoDTO> solicitudes =
            (List<SolicitudCreditoDTO>) request.getAttribute("solicitudes");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Aprobar / rechazar créditos | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Aprobar / Rechazar créditos</h1>
                    <p>Esta pantalla muestra exclusivamente solicitudes que están pendientes.</p>
                </div>
                <a class="button secondary"
                   href="<%=request.getContextPath()%>/vendedor/historial-creditos">
                    Ver historial
                </a>
            </div>

            <%
                if (solicitudes == null || solicitudes.isEmpty()) {
            %>
            <article class="card empty-state">
                <h2>No hay solicitudes pendientes</h2>
                <p>Todas las solicitudes de tus tiendas ya fueron procesadas.</p>
            </article>
            <%
                } else {
                    for (SolicitudCreditoDTO s : solicitudes) {
            %>
            <article class="card request-card">
                <div>
                    <h3><%=s.getCliente()%></h3>
                    <small>Solicitud #<%=s.getIdSolicitud()%> · <%=s.getTienda()%></small>
                </div>
                <div>
                    <span>Monto</span>
                    <b>$<%=s.getMontoTotal()%></b>
                </div>
                <div>
                    <span>Saldo</span>
                    <b>$<%=s.getSaldoPendiente()%></b>
                </div>
                <div>
                    <span>Solicitud</span>
                    <b><%=s.getFechaSolicitud()%></b>
                </div>
                <div>
                    <span>Estado</span>
                    <b class="pending"><%=s.getEstado()%></b>
                </div>
                <div class="request-actions">
                    <a class="button secondary"
                       href="<%=request.getContextPath()%>/vendedor/creditos/detalle?id=<%=s.getIdSolicitud()%>">
                        Ver detalles
                    </a>

                    <form method="post"
                          action="<%=request.getContextPath()%>/vendedor/creditos/estado">
                        <input type="hidden" name="idSolicitud" value="<%=s.getIdSolicitud()%>">
                        <input type="hidden" name="estado" value="APROBADO">
                        <button class="button primary" type="submit">Aprobar</button>
                    </form>

                    <form method="post"
                          action="<%=request.getContextPath()%>/vendedor/creditos/estado">
                        <input type="hidden" name="idSolicitud" value="<%=s.getIdSolicitud()%>">
                        <input type="hidden" name="estado" value="RECHAZADO">
                        <button class="button danger" type="submit">Rechazar</button>
                    </form>
                </div>
            </article>
            <%
                    }
                }
            %>
        </section>
    </main>
</div>
</body>
</html>
