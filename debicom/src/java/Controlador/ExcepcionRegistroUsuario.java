package Controlador;

/**
 * Excepción de dominio para errores conocidos durante el registro de usuarios.
 */
public class ExcepcionRegistroUsuario extends RuntimeException {

    public enum Tipo {
        CORREO_DUPLICADO,
        IDENTIFICACION_DUPLICADA,
        REFERENCIA_INVALIDA,
        ERROR_BASE_DATOS
    }

    private final Tipo tipo;

    public ExcepcionRegistroUsuario(Tipo tipo, String message) {
        super(message);
        this.tipo = tipo;
    }

    public ExcepcionRegistroUsuario(Tipo tipo, String message, Throwable cause) {
        super(message, cause);
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }
}
