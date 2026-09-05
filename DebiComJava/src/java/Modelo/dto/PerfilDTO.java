package Modelo.dto;

import java.time.LocalDate;

/** Datos públicos/editables del perfil del comprador o vendedor. */
public class PerfilDTO {
    private int idUsuario;
    private int idCliente;
    private String nombre;
    private String apellido;
    private String identificacion;
    private String tipoIdentificacion;
    private LocalDate fechaNacimiento;
    private String correo;
    private String telefono;
    private String direccion;
    private boolean autorizaDatos;
    private String rol;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public String getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(String tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isAutorizaDatos() { return autorizaDatos; }
    public void setAutorizaDatos(boolean autorizaDatos) { this.autorizaDatos = autorizaDatos; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
