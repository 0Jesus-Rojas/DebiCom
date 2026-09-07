<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("idUsuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    String error = (String) request.getAttribute("error");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Registrarse como vendedor | DebiCom</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/vista/CSS/styles.css">
</head>
<body class="app-body">
<div class="app-shell">
    <%@include file="WEB-INF/jspf/sidebar.jspf" %>
    <main class="app-main">
        <section class="page-content">
            <div class="page-heading">
                <div>
                    <h1>Registrarse como vendedor</h1>
                    <p>Convierte tu cuenta de comprador en una cuenta de vendedor.</p>
                </div>
            </div>

            <% if (error != null) { %>
                <article class="card" style="margin-bottom:16px;border-left:4px solid #e74c3c;">
                    <strong>Error:</strong> <%=error%>
                </article>
            <% } %>

            <article class="card">
                <h2>Confirmación</h2>
                <p>Al continuar podrás crear una o varias tiendas y administrar su inventario.</p>
                <form method="post" action="<%=request.getContextPath()%>/comprador/registrarse-vendedor">
                    <div class="form-actions">
                        <button class="button primary" type="submit">Confirmar registro como vendedor</button>
                        <a class="button secondary" href="<%=request.getContextPath()%>/comprador/perfil">Cancelar</a>
                    </div>
                </form>
            </article>
        </section>
    </main>
</div>
</body>
</html>
