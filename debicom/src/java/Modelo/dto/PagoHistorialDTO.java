package Modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoHistorialDTO {
    private int idPago;
    private int idFactura;
    private String numeroFactura;
    private String numeroReferenciaPago;
    private BigDecimal montoPagado;
    private LocalDateTime fechaPago;
    private String tipoPago;
    private String origen;
    private String tienda;
    private String observaciones;

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getNumeroReferenciaPago() { return numeroReferenciaPago; }
    public void setNumeroReferenciaPago(String numeroReferenciaPago) { this.numeroReferenciaPago = numeroReferenciaPago; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
