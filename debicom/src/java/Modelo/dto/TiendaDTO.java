package Modelo.dto;

import java.math.BigDecimal;

public class TiendaDTO {
    private int idTienda;
    private String nombreTienda;
    private String nit;
    private String direccion;
    private String telefono;
    private int idVendedor;
    private String vendedor;
    private BigDecimal ventasTotales = BigDecimal.ZERO;
    private long creditosOtorgados;
    private long clientesActivos;
    private long totalProductos;

    public int getIdTienda() { return idTienda; }
    public void setIdTienda(int idTienda) { this.idTienda = idTienda; }
    public String getNombreTienda() { return nombreTienda; }
    public void setNombreTienda(String nombreTienda) { this.nombreTienda = nombreTienda; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public int getIdVendedor() { return idVendedor; }
    public void setIdVendedor(int idVendedor) { this.idVendedor = idVendedor; }
    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }
    public BigDecimal getVentasTotales() { return ventasTotales; }
    public void setVentasTotales(BigDecimal ventasTotales) { this.ventasTotales = ventasTotales; }
    public long getCreditosOtorgados() { return creditosOtorgados; }
    public void setCreditosOtorgados(long creditosOtorgados) { this.creditosOtorgados = creditosOtorgados; }
    public long getClientesActivos() { return clientesActivos; }
    public void setClientesActivos(long clientesActivos) { this.clientesActivos = clientesActivos; }
    public long getTotalProductos() { return totalProductos; }
    public void setTotalProductos(long totalProductos) { this.totalProductos = totalProductos; }
}
