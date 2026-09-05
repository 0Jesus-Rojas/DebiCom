package Controlador;

import Modelo.dto.CreditoPendienteDTO;
import Modelo.dto.CreditosPendientesDTO;
import Modelo.dto.DashboardVendedorDTO;
import Modelo.dto.DetalleCreditoDTO;
import Modelo.dto.ProductoDTO;
import Modelo.dto.SolicitudCreditoDTO;
import Modelo.dto.TiendaDTO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Consultas y comandos agregados para el módulo de administración del vendedor. */
public class VendedorDAO {
    private static final int STOCK_BAJO_UMBRAL = 5;
    private final Conexion conexion = new Conexion();

    public List<TiendaDTO> listarTiendasDelVendedor(int idVendedor) {
        String sql = "SELECT id_tienda, nombre_tienda, nit, direccion, telefono, id_vendedor, fecha_registro "
                + "FROM tiendas WHERE id_vendedor = ? ORDER BY nombre_tienda";
        List<TiendaDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTienda(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar las tiendas del vendedor.", e);
        }
        return result;
    }

    public DashboardVendedorDTO obtenerDashboard(int idVendedor) {
        String base = " FROM solicitudes_credito sc "
                + "INNER JOIN tiendas t ON t.id_tienda = sc.id_tienda "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud = sc.id_estado_solicitud "
                + "WHERE t.id_vendedor = ? ";
        DashboardVendedorDTO dto = new DashboardVendedorDTO();
        String sqlClientes = "SELECT COUNT(DISTINCT sc.id_cliente)" + base;
        String sqlNuevas = "SELECT COUNT(*)" + base + "AND UPPER(es.nombre_estado) = 'PENDIENTE' AND sc.fecha_solicitud >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 7 DAY)";
        String sqlAprobadas = "SELECT COUNT(*)" + base + "AND UPPER(es.nombre_estado) = 'APROBADO'";
        String sqlPagos = "SELECT COALESCE(SUM(p.monto_pagado),0) FROM pagos p "
                + "INNER JOIN facturas f ON f.id_factura = p.id_factura "
                + "INNER JOIN tiendas t ON t.id_tienda = f.id_tienda WHERE t.id_vendedor = ?";
        String sqlDeuda = "SELECT COALESCE(SUM(sc.saldo_pendiente),0)" + base + "AND sc.saldo_pendiente > 0 "
                + "AND UPPER(es.nombre_estado) = 'APROBADO'";
        try (Connection conn = conexion.getConnection()) {
            dto.setTotalClientes(scalarLong(conn, sqlClientes, idVendedor));
            dto.setSolicitudesNuevas(scalarLong(conn, sqlNuevas, idVendedor));
            dto.setSolicitudesPendientes(dto.getSolicitudesNuevas());
            dto.setSolicitudesAprobadas(scalarLong(conn, sqlAprobadas, idVendedor));
            dto.setPagosRecibidos(scalarDecimal(conn, sqlPagos, idVendedor));
            dto.setDeudaPendiente(scalarDecimal(conn, sqlDeuda, idVendedor));
            return dto;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible calcular el dashboard.", e);
        }
    }

    public TiendaDTO obtenerMetricasTienda(int idTienda, int idVendedor) {
        String sql = "SELECT t.id_tienda, t.nombre_tienda, t.nit, t.direccion, t.telefono, t.id_vendedor, "
                + "t.fecha_registro, CONCAT(u.nombre,' ',u.apellido) AS vendedor, "
                + "COALESCE((SELECT SUM(f.total) FROM facturas f INNER JOIN estados_factura ef ON ef.id_estado_factura=f.id_estado_factura WHERE f.id_tienda=t.id_tienda AND UPPER(ef.nombre_estado) <> 'ANULADA'),0) AS ventas_totales, "
                + "(SELECT COUNT(*) FROM solicitudes_credito sc INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + " WHERE sc.id_tienda=t.id_tienda AND UPPER(es.nombre_estado)='APROBADO') AS creditos_otorgados, "
                + "(SELECT COUNT(DISTINCT sc.id_cliente) FROM solicitudes_credito sc INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + " WHERE sc.id_tienda=t.id_tienda AND UPPER(es.nombre_estado)='APROBADO' AND sc.saldo_pendiente > 0) AS clientes_activos, "
                + "(SELECT COUNT(*) FROM productos p WHERE p.id_tienda=t.id_tienda) AS total_productos "
                + "FROM tiendas t INNER JOIN usuarios u ON u.id_usuario=t.id_vendedor "
                + "WHERE t.id_tienda=? AND t.id_vendedor=?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTienda); ps.setInt(2, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                TiendaDTO dto = mapTienda(rs);
                dto.setVendedor(rs.getString("vendedor"));
                dto.setVentasTotales(rs.getBigDecimal("ventas_totales"));
                dto.setCreditosOtorgados(rs.getLong("creditos_otorgados"));
                dto.setClientesActivos(rs.getLong("clientes_activos"));
                dto.setTotalProductos(rs.getLong("total_productos"));
                return dto;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible calcular las métricas de la tienda.", e);
        }
    }

    public List<SolicitudCreditoDTO> listarSolicitudes(int idVendedor, String estado) {
        StringBuilder sql = new StringBuilder("SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, es.nombre_estado, "
                + "sc.fecha_solicitud, sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE t.id_vendedor=? ");
        if (estado != null && !estado.isBlank()) sql.append("AND UPPER(es.nombre_estado)=UPPER(?) ");
        sql.append("ORDER BY sc.fecha_solicitud DESC, sc.id_solicitud DESC");
        List<SolicitudCreditoDTO> result = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, idVendedor);
            if (estado != null && !estado.isBlank()) ps.setString(2, estado.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapSolicitud(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible listar las solicitudes.", e);
        }
        return result;
    }

    public DetalleCreditoDTO obtenerDetalleCredito(int idSolicitud, int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, sc.id_tienda, t.nombre_tienda, "
                + "CONCAT(u.nombre,' ',u.apellido) AS cliente, sc.monto_total, sc.saldo_pendiente, es.nombre_estado, "
                + "sc.fecha_solicitud, sc.fecha_aprobacion, sc.fecha_vencimiento, sc.observaciones "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE sc.id_solicitud=? AND t.id_vendedor=?";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud); ps.setInt(2, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                SolicitudCreditoDTO solicitud = mapSolicitud(rs);
                DetalleCreditoDTO detalle = new DetalleCreditoDTO();
                detalle.setSolicitud(solicitud);
                detalle.setProductos(listarProductosSolicitud(conn, idSolicitud));
                return detalle;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar el detalle del crédito.", e);
        }
    }

    private List<ProductoDTO> listarProductosSolicitud(Connection conn, int idSolicitud) throws SQLException {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_unitario, p.stock, p.id_tienda, "
                + "u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM detalle_solicitud_credito dsc INNER JOIN productos p ON p.id_producto=dsc.id_producto "
                + "LEFT JOIN unidades u ON u.id_unidad=p.id_unidad LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "WHERE dsc.id_solicitud=? ORDER BY p.nombre";
        List<ProductoDTO> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(rs.getInt("id_producto"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setDescripcion(rs.getString("descripcion"));
                    dto.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    dto.setStock(rs.getInt("stock"));
                    dto.setIdTienda(rs.getInt("id_tienda"));
                    dto.setUnidad(rs.getString("unidad"));
                    dto.setEstado(rs.getString("nombre_estado"));
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public boolean actualizarEstadoCredito(int idSolicitud, int idVendedor, String nuevoEstado) {
        if (!"APROBADO".equalsIgnoreCase(nuevoEstado) && !"RECHAZADO".equalsIgnoreCase(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no permitido. Use APROBADO o RECHAZADO.");
        }
        String sqlEstado = "SELECT id_estado_solicitud FROM estados_solicitud WHERE UPPER(nombre_estado)=UPPER(?) LIMIT 1";
        String sqlUpdate = "UPDATE solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "SET sc.id_estado_solicitud=?, sc.fecha_aprobacion=CURRENT_TIMESTAMP "
                + "WHERE sc.id_solicitud=? AND t.id_vendedor=? AND EXISTS (SELECT 1 FROM estados_solicitud es0 "
                + "WHERE es0.id_estado_solicitud=sc.id_estado_solicitud AND UPPER(es0.nombre_estado)='PENDIENTE')";
        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idEstado = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlEstado)) {
                    ps.setString(1, nuevoEstado.trim());
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) idEstado = rs.getInt(1); }
                }
                if (idEstado < 0) throw new IllegalStateException("No existe el estado " + nuevoEstado + ".");
                int updated;
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, idEstado); ps.setInt(2, idSolicitud); ps.setInt(3, idVendedor);
                    updated = ps.executeUpdate();
                }
                if (updated != 1) { conn.rollback(); return false; }
                conn.commit(); return true;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof IllegalArgumentException iae) throw iae;
                throw new IllegalStateException("No fue posible actualizar el estado del crédito.", e);
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible actualizar el estado del crédito.", e);
        }
    }

    public CreditosPendientesDTO obtenerCreditosPendientes(int idVendedor) {
        String sql = "SELECT sc.id_solicitud, sc.id_cliente, CONCAT(u.nombre,' ',u.apellido) AS cliente, "
                + "sc.id_tienda, t.nombre_tienda, sc.monto_total, sc.saldo_pendiente, es.nombre_estado, "
                + "sc.fecha_solicitud, sc.fecha_vencimiento "
                + "FROM solicitudes_credito sc INNER JOIN tiendas t ON t.id_tienda=sc.id_tienda "
                + "INNER JOIN clientes c ON c.id_cliente=sc.id_cliente INNER JOIN usuarios u ON u.id_usuario=c.id_usuario "
                + "INNER JOIN estados_solicitud es ON es.id_estado_solicitud=sc.id_estado_solicitud "
                + "WHERE t.id_vendedor=? AND UPPER(es.nombre_estado)='APROBADO' AND sc.saldo_pendiente>0 "
                + "ORDER BY sc.fecha_vencimiento ASC, sc.id_solicitud ASC";
        CreditosPendientesDTO result = new CreditosPendientesDTO();
        List<CreditoPendienteDTO> list = new ArrayList<>();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                BigDecimal total = BigDecimal.ZERO;
                java.util.HashSet<Integer> clientes = new java.util.HashSet<>();
                while (rs.next()) {
                    CreditoPendienteDTO dto = new CreditoPendienteDTO();
                    dto.setIdSolicitud(rs.getInt("id_solicitud"));
                    dto.setIdCliente(rs.getInt("id_cliente"));
                    dto.setCliente(rs.getString("cliente"));
                    dto.setIdTienda(rs.getInt("id_tienda"));
                    dto.setTienda(rs.getString("nombre_tienda"));
                    dto.setMontoOriginal(rs.getBigDecimal("monto_total"));
                    dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
                    dto.setEstado(rs.getString("nombre_estado"));
                    Timestamp ts = rs.getTimestamp("fecha_solicitud");
                    if (ts != null) dto.setFechaSolicitud(ts.toLocalDateTime());
                    Date date = rs.getDate("fecha_vencimiento");
                    if (date != null) dto.setFechaVencimiento(date.toLocalDate());
                    if (dto.getFechaVencimiento() != null && dto.getFechaVencimiento().isBefore(LocalDate.now())) {
                        dto.setDiasMora(ChronoUnit.DAYS.between(dto.getFechaVencimiento(), LocalDate.now()));
                    } else dto.setDiasMora(0);
                    list.add(dto);
                    total = total.add(dto.getSaldoPendiente());
                    clientes.add(dto.getIdCliente());
                }
                result.setCreditos(list);
                result.setCreditosActivos(list.size());
                result.setDineroTotalPorCobrar(total);
                result.setClientesConDeuda(clientes.size());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar los créditos pendientes.", e);
        }
        return result;
    }

    public InventarioData obtenerInventario(int idTienda, int idVendedor, String search) {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio_unitario, p.stock, p.id_tienda, "
                + "u.nombre_unidad AS unidad, ep.nombre_estado "
                + "FROM productos p LEFT JOIN unidades u ON u.id_unidad=p.id_unidad "
                + "LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                + "INNER JOIN tiendas t ON t.id_tienda=p.id_tienda WHERE p.id_tienda=? AND t.id_vendedor=? ";
        boolean filtering = search != null && !search.isBlank();
        if (filtering) sql += "AND p.nombre LIKE ? ";
        sql += "ORDER BY p.nombre ASC";
        List<ProductoDTO> productos = new ArrayList<>();
        InventarioData data = new InventarioData();
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTienda); ps.setInt(2, idVendedor); if (filtering) ps.setString(3, "%" + search.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(rs.getInt("id_producto"));
                    dto.setNombre(rs.getString("nombre")); dto.setDescripcion(rs.getString("descripcion"));
                    dto.setPrecioUnitario(rs.getBigDecimal("precio_unitario")); dto.setStock(rs.getInt("stock"));
                    dto.setIdTienda(rs.getInt("id_tienda")); dto.setUnidad(rs.getString("unidad")); dto.setEstado(rs.getString("nombre_estado"));
                    productos.add(dto);
                }
            }
            String metricSql = "SELECT "
                    + "SUM(CASE WHEN UPPER(COALESCE(ep.nombre_estado,'')) = 'ACTIVO' THEN 1 ELSE 0 END) AS activos, "
                    + "SUM(CASE WHEN UPPER(COALESCE(ep.nombre_estado,'')) = 'ACTIVO' AND p.stock > 0 THEN 1 ELSE 0 END) AS disponibles, "
                    + "SUM(CASE WHEN p.stock > 0 AND p.stock <= ? THEN 1 ELSE 0 END) AS bajo_stock, "
                    + "SUM(CASE WHEN p.stock <= 0 OR UPPER(COALESCE(ep.nombre_estado,'')) = 'AGOTADO' THEN 1 ELSE 0 END) AS agotados "
                    + "FROM productos p LEFT JOIN estados_producto ep ON ep.id_estado_producto=p.id_estado_producto "
                    + "WHERE p.id_tienda=?";
            try (PreparedStatement ps2 = conn.prepareStatement(metricSql)) {
                ps2.setInt(1, STOCK_BAJO_UMBRAL); ps2.setInt(2, idTienda);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        data.activos = rs.getLong("activos"); data.disponibles = rs.getLong("disponibles");
                        data.bajoStock = rs.getLong("bajo_stock"); data.agotados = rs.getLong("agotados");
                    }
                }
            }
            data.productos = productos;
            return data;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible consultar el inventario.", e);
        }
    }

    private long scalarLong(Connection conn, String sql, int param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1,param); try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getLong(1):0;} }
    }
    private BigDecimal scalarDecimal(Connection conn, String sql, int param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1,param); try(ResultSet rs=ps.executeQuery()){return rs.next() && rs.getBigDecimal(1)!=null?rs.getBigDecimal(1):BigDecimal.ZERO;} }
    }
    private TiendaDTO mapTienda(ResultSet rs) throws SQLException {
        TiendaDTO dto = new TiendaDTO(); dto.setIdTienda(rs.getInt("id_tienda")); dto.setNombreTienda(rs.getString("nombre_tienda"));
        dto.setNit(rs.getString("nit")); dto.setDireccion(rs.getString("direccion")); dto.setTelefono(rs.getString("telefono")); dto.setIdVendedor(rs.getInt("id_vendedor")); return dto;
    }
    private SolicitudCreditoDTO mapSolicitud(ResultSet rs) throws SQLException {
        SolicitudCreditoDTO dto = new SolicitudCreditoDTO(); dto.setIdSolicitud(rs.getInt("id_solicitud")); dto.setIdCliente(rs.getInt("id_cliente"));
        dto.setIdTienda(rs.getInt("id_tienda")); dto.setTienda(rs.getString("nombre_tienda")); dto.setCliente(rs.getString("cliente"));
        dto.setMontoTotal(rs.getBigDecimal("monto_total")); dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente")); dto.setEstado(rs.getString("nombre_estado"));
        Timestamp s=rs.getTimestamp("fecha_solicitud"); if(s!=null)dto.setFechaSolicitud(s.toLocalDateTime()); Timestamp a=rs.getTimestamp("fecha_aprobacion"); if(a!=null)dto.setFechaAprobacion(a.toLocalDateTime());
        Date v=rs.getDate("fecha_vencimiento"); if(v!=null)dto.setFechaVencimiento(v.toLocalDate()); dto.setObservaciones(rs.getString("observaciones")); return dto;
    }

    public static class InventarioData {
        public long activos, disponibles, bajoStock, agotados;
        public List<ProductoDTO> productos = new ArrayList<>();
    }
}
