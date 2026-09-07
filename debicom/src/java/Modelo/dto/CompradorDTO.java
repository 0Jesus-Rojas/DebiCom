package Modelo.dto;

/** Datos mínimos de un comprador para operaciones del vendedor. */
public class CompradorDTO {
    private int idCliente;
    private String nombreCompleto;
    private String identificacion;

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
}
