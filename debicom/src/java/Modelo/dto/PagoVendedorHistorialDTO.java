package Modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pago aplicado a un crédito otorgado por un vendedor.
 */
public class PagoVendedorHistorialDTO {
    private int idPago;
    private int idSolicitud;
    private int idCliente;
    private String cliente;
    private String tienda;
    private BigDecimal montoPagado;
    private LocalDateTime fechaPago;
    private String numeroReferencia;
    private String observaciones;
    private String estadoCredito;

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTienda() { return tienda; }
    public void setTienda(String tienda) { this.tienda = tienda; }

    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public String getNumeroReferencia() { return numeroReferencia; }
    public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstadoCredito() { return estadoCredito; }
    public void setEstadoCredito(String estadoCredito) { this.estadoCredito = estadoCredito; }
}
