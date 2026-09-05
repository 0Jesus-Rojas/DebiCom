package Modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreditoPendienteDTO {
    private int idSolicitud;
    private int idCliente;
    private String cliente;
    private int idTienda;
    private String tienda;
    private BigDecimal montoOriginal;
    private BigDecimal saldoPendiente;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaVencimiento;
    private long diasMora;

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public int getIdTienda() { return idTienda; }
    public void setIdTienda(int idTienda) { this.idTienda = idTienda; }
    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }
    public BigDecimal getMontoOriginal() { return montoOriginal; }
    public void setMontoOriginal(BigDecimal montoOriginal) { this.montoOriginal = montoOriginal; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public long getDiasMora() { return diasMora; }
    public void setDiasMora(long diasMora) { this.diasMora = diasMora; }
}
