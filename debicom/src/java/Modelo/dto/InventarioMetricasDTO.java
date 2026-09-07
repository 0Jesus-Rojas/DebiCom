package Modelo.dto;

public class InventarioMetricasDTO {
    private long productosActivos;
    private long productosDisponibles;
    private long productosBajoStock;
    private long productosAgotados;

    public long getProductosActivos() { return productosActivos; }
    public void setProductosActivos(long productosActivos) { this.productosActivos = productosActivos; }
    public long getProductosDisponibles() { return productosDisponibles; }
    public void setProductosDisponibles(long productosDisponibles) { this.productosDisponibles = productosDisponibles; }
    public long getProductosBajoStock() { return productosBajoStock; }
    public void setProductosBajoStock(long productosBajoStock) { this.productosBajoStock = productosBajoStock; }
    public long getProductosAgotados() { return productosAgotados; }
    public void setProductosAgotados(long productosAgotados) { this.productosAgotados = productosAgotados; }
}
