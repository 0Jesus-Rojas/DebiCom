package Servicio;

import Controlador.UsuarioDAO;

/**
 * Casos de uso relacionados con la conversión de comprador a vendedor.
 */
public class RegistroVendedorService {
    private final UsuarioDAO usuarioDAO;

    public RegistroVendedorService() {
        this(new UsuarioDAO());
    }

    public RegistroVendedorService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public boolean puedeRegistrarseComoVendedor(int idUsuario) {
        return usuarioDAO.puedeRegistrarseComoVendedor(idUsuario);
    }

    /**
     * @return id del rol Vendedor asignado; 0 si no se actualizó.
     */
    public int registrarComoVendedor(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("Usuario inválido.");
        }
        if (!usuarioDAO.puedeRegistrarseComoVendedor(idUsuario)) {
            return 0;
        }
        return usuarioDAO.registrarComoVendedor(idUsuario);
    }
}
