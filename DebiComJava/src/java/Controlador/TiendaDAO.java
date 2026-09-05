package Controlador;

import Modelo.Tiendas;
import Modelo.dto.TiendaDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para las operaciones de tiendas.
 */
public class TiendaDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Consulta una tienda por su identificador.
     */
    public Tiendas consultarTienda(int idTienda) {
        String sql = "SELECT id_tienda, nombre_tienda, nit, direccion, telefono, "
                + "id_vendedor, fecha_registro "
                + "FROM tiendas WHERE id_tienda = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTienda);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTienda(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar la tienda.", e);
        }

        return null;
    }

    /**
     * Retorna las tiendas disponibles para que un comprador solicite crédito.
     * El esquema no tiene un campo de estado para tiendas, por lo que se
     * consideran disponibles todas las tiendas registradas.
     */
    public List<TiendaDTO> listarTiendasDisponibles() {
        String sql = "SELECT id_tienda, nombre_tienda, nit, direccion, telefono "
                + "FROM tiendas ORDER BY nombre_tienda ASC";
        List<TiendaDTO> tiendas = new ArrayList<>();

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TiendaDTO dto = new TiendaDTO();
                dto.setIdTienda(rs.getInt("id_tienda"));
                dto.setNombreTienda(rs.getString("nombre_tienda"));
                dto.setNit(rs.getString("nit"));
                dto.setDireccion(rs.getString("direccion"));
                dto.setTelefono(rs.getString("telefono"));
                tiendas.add(dto);
            }

            return tiendas;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible obtener las tiendas disponibles.", e);
        }
    }

    /**
     * Inserta una tienda y retorna su ID generado.
     */
    public int insertarTienda(Tiendas tienda) {
        String sql = "INSERT INTO tiendas "
                + "(nombre_tienda, nit, direccion, telefono, id_vendedor, fecha_registro) "
                + "VALUES (?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tienda.getNombreTienda());
            ps.setString(2, tienda.getNit());
            ps.setString(3, tienda.getDireccion());
            ps.setString(4, tienda.getTelefono());
            ps.setInt(5, tienda.getIdVendedor());
            if (tienda.getFechaRegistro() != null) {
                ps.setTimestamp(6, new java.sql.Timestamp(tienda.getFechaRegistro().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible registrar la tienda.", e);
        }

        return 0;
    }

    /**
     * Actualiza únicamente los datos editables de la tienda.
     * El vendedor y la fecha de registro no se modifican desde este método.
     */
    public boolean actualizarDatosTienda(Tiendas tienda) {
        String sql = "UPDATE tiendas SET nombre_tienda = ?, nit = ?, direccion = ?, telefono = ? "
                + "WHERE id_tienda = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tienda.getNombreTienda());
            ps.setString(2, tienda.getNit());
            ps.setString(3, tienda.getDireccion());
            ps.setString(4, tienda.getTelefono());
            ps.setInt(5, tienda.getIdTienda());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible actualizar la tienda.", e);
        }
    }

    /**
     * Verifica que una tienda pertenezca al vendedor autenticado.
     */
    public boolean perteneceAVendedor(int idTienda, int idVendedor) {
        String sql = "SELECT 1 FROM tiendas WHERE id_tienda = ? AND id_vendedor = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTienda);
            ps.setInt(2, idVendedor);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible validar el propietario de la tienda.", e);
        }
    }

    private Tiendas mapTienda(ResultSet rs) throws SQLException {
        Tiendas tienda = new Tiendas();
        tienda.setIdTienda(rs.getInt("id_tienda"));
        tienda.setNombreTienda(rs.getString("nombre_tienda"));
        tienda.setNit(rs.getString("nit"));
        tienda.setDireccion(rs.getString("direccion"));
        tienda.setTelefono(rs.getString("telefono"));
        tienda.setIdVendedor(rs.getInt("id_vendedor"));
        tienda.setFechaRegistro(rs.getDate("fecha_registro"));
        return tienda;
    }
}
