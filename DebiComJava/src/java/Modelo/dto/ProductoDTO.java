package Modelo.dto;

import java.math.BigDecimal;

public class ProductoDTO {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precioUnitario;
    private int stock;
    private int idTienda;
    private String unidad;
    private String estado;

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getIdTienda() { return idTienda; }
    public void setIdTienda(int idTienda) { this.idTienda = idTienda; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
