package Modelo.dto;

/** Producto y cantidad solicitados para una venta directa de contado. */
public class ItemVentaDirectaDTO {
    private int idProducto;
    private int cantidad;

    public ItemVentaDirectaDTO() {
    }

    public ItemVentaDirectaDTO(int idProducto, int cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
