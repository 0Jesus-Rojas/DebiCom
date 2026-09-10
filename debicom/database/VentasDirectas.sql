-- =========================================================
-- DebiCom - Migración para Ventas Directas de Contado
-- Compatible con el esquema actual de facturas/pagos.
-- =========================================================
USE debicom;

-- Una venta directa no nace de una solicitud de crédito.
-- Se conserva la FK existente y se permite NULL para distinguir
-- facturas de crédito (id_solicitud != NULL) de ventas directas (NULL).
ALTER TABLE facturas
    MODIFY COLUMN id_solicitud INT(11) NULL;

-- El módulo utiliza un método de pago existente del catálogo tipo_pago
-- y un estado de factura PAGADA/PAGADO. No se agrega una fila especial
-- de tipo PRESENCIAL.

-- Verificación opcional:
-- SELECT id_factura, numero_factura, id_solicitud, id_cliente, id_tienda, total
-- FROM facturas
-- WHERE id_solicitud IS NULL
-- ORDER BY id_factura DESC;
