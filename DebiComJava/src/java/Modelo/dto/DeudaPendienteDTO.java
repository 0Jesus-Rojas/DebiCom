package Modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DeudaPendienteDTO {
    private int idSolicitud;
    private int idFactura;
    private String tienda;
    private BigDecimal montoOriginal;
    private BigDecimal saldoPendiente;
    private LocalDate fechaVencimiento;
    private String estado;

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }
    public BigDecimal getMontoOriginal() { return montoOriginal; }
    public void setMontoOriginal(BigDecimal montoOriginal) { this.montoOriginal = montoOriginal; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
