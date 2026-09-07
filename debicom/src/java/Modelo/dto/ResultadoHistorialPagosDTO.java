package Modelo.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResultadoHistorialPagosDTO {
    private BigDecimal totalPagado = BigDecimal.ZERO;
    private List<PagoHistorialDTO> pagos = new ArrayList<>();

    public BigDecimal getTotalPagado() { return totalPagado; }
    public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
    public List<PagoHistorialDTO> getPagos() { return pagos; }
    public void setPagos(List<PagoHistorialDTO> pagos) { this.pagos = pagos; }
}
