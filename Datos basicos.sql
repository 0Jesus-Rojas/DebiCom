USE `debicom`;

SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

-- =========================================================
-- DATOS PREDEFINIDOS - DEBICOM
-- Solo se poblan:
-- estados_factura, estados_producto, estados_solicitud,
-- tipo_identificaciones, tipo_pago, tipos_movimiento, unidades,
-- 4 vendedores, 4 tiendas y 20 productos por tienda.
-- =========================================================

-- ---------------------------------------------------------
-- 1. CATÁLOGOS
-- ---------------------------------------------------------

INSERT INTO `estados_factura` (`nombre_estado`)
SELECT 'Pendiente' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_factura` WHERE `nombre_estado` = 'Pendiente'
);
INSERT INTO `estados_factura` (`nombre_estado`)
SELECT 'Pagada' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_factura` WHERE `nombre_estado` = 'Pagada'
);
INSERT INTO `estados_factura` (`nombre_estado`)
SELECT 'Vencida' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_factura` WHERE `nombre_estado` = 'Vencida'
);
INSERT INTO `estados_factura` (`nombre_estado`)
SELECT 'Anulada' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_factura` WHERE `nombre_estado` = 'Anulada'
);

INSERT INTO `estados_producto` (`nombre_estado`)
SELECT 'Activo' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_producto` WHERE `nombre_estado` = 'Activo'
);
INSERT INTO `estados_producto` (`nombre_estado`)
SELECT 'Inactivo' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_producto` WHERE `nombre_estado` = 'Inactivo'
);
INSERT INTO `estados_producto` (`nombre_estado`)
SELECT 'Agotado' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_producto` WHERE `nombre_estado` = 'Agotado'
);
INSERT INTO `estados_producto` (`nombre_estado`)
SELECT 'Descontinuado' WHERE NOT EXISTS (
    SELECT 1 FROM `estados_producto` WHERE `nombre_estado` = 'Descontinuado'
);

INSERT INTO estados_solicitud (id_estado_solicitud,nombre_estado) VALUES
(1,'PENDIENTE'),(2,'APROBADO'),(3,'RECHAZADO'),(4,'PAGADO'),(5,'VENCIDO');

INSERT INTO `tipo_identificaciones` (`nombre_tipo`)
SELECT 'Cédula de Ciudadanía' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_identificaciones`
    WHERE `nombre_tipo` = 'Cédula de Ciudadanía'
);
INSERT INTO `tipo_identificaciones` (`nombre_tipo`)
SELECT 'Cédula de Extranjería' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_identificaciones`
    WHERE `nombre_tipo` = 'Cédula de Extranjería'
);
INSERT INTO `tipo_identificaciones` (`nombre_tipo`)
SELECT 'Pasaporte' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_identificaciones`
    WHERE `nombre_tipo` = 'Pasaporte'
);

INSERT INTO `tipo_pago` (`nombre_pago`)
SELECT 'Efectivo' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_pago` WHERE `nombre_pago` = 'Efectivo'
);
INSERT INTO `tipo_pago` (`nombre_pago`)
SELECT 'Tarjeta débito' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_pago` WHERE `nombre_pago` = 'Tarjeta débito'
);
INSERT INTO `tipo_pago` (`nombre_pago`)
SELECT 'Tarjeta crédito' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_pago` WHERE `nombre_pago` = 'Tarjeta crédito'
);
INSERT INTO `tipo_pago` (`nombre_pago`)
SELECT 'Transferencia bancaria' WHERE NOT EXISTS (
    SELECT 1 FROM `tipo_pago` WHERE `nombre_pago` = 'Transferencia bancaria'
);

INSERT INTO `tipos_movimiento` (`nombre_tipo`)
SELECT 'Entrada' WHERE NOT EXISTS (
    SELECT 1 FROM `tipos_movimiento` WHERE `nombre_tipo` = 'Entrada'
);
INSERT INTO `tipos_movimiento` (`nombre_tipo`)
SELECT 'Salida' WHERE NOT EXISTS (
    SELECT 1 FROM `tipos_movimiento` WHERE `nombre_tipo` = 'Salida'
);
INSERT INTO `tipos_movimiento` (`nombre_tipo`)
SELECT 'Ajuste positivo' WHERE NOT EXISTS (
    SELECT 1 FROM `tipos_movimiento` WHERE `nombre_tipo` = 'Ajuste positivo'
);
INSERT INTO `tipos_movimiento` (`nombre_tipo`)
SELECT 'Ajuste negativo' WHERE NOT EXISTS (
    SELECT 1 FROM `tipos_movimiento` WHERE `nombre_tipo` = 'Ajuste negativo'
);

INSERT INTO `unidades` (`nombre_unidad`)
SELECT 'Unidad' WHERE NOT EXISTS (
    SELECT 1 FROM `unidades` WHERE `nombre_unidad` = 'Unidad'
);
INSERT INTO `unidades` (`nombre_unidad`)
SELECT 'Kilogramo' WHERE NOT EXISTS (
    SELECT 1 FROM `unidades` WHERE `nombre_unidad` = 'Kilogramo'
);
INSERT INTO `unidades` (`nombre_unidad`)
SELECT 'Gramo' WHERE NOT EXISTS (
    SELECT 1 FROM `unidades` WHERE `nombre_unidad` = 'Gramo'
);
INSERT INTO `unidades` (`nombre_unidad`)
SELECT 'Litro' WHERE NOT EXISTS (
    SELECT 1 FROM `unidades` WHERE `nombre_unidad` = 'Litro'
);
INSERT INTO `unidades` (`nombre_unidad`)
SELECT 'Mililitro' WHERE NOT EXISTS (
    SELECT 1 FROM `unidades` WHERE `nombre_unidad` = 'Mililitro'
);
INSERT INTO `unidades` (`nombre_unidad`)
SELECT 'Paquete' WHERE NOT EXISTS (
    SELECT 1 FROM `unidades` WHERE `nombre_unidad` = 'Paquete'
);

-- ---------------------------------------------------------
-- 2. ROLES
-- Se crean únicamente los roles Administrador y Vendedor.
-- No se modifica ni elimina ningún otro rol existente.
-- ---------------------------------------------------------

INSERT INTO `roles` (`nombre_rol`)
SELECT 'administrador'
WHERE NOT EXISTS (
    SELECT 1 FROM `roles` WHERE LOWER(`nombre_rol`) = 'administrador'
);

INSERT INTO `roles` (`nombre_rol`)
SELECT 'vendedor'
WHERE NOT EXISTS (
    SELECT 1 FROM `roles` WHERE LOWER(`nombre_rol`) = 'vendedor'
);

-- ---------------------------------------------------------
-- 3. 4 VENDEDORES
--
-- Se busca el rol por nombre para no depender de un ID fijo.
-- Los cuatro usuarios reciben el rol "vendedor".
-- Las contraseñas son de prueba y están en texto plano
-- porque este script solo crea datos iniciales.
-- ---------------------------------------------------------

INSERT INTO `usuarios`
(`nombre`, `apellido`, `identificacion`, `fecha_nacimiento`, `correo`,
 `telefono`, `direccion`, `password`, `fecha_vencimiento_clave`,
 `autoriza_datos`, `id_tipo_identificacion`, `id_rol`)
SELECT 'Carlos', 'Rodríguez', '1000000001', '1990-03-15',
       'carlos.vendedor@debicom.test', '3001000001',
       'Carrera 10 #20-30, Bogotá', 'Vendedor123',
       NULL, 2,
       (SELECT id_tipo_identificacion FROM tipo_identificaciones
        WHERE nombre_tipo = 'Cédula de Ciudadanía' LIMIT 1),
       (SELECT id_rol FROM roles WHERE nombre_rol = 'vendedor' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios
    WHERE correo = 'carlos.vendedor@debicom.test'
);

INSERT INTO `usuarios`
(`nombre`, `apellido`, `identificacion`, `fecha_nacimiento`, `correo`,
 `telefono`, `direccion`, `password`, `fecha_vencimiento_clave`,
 `autoriza_datos`, `id_tipo_identificacion`, `id_rol`)
SELECT 'María', 'Gómez', '1000000002', '1992-07-22',
       'maria.vendedor@debicom.test', '3001000002',
       'Calle 45 #12-18, Medellín', 'Vendedor123',
       NULL, 2,
       (SELECT id_tipo_identificacion FROM tipo_identificaciones
        WHERE nombre_tipo = 'Cédula de Ciudadanía' LIMIT 1),
       (SELECT id_rol FROM roles WHERE nombre_rol = 'vendedor' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios
    WHERE correo = 'maria.vendedor@debicom.test'
);

INSERT INTO `usuarios`
(`nombre`, `apellido`, `identificacion`, `fecha_nacimiento`, `correo`,
 `telefono`, `direccion`, `password`, `fecha_vencimiento_clave`,
 `autoriza_datos`, `id_tipo_identificacion`, `id_rol`)
SELECT 'Andrés', 'Martínez', '1000000003', '1988-11-08',
       'andres.vendedor@debicom.test', '3001000003',
       'Carrera 33 #8-44, Cali', 'Vendedor123',
       NULL, 2,
       (SELECT id_tipo_identificacion FROM tipo_identificaciones
        WHERE nombre_tipo = 'Cédula de Ciudadanía' LIMIT 1),
       (SELECT id_rol FROM roles WHERE nombre_rol = 'vendedor' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios
    WHERE correo = 'andres.vendedor@debicom.test'
);

INSERT INTO `usuarios`
(`nombre`, `apellido`, `identificacion`, `fecha_nacimiento`, `correo`,
 `telefono`, `direccion`, `password`, `fecha_vencimiento_clave`,
 `autoriza_datos`, `id_tipo_identificacion`, `id_rol`)
SELECT 'Laura', 'Hernández', '1000000004', '1991-05-30',
       'laura.vendedor@debicom.test', '3001000004',
       'Calle 72 #15-20, Barranquilla', 'Vendedor123',
       NULL, 2,
       (SELECT id_tipo_identificacion FROM tipo_identificaciones
        WHERE nombre_tipo = 'Cédula de Ciudadanía' LIMIT 1),
       (SELECT id_rol FROM roles WHERE nombre_rol = 'vendedor' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios
    WHERE correo = 'laura.vendedor@debicom.test'
);

-- ---------------------------------------------------------
-- 4. AGREGAR LOS 4 VENDEDORES TAMBIÉN COMO CLIENTES
-- Cada usuario solo puede aparecer una vez en clientes.
-- El crédito inicial queda en 0.00.
-- ---------------------------------------------------------

INSERT INTO `clientes` (`credito_actual`, `id_usuario`)
SELECT 0.00, u.id_usuario
FROM usuarios u
WHERE u.correo IN (
    'carlos.vendedor@debicom.test',
    'maria.vendedor@debicom.test',
    'andres.vendedor@debicom.test',
    'laura.vendedor@debicom.test'
)
AND NOT EXISTS (
    SELECT 1 FROM clientes c WHERE c.id_usuario = u.id_usuario
);

-- ---------------------------------------------------------
-- 5. 1 TIENDA PARA CADA VENDEDOR
-- ---------------------------------------------------------

INSERT INTO `tiendas`
(`nombre_tienda`, `nit`, `direccion`, `telefono`, `id_vendedor`)
SELECT 'Tienda Carlos', '900100000-1',
       'Carrera 10 #20-30, Bogotá', '6013000001', u.id_usuario
FROM usuarios u
WHERE u.correo = 'carlos.vendedor@debicom.test'
  AND NOT EXISTS (
      SELECT 1 FROM tiendas t WHERE t.nit = '900100000-1'
  );

INSERT INTO `tiendas`
(`nombre_tienda`, `nit`, `direccion`, `telefono`, `id_vendedor`)
SELECT 'Tienda María', '900100000-2',
       'Calle 45 #12-18, Medellín', '6043000002', u.id_usuario
FROM usuarios u
WHERE u.correo = 'maria.vendedor@debicom.test'
  AND NOT EXISTS (
      SELECT 1 FROM tiendas t WHERE t.nit = '900100000-2'
  );

INSERT INTO `tiendas`
(`nombre_tienda`, `nit`, `direccion`, `telefono`, `id_vendedor`)
SELECT 'Tienda Andrés', '900100000-3',
       'Carrera 33 #8-44, Cali', '6023000003', u.id_usuario
FROM usuarios u
WHERE u.correo = 'andres.vendedor@debicom.test'
  AND NOT EXISTS (
      SELECT 1 FROM tiendas t WHERE t.nit = '900100000-3'
  );

INSERT INTO `tiendas`
(`nombre_tienda`, `nit`, `direccion`, `telefono`, `id_vendedor`)
SELECT 'Tienda Laura', '900100000-4',
       'Calle 72 #15-20, Barranquilla', '6053000004', u.id_usuario
FROM usuarios u
WHERE u.correo = 'laura.vendedor@debicom.test'
  AND NOT EXISTS (
      SELECT 1 FROM tiendas t WHERE t.nit = '900100000-4'
  );

-- ---------------------------------------------------------
-- 6. 20 PRODUCTOS POR TIENDA = 80 PRODUCTOS
-- ---------------------------------------------------------

INSERT INTO productos
(nombre, descripcion, precio_unitario, stock, id_tienda, id_unidad, id_estado_producto)
SELECT x.nombre, x.descripcion, x.precio, x.stock, t.id_tienda,
       (SELECT id_unidad FROM unidades WHERE nombre_unidad = x.unidad LIMIT 1),
       (SELECT id_estado_producto FROM estados_producto
        WHERE nombre_estado = 'Activo' LIMIT 1)
FROM (
    SELECT 'Arroz Premium 1 kg' nombre, 'Arroz blanco premium de 1 kg' descripcion, 5200.00 precio, 80 stock, 'Kilogramo' unidad
    UNION ALL SELECT 'Frijol Rojo 500 g', 'Frijol rojo seleccionado', 4800.00, 70, 'Gramo'
    UNION ALL SELECT 'Lenteja 500 g', 'Lenteja seleccionada', 4200.00, 65, 'Gramo'
    UNION ALL SELECT 'Aceite Vegetal 1 L', 'Aceite vegetal comestible', 8500.00, 55, 'Litro'
    UNION ALL SELECT 'Azúcar 1 kg', 'Azúcar blanca refinada', 4300.00, 90, 'Kilogramo'
    UNION ALL SELECT 'Sal 500 g', 'Sal de cocina yodada', 1800.00, 100, 'Gramo'
    UNION ALL SELECT 'Harina de Trigo 1 kg', 'Harina de trigo para cocina', 3900.00, 60, 'Kilogramo'
    UNION ALL SELECT 'Café Molido 250 g', 'Café molido tradicional', 7800.00, 45, 'Gramo'
    UNION ALL SELECT 'Chocolate en Polvo 200 g', 'Chocolate instantáneo en polvo', 6200.00, 50, 'Gramo'
    UNION ALL SELECT 'Leche Entera 1 L', 'Leche entera UHT', 4100.00, 70, 'Litro'
    UNION ALL SELECT 'Jugo de Naranja 1 L', 'Bebida de naranja', 5200.00, 45, 'Litro'
    UNION ALL SELECT 'Agua Mineral 600 ml', 'Agua mineral embotellada', 2200.00, 100, 'Mililitro'
    UNION ALL SELECT 'Galletas de Chocolate', 'Paquete de galletas con chocolate', 3500.00, 75, 'Paquete'
    UNION ALL SELECT 'Papas Fritas 150 g', 'Papas fritas tradicionales', 4500.00, 65, 'Gramo'
    UNION ALL SELECT 'Atún en Lata 170 g', 'Atún en aceite', 6900.00, 50, 'Gramo'
    UNION ALL SELECT 'Sardinas en Lata 425 g', 'Sardinas en salsa de tomate', 7200.00, 35, 'Gramo'
    UNION ALL SELECT 'Detergente en Polvo 1 kg', 'Detergente para ropa', 9800.00, 40, 'Kilogramo'
    UNION ALL SELECT 'Jabón de Baño', 'Jabón corporal', 2800.00, 90, 'Unidad'
    UNION ALL SELECT 'Papel Higiénico x4', 'Paquete de cuatro rollos', 6200.00, 55, 'Paquete'
    UNION ALL SELECT 'Crema Dental 100 ml', 'Crema dental familiar', 5400.00, 45, 'Mililitro'
) x
CROSS JOIN tiendas t
WHERE t.nit IN ('900100000-1','900100000-2','900100000-3','900100000-4')
  AND NOT EXISTS (
      SELECT 1
      FROM productos p
      WHERE p.id_tienda = t.id_tienda
        AND p.nombre = x.nombre
  );

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- VERIFICACIÓN
-- =========================================================

SELECT 'Estados factura' AS tabla, COUNT(*) AS cantidad FROM estados_factura
UNION ALL
SELECT 'Estados producto', COUNT(*) FROM estados_producto
UNION ALL
SELECT 'Estados solicitud', COUNT(*) FROM estados_solicitud
UNION ALL
SELECT 'Tipos identificación', COUNT(*) FROM tipo_identificaciones
UNION ALL
SELECT 'Tipos de pago', COUNT(*) FROM tipo_pago
UNION ALL
SELECT 'Tipos movimiento', COUNT(*) FROM tipos_movimiento
UNION ALL
SELECT 'Unidades', COUNT(*) FROM unidades;

SELECT
    t.id_tienda,
    t.nombre_tienda,
    u.nombre,
    u.apellido,
    COUNT(p.id_producto) AS productos
FROM tiendas t
INNER JOIN usuarios u ON u.id_usuario = t.id_vendedor
LEFT JOIN productos p ON p.id_tienda = t.id_tienda
WHERE t.nit IN ('900100000-1','900100000-2','900100000-3','900100000-4')
GROUP BY t.id_tienda, t.nombre_tienda, u.nombre, u.apellido
ORDER BY t.id_tienda;
