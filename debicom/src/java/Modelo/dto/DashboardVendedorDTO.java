package Modelo.dto;

import java.math.BigDecimal;

public class DashboardVendedorDTO {
    private long totalClientes;
    private long solicitudesNuevas;
    private long solicitudesPendientes;
    private long solicitudesAprobadas;
    private BigDecimal pagosRecibidos = BigDecimal.ZERO;
    private BigDecimal deudaPendiente = BigDecimal.ZERO;

    public long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(long totalClientes) { this.totalClientes = totalClientes; }
    public long getSolicitudesNuevas() { return solicitudesNuevas; }
    public void setSolicitudesNuevas(long solicitudesNuevas) { this.solicitudesNuevas = solicitudesNuevas; }
    public long getSolicitudesPendientes() { return solicitudesPendientes; }
    public void setSolicitudesPendientes(long solicitudesPendientes) { this.solicitudesPendientes = solicitudesPendientes; }
    public long getSolicitudesAprobadas() { return solicitudesAprobadas; }
    public void setSolicitudesAprobadas(long solicitudesAprobadas) { this.solicitudesAprobadas = solicitudesAprobadas; }
    public BigDecimal getPagosRecibidos() { return pagosRecibidos; }
    public void setPagosRecibidos(BigDecimal pagosRecibidos) { this.pagosRecibidos = pagosRecibidos; }
    public BigDecimal getDeudaPendiente() { return deudaPendiente; }
    public void setDeudaPendiente(BigDecimal deudaPendiente) { this.deudaPendiente = deudaPendiente; }
}
