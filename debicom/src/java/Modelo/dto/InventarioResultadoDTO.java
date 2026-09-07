package Modelo.dto;

import java.util.ArrayList;
import java.util.List;

public class InventarioResultadoDTO {
    private InventarioMetricasDTO metricas = new InventarioMetricasDTO();
    private List<ProductoDTO> productos = new ArrayList<>();

    public InventarioMetricasDTO getMetricas() { return metricas; }
    public void setMetricas(InventarioMetricasDTO metricas) { this.metricas = metricas; }
    public List<ProductoDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoDTO> productos) { this.productos = productos; }
}
