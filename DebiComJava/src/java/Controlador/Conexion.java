package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proveedor central de conexiones JDBC para la aplicación DebiCom.
 *
 * Compatible con el proyecto actual y preparado para los nuevos DAOs.
 * La configuración puede recibirse mediante propiedades del sistema o
 * variables de entorno; de no existir, se usan los valores locales del
 * proyecto actual.
 */
public class Conexion {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String HOST = obtenerConfig("DEBICOM_DB_HOST", "localhost");
    private static final String PORT = obtenerConfig("DEBICOM_DB_PORT", "3306");
    private static final String DATABASE = obtenerConfig("DEBICOM_DB_NAME", "debicom");
    private static final String USER = obtenerConfig("DEBICOM_DB_USER", "root");
    private static final String PASSWORD = obtenerConfig("DEBICOM_DB_PASSWORD", "");

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Bogota";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "No se encontró el driver JDBC de MySQL: " + e.getMessage());
        }
    }

    /**
     * Constructor conservado para no romper el código existente del proyecto.
     */
    public Conexion() {
    }

    /**
     * Abre una conexión nueva. El consumidor debe cerrarla con try-with-resources.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Método legado. Los DAOs actuales utilizan este nombre.
     *
     * @return una conexión nueva o null si no pudo abrirse.
     */
    public Connection getconn() {
        try {
            return getConnection();
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos '" + DATABASE + "': "
                    + e.getMessage());
            return null;
        }
    }

    public static String getDatabaseName() {
        return DATABASE;
    }

    private static String obtenerConfig(String variable, String valorDefecto) {
        String propiedad = System.getProperty(variable);
        if (propiedad != null && !propiedad.isBlank()) {
            return propiedad;
        }

        String entorno = System.getenv(variable);
        if (entorno != null && !entorno.isBlank()) {
            return entorno;
        }

        return valorDefecto;
    }
}
