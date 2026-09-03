<%-- 
    Document   : panel
    Created on : 3/09/2026, 3:04:44 p. m.
    Author     : Jesus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            if(session.getAttribute("correo") == null){
                response.sendRedirect("index.html");
                return;
            }
        %>
        <h1>Bienvenido, <%= session.getAttribute("nombre") %> <%= session.getAttribute("apellido") %></h1>
    </body>
</html>
