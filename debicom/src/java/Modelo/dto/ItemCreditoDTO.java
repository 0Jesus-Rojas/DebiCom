package Modelo.dto;

/** Producto y cantidad solicitados por el vendedor al crear un crédito directo. */
public class ItemCreditoDTO {
    private int idProducto;
    private int cantidad;

    public ItemCreditoDTO() { }

    public ItemCreditoDTO(int idProducto, int cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
