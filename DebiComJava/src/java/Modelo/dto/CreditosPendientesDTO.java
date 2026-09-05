package Modelo.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreditosPendientesDTO {
    private long creditosActivos;
    private BigDecimal dineroTotalPorCobrar = BigDecimal.ZERO;
    private long clientesConDeuda;
    private List<CreditoPendienteDTO> creditos = new ArrayList<>();

    public long getCreditosActivos() { return creditosActivos; }
    public void setCreditosActivos(long creditosActivos) { this.creditosActivos = creditosActivos; }
    public BigDecimal getDineroTotalPorCobrar() { return dineroTotalPorCobrar; }
    public void setDineroTotalPorCobrar(BigDecimal dineroTotalPorCobrar) { this.dineroTotalPorCobrar = dineroTotalPorCobrar; }
    public long getClientesConDeuda() { return clientesConDeuda; }
    public void setClientesConDeuda(long clientesConDeuda) { this.clientesConDeuda = clientesConDeuda; }
    public List<CreditoPendienteDTO> getCreditos() { return creditos; }
    public void setCreditos(List<CreditoPendienteDTO> creditos) { this.creditos = creditos; }
}
