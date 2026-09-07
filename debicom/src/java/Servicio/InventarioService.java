package Servicio;

import Controlador.EstadoProductoDAO;
import Controlador.ProductoDAO;
import Controlador.TiendaDAO;
import Controlador.UnidadDAO;
import Controlador.VendedorDAO;
import Modelo.Productos;
import Modelo.dto.ProductoDTO;
import Modelo.dto.TiendaDTO;
import Modelo.EstadosProducto;
import Modelo.Unidades;
import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso del inventario del vendedor.
 */
public class InventarioService {
    private final VendedorDAO vendedorDAO;
    private final TiendaDAO tiendaDAO;
    private final ProductoDAO productoDAO;
    private final UnidadDAO unidadDAO;
    private final EstadoProductoDAO estadoProductoDAO;

    public InventarioService() {
        this(new VendedorDAO(), new TiendaDAO(), new ProductoDAO(), new UnidadDAO(), new EstadoProductoDAO());
    }

    public InventarioService(VendedorDAO vendedorDAO, TiendaDAO tiendaDAO,
                             ProductoDAO productoDAO, UnidadDAO unidadDAO,
                             EstadoProductoDAO estadoProductoDAO) {
        this.vendedorDAO = vendedorDAO;
        this.tiendaDAO = tiendaDAO;
        this.productoDAO = productoDAO;
        this.unidadDAO = unidadDAO;
        this.estadoProductoDAO = estadoProductoDAO;
    }

    public List<TiendaDTO> listarTiendas(int idVendedor) {
        return vendedorDAO.listarTiendasDelVendedor(idVendedor);
    }

    public VendedorDAO.InventarioData obtenerInventario(int idTienda, int idVendedor, String search) {
        asegurarPropiedadTienda(idTienda, idVendedor);
        return vendedorDAO.obtenerInventario(idTienda, idVendedor, search);
    }

    public List<Unidades> listarUnidades() {
        return unidadDAO.listarUnidades();
    }

    public List<EstadosProducto> listarEstadosProducto() {
        return estadoProductoDAO.listarEstadosProducto();
    }

    public int crearProducto(int idVendedor, int idTienda, String nombre, String descripcion,
                             BigDecimal precio, int stock, int idUnidad, int idEstadoProducto) {
        asegurarPropiedadTienda(idTienda, idVendedor);

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (unidadDAO.consultarUnidad(idUnidad) == null) {
            throw new IllegalArgumentException("La unidad seleccionada no existe.");
        }
        if (estadoProductoDAO.consultarEstadoProducto(idEstadoProducto) == null) {
            throw new IllegalArgumentException("El estado seleccionado no existe.");
        }

        Productos producto = new Productos();
        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion == null || descripcion.isBlank() ? null : descripcion.trim());
        producto.setPrecioUnitario(precio.floatValue());
        producto.setStock(stock);
        producto.setIdTienda(idTienda);
        producto.setIdUnidad(idUnidad);
        producto.setIdEstadoProducto(idEstadoProducto);

        if (!productoDAO.insertarProducto(producto)) {
            throw new IllegalStateException("No fue posible registrar el producto.");
        }

        ProductoDTO creado = null;
        // ProductoDAO no expone aún el ID generado; el formulario solo necesita confirmar éxito.
        return 1;
    }

    public boolean actualizarProducto(int idVendedor, int idProducto, int idTienda,
                                      String nombre, String descripcion, BigDecimal precio,
                                      int stock, int idUnidad, int idEstadoProducto) {
        asegurarPropiedadTienda(idTienda, idVendedor);

        if (idProducto <= 0) {
            throw new IllegalArgumentException("El producto seleccionado no es válido.");
        }
        Productos producto = productoDAO.consultarProducto(idProducto);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe.");
        }
        if (producto.getIdTienda() != idTienda) {
            throw new SecurityException("El producto no pertenece a la tienda seleccionada.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (unidadDAO.consultarUnidad(idUnidad) == null) {
            throw new IllegalArgumentException("La unidad seleccionada no existe.");
        }
        if (estadoProductoDAO.consultarEstadoProducto(idEstadoProducto) == null) {
            throw new IllegalArgumentException("El estado seleccionado no existe.");
        }

        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion == null || descripcion.isBlank() ? null : descripcion.trim());
        producto.setPrecioUnitario(precio.floatValue());
        producto.setStock(stock);
        producto.setIdTienda(idTienda);
        producto.setIdUnidad(idUnidad);
        producto.setIdEstadoProducto(idEstadoProducto);

        if (!productoDAO.actualizarProducto(producto)) {
            throw new IllegalStateException("No fue posible actualizar el producto.");
        }
        return true;
    }

    private void asegurarPropiedadTienda(int idTienda, int idVendedor) {
        if (idTienda <= 0 || idVendedor <= 0 || !tiendaDAO.perteneceAVendedor(idTienda, idVendedor)) {
            throw new SecurityException("La tienda no pertenece al vendedor autenticado.");
        }
    }
}
