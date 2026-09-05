package Servicio;

import Controlador.TiendaDAO;
import Controlador.VendedorDAO;
import Modelo.Tiendas;
import Modelo.dto.TiendaDTO;
import java.util.List;

/**
 * Lógica de negocio para la administración de tiendas del vendedor.
 */
public class TiendaService {
    private final TiendaDAO tiendaDAO;
    private final VendedorDAO vendedorDAO;

    public TiendaService() {
        this(new TiendaDAO(), new VendedorDAO());
    }

    public TiendaService(TiendaDAO tiendaDAO, VendedorDAO vendedorDAO) {
        this.tiendaDAO = tiendaDAO;
        this.vendedorDAO = vendedorDAO;
    }

    public List<TiendaDTO> listarTiendas(int idVendedor) {
        validarVendedor(idVendedor);
        return vendedorDAO.listarTiendasDelVendedor(idVendedor);
    }

    public TiendaDTO obtenerTienda(int idTienda, int idVendedor) {
        validarVendedor(idVendedor);
        if (idTienda <= 0 || !tiendaDAO.perteneceAVendedor(idTienda, idVendedor)) {
            return null;
        }
        return vendedorDAO.obtenerMetricasTienda(idTienda, idVendedor);
    }

    public int crearTienda(int idVendedor, String nombre, String nit, String direccion, String telefono) {
        validarVendedor(idVendedor);
        validarDatos(nombre, nit, direccion, telefono);

        Tiendas tienda = new Tiendas();
        tienda.setNombreTienda(nombre.trim());
        tienda.setNit(nit.trim());
        tienda.setDireccion(direccion.trim());
        tienda.setTelefono(telefono.trim());
        tienda.setIdVendedor(idVendedor);

        return tiendaDAO.insertarTienda(tienda);
    }

    public boolean actualizarTienda(int idVendedor, int idTienda,
                                    String nombre, String nit, String direccion, String telefono) {
        validarVendedor(idVendedor);
        if (!tiendaDAO.perteneceAVendedor(idTienda, idVendedor)) {
            throw new SecurityException("La tienda no pertenece al vendedor autenticado.");
        }
        validarDatos(nombre, nit, direccion, telefono);

        Tiendas tienda = new Tiendas();
        tienda.setIdTienda(idTienda);
        tienda.setIdVendedor(idVendedor);
        tienda.setNombreTienda(nombre.trim());
        tienda.setNit(nit.trim());
        tienda.setDireccion(direccion.trim());
        tienda.setTelefono(telefono.trim());

        return tiendaDAO.actualizarDatosTienda(tienda);
    }

    private void validarVendedor(int idVendedor) {
        if (idVendedor <= 0) {
            throw new IllegalArgumentException("Usuario vendedor inválido.");
        }
    }

    private void validarDatos(String nombre, String nit, String direccion, String telefono) {
        if (blank(nombre) || blank(nit) || blank(direccion) || blank(telefono)) {
            throw new IllegalArgumentException("Todos los datos de la tienda son obligatorios.");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
