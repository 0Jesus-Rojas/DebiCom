package Modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DetalleCreditoDTO {
    private SolicitudCreditoDTO solicitud;
    private List<ProductoDTO> productos = new ArrayList<>();

    public SolicitudCreditoDTO getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudCreditoDTO solicitud) { this.solicitud = solicitud; }
    public List<ProductoDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoDTO> productos) { this.productos = productos; }
}
