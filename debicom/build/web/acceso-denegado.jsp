<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String motivo = request.getParameter("motivo");
    String mensaje;

    if ("sin-rol".equalsIgnoreCase(motivo)) {
        mensaje = "Tu usuario está autenticado, pero todavía no tiene un rol asignado.";
    } else if ("rol".equalsIgnoreCase(motivo)) {
        mensaje = "Tu rol actual no tiene permisos para acceder a esta sección.";
    } else {
        mensaje = "No tienes permisos para acceder a esta sección.";
    }
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Acceso denegado | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
    <div class="app-shell">
        <%@include file="WEB-INF/jspf/sidebar.jspf" %>
        <main class="app-main">
            <section class="page-content">
                <article class="card">
                    <h1>Acceso no disponible</h1>
                    <p><%=mensaje%></p>

                    <div class="form-actions">
                        <a class="button primary"
                           href="<%=request.getContextPath()%>/comprador/estado-credito">
                            Ir al dashboard
                        </a>

                        <a class="button secondary"
                           href="<%=request.getContextPath()%>/comprador/perfil">
                            Ver mi perfil
                        </a>
                    </div>
                </article>
            </section>
        </main>
    </div>
</body>
</html>
