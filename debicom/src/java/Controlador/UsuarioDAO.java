package Controlador;

import Modelo.Usuarios;
import Modelo.dto.PerfilDTO;
import Util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

/** DAO de usuarios. Mantiene CRUD legacy y agrega consultas orientadas al backend MVC. */
public class UsuarioDAO {

    private final Conexion conexion = new Conexion();

    public Usuarios consultarUsuario(int idUsuario) {
        String sql = "SELECT id_usuario, nombre, apellido, identificacion, fecha_nacimiento, correo, "
                + "telefono, direccion, password, fecha_vencimiento_clave, autoriza_datos, "
                + "id_tipo_identificacion, id_rol, fecha_registro "
                + "FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando usuario " + idUsuario, e);
        }
        return null;
    }

    /**
     * Busca un usuario únicamente por correo. Permite distinguir "usuario no existe"
     * de "contraseña incorrecta" en la capa web.
     */
    public Usuarios buscarPorCorreo(String correo) {
        String sql = "SELECT id_usuario, nombre, apellido, identificacion, fecha_nacimiento, correo, "
                + "telefono, direccion, password, fecha_vencimiento_clave, autoriza_datos, "
                + "id_tipo_identificacion, id_rol, fecha_registro "
                + "FROM usuarios WHERE LOWER(TRIM(correo)) = LOWER(TRIM(?))";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUsuario(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando usuario por correo.", e);
        }
    }

    /**
     * Autentica por correo y verifica el hash en Java.
     * Mantiene compatibilidad con usuarios antiguos y los migra al primer login correcto.
     */
    public Usuarios autenticar(String correo, String password) {
        Usuarios usuario = buscarPorCorreo(correo);

        if (usuario == null) {
            return null;
        }

        String stored = usuario.getPassword();
        boolean valid;

        if (PasswordUtil.isHashed(stored)) {
            valid = PasswordUtil.verify(password, stored);
        } else {
            valid = java.util.Objects.equals(stored, password);
            if (valid) {
                actualizarPassword(usuario.getIdUsuario(), PasswordUtil.hash(password));
            }
        }

        return valid ? usuario : null;
    }

    /** Actualiza únicamente el hash de contraseña. */
    public boolean actualizarPassword(int idUsuario, String passwordHash) {
        if (!PasswordUtil.isHashed(passwordHash)) {
            throw new IllegalArgumentException(
                    "La contraseña debe recibirse ya convertida a un hash PBKDF2.");
        }

        String sql = "UPDATE usuarios SET password = ? WHERE id_usuario = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando la contraseña.", e);
        }
    }

    /** Perfil completo, incluyendo datos de cliente si existe. */
    public PerfilDTO consultarPerfil(int idUsuario) {
        String sql = "SELECT u.id_usuario, c.id_cliente, u.nombre, u.apellido, u.identificacion, "
                + "ti.nombre_tipo AS tipo_identificacion, u.fecha_nacimiento, u.correo, u.telefono, "
                + "u.direccion, u.autoriza_datos, r.nombre_rol AS rol "
                + "FROM usuarios u "
                + "LEFT JOIN clientes c ON c.id_usuario = u.id_usuario "
                + "INNER JOIN tipo_identificaciones ti ON ti.id_tipo_identificacion = u.id_tipo_identificacion "
                + "LEFT JOIN roles r ON r.id_rol = u.id_rol "
                + "WHERE u.id_usuario = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PerfilDTO dto = new PerfilDTO();
                    dto.setIdUsuario(rs.getInt("id_usuario"));
                    dto.setIdCliente(rs.getObject("id_cliente", Integer.class) == null ? 0 : rs.getInt("id_cliente"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setApellido(rs.getString("apellido"));
                    dto.setIdentificacion(rs.getString("identificacion"));
                    dto.setTipoIdentificacion(rs.getString("tipo_identificacion"));
                    Date nacimiento = rs.getDate("fecha_nacimiento");
                    dto.setFechaNacimiento(nacimiento == null ? null : nacimiento.toLocalDate());
                    dto.setCorreo(rs.getString("correo"));
                    dto.setTelefono(rs.getString("telefono"));
                    dto.setDireccion(rs.getString("direccion"));
                    dto.setAutorizaDatos(rs.getBoolean("autoriza_datos"));
                    dto.setRol(rs.getString("rol"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando perfil", e);
        }
        return null;
    }

    /** Actualiza únicamente los datos editables del perfil; nunca toca la contraseña aquí. */
    public boolean actualizarPerfil(PerfilDTO perfil) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, identificacion = ?, "
                + "fecha_nacimiento = ?, correo = ?, telefono = ?, direccion = ?, autoriza_datos = ? "
                + "WHERE id_usuario = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, perfil.getNombre());
            ps.setString(2, perfil.getApellido());
            ps.setString(3, perfil.getIdentificacion());
            ps.setDate(4, perfil.getFechaNacimiento() == null ? null : Date.valueOf(perfil.getFechaNacimiento()));
            ps.setString(5, perfil.getCorreo());
            ps.setString(6, perfil.getTelefono());
            ps.setString(7, perfil.getDireccion());
            ps.setBoolean(8, perfil.isAutorizaDatos());
            ps.setInt(9, perfil.getIdUsuario());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando perfil", e);
        }
    }

    public Integer obtenerIdClientePorUsuario(int idUsuario) {
        String sql = "SELECT id_cliente FROM clientes WHERE id_usuario = ?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id_cliente") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando el cliente del usuario", e);
        }
    }


    /**
     * Registra un usuario como comprador en una única transacción.
     * Si falla la inserción en clientes, también se revierte usuarios.
     */
    /**
     * Verifica si ya existe un usuario con el correo indicado.
     */
    public boolean existeCorreoRegistrado(String correo) {
        String sql = "SELECT 1 FROM usuarios WHERE LOWER(TRIM(correo)) = LOWER(TRIM(?)) LIMIT 1";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.ERROR_BASE_DATOS,
                    "No fue posible verificar el correo en la base de datos.", e);
        }
    }

    /**
     * Verifica si ya existe un usuario con la identificación indicada.
     */
    public boolean existeIdentificacionRegistrada(String identificacion) {
        String sql = "SELECT 1 FROM usuarios WHERE TRIM(identificacion) = TRIM(?) LIMIT 1";
        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.ERROR_BASE_DATOS,
                    "No fue posible verificar la identificación en la base de datos.", e);
        }
    }

    public int registrarUsuarioComoComprador(Usuarios u) {
        if (u == null) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.REFERENCIA_INVALIDA,
                    "Los datos del usuario son obligatorios.");
        }

        String correo = u.getCorreo() == null ? "" : u.getCorreo().trim();
        String identificacion = u.getIdentificacion() == null ? "" : u.getIdentificacion().trim();

        if (correo.isBlank()) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.REFERENCIA_INVALIDA,
                    "El correo es obligatorio.");
        }

        if (identificacion.isBlank()) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.REFERENCIA_INVALIDA,
                    "La identificación es obligatoria.");
        }

        u.setCorreo(correo);
        u.setIdentificacion(identificacion);

        // Validación explícita: no se interpreta un error JDBC genérico como duplicado.
        if (existeCorreoRegistrado(correo)) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.CORREO_DUPLICADO,
                    "El correo electrónico ya está registrado.");
        }

        if (existeIdentificacionRegistrada(identificacion)) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.IDENTIFICACION_DUPLICADA,
                    "La identificación ya está registrada.");
        }

        String insertUser = "INSERT INTO usuarios("
                + "nombre, apellido, identificacion, fecha_nacimiento, correo, telefono, direccion, "
                + "password, fecha_vencimiento_clave, autoriza_datos, id_tipo_identificacion, id_rol, fecha_registro"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 180 DAY), ?, ?, NULL, CURRENT_TIMESTAMP)";

        String insertCliente = "INSERT INTO clientes(credito_actual, id_usuario) VALUES (0, ?)";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idUsuario;

                try (PreparedStatement ps = conn.prepareStatement(
                        insertUser, java.sql.Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, u.getNombre());
                    ps.setString(2, u.getApellido());
                    ps.setString(3, identificacion);
                    ps.setDate(4, u.getFechaNacimiento());
                    ps.setString(5, correo);
                    ps.setString(6, u.getTelefono() == null ? "" : u.getTelefono());
                    ps.setString(7, u.getDireccion() == null ? "" : u.getDireccion());
                    ps.setString(8, protegerPassword(u.getPassword()));
                    ps.setBoolean(9, u.isAutorizaDatos());
                    ps.setInt(10, u.getIdTipoIdentificacion());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se generó el identificador del usuario.");
                        }
                        idUsuario = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertCliente)) {
                    ps.setInt(1, idUsuario);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("No se creó el registro del comprador.");
                    }
                }

                conn.commit();
                return idUsuario;

            } catch (SQLIntegrityConstraintViolationException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                // 1062 = duplicate key en MySQL. El chequeo posterior identifica cuál.
                if (e.getErrorCode() == 1062) {
                    if (existeCorreoEnConexion(conn, correo)) {
                        throw new ExcepcionRegistroUsuario(
                                ExcepcionRegistroUsuario.Tipo.CORREO_DUPLICADO,
                                "El correo electrónico ya está registrado.", e);
                    }
                    if (existeIdentificacionEnConexion(conn, identificacion)) {
                        throw new ExcepcionRegistroUsuario(
                                ExcepcionRegistroUsuario.Tipo.IDENTIFICACION_DUPLICADA,
                                "La identificación ya está registrada.", e);
                    }
                    throw new ExcepcionRegistroUsuario(
                            ExcepcionRegistroUsuario.Tipo.ERROR_BASE_DATOS,
                            "La base de datos rechazó el registro por una restricción de unicidad.", e);
                }

                // 1452 y similares: una referencia/catálogo requerido no existe.
                if (e.getErrorCode() == 1452) {
                    throw new ExcepcionRegistroUsuario(
                            ExcepcionRegistroUsuario.Tipo.REFERENCIA_INVALIDA,
                            "El tipo de identificación seleccionado no existe en la base de datos.", e);
                }

                throw new ExcepcionRegistroUsuario(
                        ExcepcionRegistroUsuario.Tipo.ERROR_BASE_DATOS,
                        "La base de datos rechazó el registro del usuario.", e);

            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw new ExcepcionRegistroUsuario(
                        ExcepcionRegistroUsuario.Tipo.ERROR_BASE_DATOS,
                        "Ocurrió un error de base de datos al registrar el usuario.", e);
            } catch (ExcepcionRegistroUsuario e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException e) {
            throw new ExcepcionRegistroUsuario(
                    ExcepcionRegistroUsuario.Tipo.ERROR_BASE_DATOS,
                    "No fue posible conectar con la base de datos.", e);
        }
    }

    private boolean existeCorreoEnConexion(Connection conn, String correo) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE LOWER(TRIM(correo)) = LOWER(TRIM(?)) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existeIdentificacionEnConexion(Connection conn, String identificacion) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE TRIM(identificacion) = TRIM(?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Determina si el usuario puede registrarse como vendedor.
     *
     * Se bloquea únicamente cuando el usuario ya tiene el rol VENDEDOR.
     * Los compradores del esquema actual tienen id_rol NULL; además se admite
     * el nombre COMPRADOR por si una instalación utiliza ese rol explícito.
     */
    public boolean puedeRegistrarseComoVendedor(int idUsuario) {
        String sql = "SELECT r.nombre_rol "
                + "FROM usuarios u "
                + "LEFT JOIN roles r ON r.id_rol = u.id_rol "
                + "WHERE u.id_usuario = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                String rol = rs.getString("nombre_rol");

                return rol == null
                        || "COMPRADOR".equalsIgnoreCase(rol.trim());
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error verificando el rol del usuario.", e);
        }
    }

    /**
     * Convierte al usuario en vendedor dentro de una transacción.
     * El id del rol se resuelve por nombre, nunca se hardcodea.
     */
    public int registrarComoVendedor(int idUsuario) {
        String buscarRol = "SELECT id_rol FROM roles WHERE UPPER(nombre_rol) = 'VENDEDOR' LIMIT 1";
        String actualizar = "UPDATE usuarios SET id_rol = ? "
                + "WHERE id_usuario = ? "
                + "AND (id_rol IS NULL OR id_rol = ("
                + "    SELECT r_comprador.id_rol FROM roles r_comprador "
                + "    WHERE UPPER(r_comprador.nombre_rol) = 'COMPRADOR' LIMIT 1"
                + "))";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer idRol = null;

                try (PreparedStatement ps = conn.prepareStatement(buscarRol);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idRol = rs.getInt("id_rol");
                    }
                }

                if (idRol == null) {
                    throw new IllegalStateException("No existe el rol Vendedor en la base de datos.");
                }

                int actualizado;
                try (PreparedStatement ps = conn.prepareStatement(actualizar)) {
                    ps.setInt(1, idRol);
                    ps.setInt(2, idUsuario);
                    actualizado = ps.executeUpdate();
                }

                if (actualizado != 1) {
                    conn.rollback();
                    return 0;
                }

                conn.commit();
                return idRol;
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                if (e instanceof IllegalStateException ise) {
                    throw ise;
                }
                throw new IllegalStateException("No fue posible registrar al usuario como vendedor.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error registrando al usuario como vendedor.", e);
        }
    }

    public boolean insertarUsuario(Usuarios u) {
        String sql = "INSERT INTO usuarios(nombre, apellido, identificacion, fecha_nacimiento, correo, telefono, "
                + "direccion, password, fecha_vencimiento_clave, autoriza_datos, id_tipo_identificacion, id_rol, fecha_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre()); ps.setString(2, u.getApellido()); ps.setString(3, u.getIdentificacion());
            ps.setDate(4, u.getFechaNacimiento()); ps.setString(5, u.getCorreo()); ps.setString(6, u.getTelefono());
            ps.setString(7, u.getDireccion()); ps.setString(8, protegerPassword(u.getPassword())); ps.setDate(9, u.getFechaVencimientoClave());
            ps.setBoolean(10, u.isAutorizaDatos()); ps.setInt(11, u.getIdTipoIdentificacion());
            if (u.getIdRol() > 0) ps.setInt(12, u.getIdRol()); else ps.setNull(12, java.sql.Types.INTEGER);
            if (u.getFechaRegistro() != null) ps.setDate(13, u.getFechaRegistro()); else ps.setDate(13, new Date(System.currentTimeMillis()));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { return false; }
    }

    public boolean actualizarUsuario(Usuarios u) {
        String sql = "UPDATE usuarios SET nombre=?, apellido=?, identificacion=?, fecha_nacimiento=?, correo=?, telefono=?, "
                + "direccion=?, password=?, fecha_vencimiento_clave=?, autoriza_datos=?, id_tipo_identificacion=?, id_rol=? WHERE id_usuario=?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre()); ps.setString(2, u.getApellido()); ps.setString(3, u.getIdentificacion());
            ps.setDate(4, u.getFechaNacimiento()); ps.setString(5, u.getCorreo()); ps.setString(6, u.getTelefono());
            ps.setString(7, u.getDireccion()); ps.setString(8, protegerPassword(u.getPassword())); ps.setDate(9, u.getFechaVencimientoClave());
            ps.setBoolean(10, u.isAutorizaDatos()); ps.setInt(11, u.getIdTipoIdentificacion());
            if (u.getIdRol() > 0) ps.setInt(12, u.getIdRol()); else ps.setNull(12, java.sql.Types.INTEGER);
            ps.setInt(13, u.getIdUsuario());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { return false; }
    }

    public boolean eliminarUsuario(int idUsuario) {
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM usuarios WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario); return ps.executeUpdate() == 1;
        } catch (SQLException e) { return false; }
    }

    private String protegerPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía.");
        }
        return PasswordUtil.isHashed(password)
                ? password
                : PasswordUtil.hash(password);
    }

    private Usuarios mapUsuario(ResultSet rs) throws SQLException {
        Usuarios u = new Usuarios();
        u.setIdUsuario(rs.getInt("id_usuario")); u.setNombre(rs.getString("nombre")); u.setApellido(rs.getString("apellido"));
        u.setIdentificacion(rs.getString("identificacion")); u.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        u.setCorreo(rs.getString("correo")); u.setTelefono(rs.getString("telefono")); u.setDireccion(rs.getString("direccion"));
        u.setPassword(rs.getString("password")); u.setFechaVencimientoClave(rs.getDate("fecha_vencimiento_clave"));
        u.setAutorizaDatos(rs.getBoolean("autoriza_datos")); u.setIdTipoIdentificacion(rs.getInt("id_tipo_identificacion"));
        Integer rol = rs.getObject("id_rol", Integer.class); u.setIdRol(rol == null ? 0 : rol); u.setFechaRegistro(rs.getDate("fecha_registro"));
        return u;
    }
}
