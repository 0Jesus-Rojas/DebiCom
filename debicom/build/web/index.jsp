<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="Controlador.TipoIdentificacionDAO" %>
<%@ page import="Modelo.TipoIdentificaciones" %>
<%@ page import="java.util.ArrayList" %>

<%
    TipoIdentificacionDAO tipoDAO = new TipoIdentificacionDAO();
ArrayList<TipoIdentificaciones> tipos
            = tipoDAO.consultarTiposIdentificacion();

%>

<!DOCTYPE html>

<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Inicio | DebiCom</title>
        <link rel="stylesheet" href="vista/CSS/styles.css" />
    </head>
    <body data-auth-tab="<%= request.getAttribute("authTab") == null ? "" : request.getAttribute("authTab") %>" data-auth-error="<%= request.getAttribute("authMessage") != null ? "true" : "false" %>">

        <header class="navbar">
            <div class="brand">DebiCom</div>
            <button class="btn-open-modal" id="openModalBtn" type="button">
                Iniciar Sesion / Registro
            </button>
        </header>

        <main class="hero">
            <h1>Bienvenido a DebiCom</h1>
            <p>Administra tus créditos, tiendas y pagos desde una sola plataforma.</p>
        </main>

        <!-- Modal de Autenticacion -->
        <div class="modal-overlay" id="authModal" role="dialog" aria-modal="true" aria-hidden="true">
            <div class="modal-container">

                <button class="btn-close" id="closeModalBtn" type="button" aria-label="Cerrar">
                    X
                </button>

                <%
                    String authMessage = (String) request.getAttribute("authMessage");
                    String authType = (String) request.getAttribute("authType");
                    if (authType == null || authType.isBlank()) {
                        authType = "error";
                    }
                %>

                <% if (authMessage != null && !authMessage.isBlank()) { %>
                    <div id="authAlert"
                         class="auth-alert <%= "warning".equals(authType) ? "warning" : "error" %>"
                         role="alert"
                         aria-live="polite">
                        <span class="auth-alert-icon"><%= "warning".equals(authType) ? "!" : "!" %></span>
                        <span><%= authMessage %></span>
                        <button type="button" class="auth-alert-close" aria-label="Cerrar alerta">&times;</button>
                    </div>
                <% } %>

                <div class="tab-buttons">
                    <button class="tab-btn active" type="button" onclick="switchTab('login', this)">
                        Iniciar Sesion
                    </button>

                    <button class="tab-btn" type="button" onclick="switchTab('register', this)">
                        Registro
                    </button>
                </div>

                <!-- Formulario de Login -->
                <form action="login" id="login" class="tab-content active" method="post">

                    <div class="form-group">
                        <label for="correo">Correo Electronico</label>
                        <input
                            type="email"
                            name="correo"
                            id="correo"
                            required
                            placeholder="correo@ejemplo.com"
                            autocomplete="email">
                    </div>

                    <div class="form-group">
                        <label for="clave">Contrasena</label>
                        <input
                            type="password"
                            name="clave"
                            id="clave"
                            required
                            placeholder="********"
                            autocomplete="current-password">
                    </div>

                    <button type="submit" class="btn-submit">
                        Ingresar
                    </button>
                </form>

                <!-- Formulario de Registro -->
                <form action="register" id="register" class="tab-content" method="post">

                    <div class="form-grid">

                        <div class="form-group">
                            <label for="reg-nombre">Nombre</label>
                            <input
                                type="text"
                                name="nombre"
                                id="reg-nombre"
                                required
                                autocomplete="given-name">
                        </div>

                        <div class="form-group">
                            <label for="reg-apellido">Apellido</label>
                            <input
                                type="text"
                                name="apellido"
                                id="reg-apellido"
                                required
                                autocomplete="family-name">
                        </div>

                    </div>

                    <div class="form-grid">

                        <div class="form-group">
                            <label for="reg-tipo-id">Tipo Identificacion</label>

                            <select name="tipo_id" id="reg-tipo-id" required>

                                <% for (TipoIdentificaciones tipo : tipos) {%>
                                
                                <option value="<%= tipo.getIdTipoIdentificacion()%>">
                                    <%= tipo.getNombreTipo()%>
                                </option>

                                <% }%>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="reg-id">N Identificacion</label>

                            <input
                                type="text"
                                name="num_id"
                                id="reg-id"
                                required>
                        </div>

                    </div>

                    <div class="form-grid">

                        <div class="form-group">
                            <label for="reg-fecha-nac">Fecha Nacimiento</label>

                            <input
                                type="date"
                                name="fecha_nac"
                                id="reg-fecha-nac"
                                required>
                        </div>

                        <div class="form-group">
                            <label for="reg-telefono">Telefono</label>

                            <input
                                type="tel"
                                name="telefono"
                                id="reg-telefono"
                                required
                                autocomplete="tel">
                        </div>

                    </div>

                    <div class="form-group">
                        <label for="reg-direccion">Direccion</label>

                        <input
                            type="text"
                            name="direccion"
                            id="reg-direccion"
                            required>
                    </div>

                    <div class="form-group">
                        <label for="reg-email">Correo Electronico</label>

                        <input
                            type="email"
                            name="correo"
                            id="reg-email"
                            required
                            autocomplete="email">
                    </div>

                    <div class="form-group">
                        <label for="reg-password">Contrasena</label>

                        <input
                            type="password"
                            name="clave"
                            id="reg-password"
                            required
                            autocomplete="new-password">
                    </div>

                    <div class="form-group checkbox-group">

                        <input
                            type="checkbox"
                            name="autoriza"
                            id="reg-autoriza"
                            value="true"
                            required>

                        <label for="reg-autoriza">
                            Autorizo el tratamiento de datos personales
                        </label>

                    </div>

                    <button type="submit" class="btn-submit">
                        Registrarse
                    </button>

                </form>
            </div>
        </div>

        <script src="vista/JavaScript/main.js" defer></script>

    </body>

</html>
