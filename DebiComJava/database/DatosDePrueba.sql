USE `debicom`;

-- =====================================================
-- DATOS DE PRUEBA PARA CRUD - SISTEMA DE CRÉDITOS E INVENTARIO
-- =====================================================
SET FOREIGN_KEY_CHECKS=0;

-- Catálogos
INSERT INTO tipo_identificaciones (id_tipo_identificacion,nombre_tipo) VALUES
(1,'Cédula de ciudadanía'),(2,'Cédula de extranjería'),(3,'NIT'),(4,'Pasaporte');

INSERT INTO roles (id_rol,nombre_rol) VALUES
(1,'Administrador'),(2,'Vendedor'),(3,'Supervisor');

INSERT INTO unidades (id_unidad,nombre_unidad) VALUES
(1,'Unidad'),(2,'Caja'),(3,'Kilogramo'),(4,'Litro'),(5,'Paquete');

INSERT INTO estados_producto (id_estado_producto,nombre_estado) VALUES
(1,'ACTIVO'),(2,'INACTIVO'),(3,'AGOTADO');

INSERT INTO tipos_movimiento (id_tipo_movimiento,nombre_tipo) VALUES
(1,'ENTRADA'),(2,'SALIDA'),(3,'AJUSTE');

INSERT INTO estados_solicitud (id_estado_solicitud,nombre_estado) VALUES
(1,'PENDIENTE'),(2,'APROBADO'),(3,'RECHAZADO'),(4,'PAGADO'),(5,'VENCIDO');

INSERT INTO estados_factura (id_estado_factura,nombre_estado) VALUES
(1,'PENDIENTE'),(2,'PAGADA'),(3,'ANULADA'),(4,'VENCIDA');

INSERT INTO tipo_pago (id_tipo_pago,nombre_pago) VALUES
(1,'Efectivo'),(2,'Transferencia bancaria'),(3,'Tarjeta débito'),(4,'Tarjeta crédito');

-- Usuarios: contraseñas de prueba (texto plano SOLO para entorno local)
INSERT INTO usuarios
(id_usuario,nombre,apellido,identificacion,fecha_nacimiento,correo,telefono,direccion,password,fecha_vencimiento_clave,autoriza_datos,id_tipo_identificacion,id_rol)
VALUES
(1,'Carlos','Rodríguez','1001001001','1988-03-15','carlos.admin@debicom.test','3001001001','Cra 10 # 20-30','Admin123*','2027-12-31',1,1,1),
(2,'Laura','Martínez','1001001002','1992-07-22','laura.vendedor@debicom.test','3001001002','Calle 45 # 12-18','Vendedor123*','2027-12-31',1,1,2),
(3,'Andrés','Gómez','1001001003','1985-11-08','andres.supervisor@debicom.test','3001001003','Carrera 7 # 80-15','Supervisor123*','2027-12-31',1,1,3),
(4,'María','López','1001001004','1995-01-19','maria.cliente@debicom.test','3001001004','Calle 72 # 15-20','Cliente123*',NULL,1,1,NULL),
(5,'Juan','Pérez','1001001005','1990-05-03','juan.cliente@debicom.test','3001001005','Cra 30 # 45-10','Cliente123*',NULL,1,1,NULL),
(6,'Sofía','Ramírez','1001001006','1998-09-27','sofia.cliente@debicom.test','3001001006','Calle 100 # 20-50','Cliente123*',NULL,1,1,NULL),
(7,'Diego','Torres','1001001007','1987-12-12','diego.cliente@debicom.test','3001001007','Carrera 50 # 25-60','Cliente123*',NULL,1,1,NULL),
(8,'Valentina','Castro','1001001008','1993-06-30','valentina.cliente@debicom.test','3001001008','Calle 26 # 40-22','Cliente123*',NULL,1,1,NULL);

-- Clientes
INSERT INTO clientes (id_cliente,credito_actual,id_usuario) VALUES
(1,1250000.00,4),(2,780000.00,5),(3,0.00,6),(4,2150000.00,7),(5,350000.00,8);

-- Tiendas
INSERT INTO tiendas (id_tienda,nombre_tienda,nit,direccion,telefono,id_vendedor) VALUES
(1,'Debicom Centro','900123456-1','Carrera 10 # 20-30','6015551001',2),
(2,'Debicom Norte','900123457-2','Calle 120 # 15-40','6015551002',2),
(3,'Debicom Sur','900123458-3','Carrera 50 # 30-25','6015551003',3);

-- Productos
INSERT INTO productos
(id_producto,nombre,descripcion,precio_unitario,stock,id_tienda,id_unidad,id_estado_producto)
VALUES
(1,'Arroz 1 kg','Arroz blanco de primera calidad',4800.00,120,1,1,1),
(2,'Aceite vegetal 1 L','Aceite vegetal comestible',9500.00,80,1,4,1),
(3,'Azúcar 1 kg','Azúcar blanca refinada',5200.00,65,1,1,1),
(4,'Leche 1 L','Leche entera UHT',4200.00,100,1,4,1),
(5,'Café 500 g','Café molido tradicional',18500.00,40,1,1,1),
(6,'Detergente 1 kg','Detergente para ropa',12800.00,55,2,1,1),
(7,'Jabón de baño','Jabón de tocador',4200.00,150,2,1,1),
(8,'Papel higiénico x4','Paquete de cuatro rollos',8900.00,70,2,5,1),
(9,'Gaseosa 1.5 L','Bebida gaseosa',6500.00,90,3,4,1),
(10,'Atún en lata','Atún en aceite, lata individual',7200.00,35,3,1,1),
(11,'Harina de trigo 1 kg','Harina de trigo para cocina',5600.00,45,3,1,1),
(12,'Producto de prueba','Producto temporal para probar CRUD',15000.00,0,3,1,2);

-- Movimientos de inventario
INSERT INTO movimientos_inventario
(id_movimiento,id_producto,id_tipo_movimiento,cantidad,motivo,fecha_movimiento)
VALUES
(1,1,1,150,'Compra inicial','2026-08-01 09:00:00'),
(2,1,2,30,'Venta a crédito','2026-08-05 14:20:00'),
(3,2,1,100,'Compra a proveedor','2026-08-02 10:30:00'),
(4,3,1,80,'Compra inicial','2026-08-03 08:45:00'),
(5,3,2,15,'Venta mostrador','2026-08-10 16:10:00'),
(6,5,1,50,'Reposición de inventario','2026-08-12 11:15:00'),
(7,6,2,10,'Venta a crédito','2026-08-14 13:40:00'),
(8,7,1,200,'Compra a proveedor','2026-08-15 09:25:00'),
(9,8,2,20,'Venta mostrador','2026-08-18 17:00:00'),
(10,9,1,100,'Compra a proveedor','2026-08-20 10:00:00'),
(11,10,2,5,'Venta a crédito','2026-08-22 15:30:00'),
(12,12,3,0,'Producto creado para pruebas CRUD','2026-08-25 12:00:00');

-- Solicitudes de crédito
INSERT INTO solicitudes_credito
(id_solicitud,id_cliente,id_tienda,monto_total,saldo_pendiente,id_estado_solicitud,fecha_solicitud,fecha_aprobacion,fecha_vencimiento,observaciones)
VALUES
(1,1,1,156000.00,156000.00,1,'2026-08-20 09:15:00',NULL,'2026-09-20','Solicitud pendiente de revisión'),
(2,2,1,285000.00,285000.00,2,'2026-08-15 11:00:00','2026-08-15 14:30:00','2026-09-15','Crédito aprobado'),
(3,3,2,98000.00,0.00,4,'2026-07-10 10:00:00','2026-07-10 12:00:00','2026-08-10','Crédito pagado'),
(4,4,3,450000.00,450000.00,2,'2026-08-05 15:20:00','2026-08-06 09:00:00','2026-09-05','Cliente recurrente'),
(5,5,1,125000.00,35000.00,2,'2026-07-25 13:00:00','2026-07-25 15:00:00','2026-08-25','Pago parcial registrado');

-- Detalles de solicitudes
INSERT INTO detalle_solicitud_credito
(id_detalle_solicitud,id_solicitud,id_producto,cantidad,precio_unitario,subtotal)
VALUES
(1,1,1,10,4800.00,48000.00),(2,1,5,4,18500.00,74000.00),(3,1,7,8,4200.00,33600.00),
(4,2,2,10,9500.00,95000.00),(5,2,6,10,12800.00,128000.00),(6,2,8,7,8900.00,62300.00),
(7,3,4,10,4200.00,42000.00),(8,3,9,8,6500.00,52000.00),(9,4,1,20,4800.00,96000.00),
(10,4,3,15,5200.00,78000.00),(11,4,5,10,18500.00,185000.00),(12,4,7,10,4200.00,42000.00),
(13,5,2,5,9500.00,47500.00),(14,5,6,5,12800.00,64000.00),(15,5,7,3,4200.00,12600.00);

-- Facturas
INSERT INTO facturas
(id_factura,numero_factura,id_solicitud,id_cliente,id_tienda,subtotal,impuestos,total,fecha_emision,id_estado_factura)
VALUES
(1,'FAC-2026-0001',2,2,1,285000.00,54150.00,339150.00,'2026-08-15 14:35:00',1),
(2,'FAC-2026-0002',3,3,2,94000.00,17860.00,111860.00,'2026-07-10 12:05:00',2),
(3,'FAC-2026-0003',4,4,3,401000.00,76190.00,477190.00,'2026-08-06 09:10:00',1),
(4,'FAC-2026-0004',5,5,1,124100.00,23579.00,147679.00,'2026-07-25 15:05:00',2);

-- Detalles de facturas
INSERT INTO detalle_facturas
(id_detalle_factura,id_factura,id_producto,cantidad,precio_unitario,subtotal)
VALUES
(1,1,2,10,9500.00,95000.00),(2,1,6,10,12800.00,128000.00),(3,1,8,7,8900.00,62300.00),
(4,2,4,10,4200.00,42000.00),(5,2,9,8,6500.00,52000.00),
(6,3,1,20,4800.00,96000.00),(7,3,3,15,5200.00,78000.00),(8,3,5,10,18500.00,185000.00),(9,3,7,10,4200.00,42000.00),
(10,4,2,5,9500.00,47500.00),(11,4,6,5,12800.00,64000.00),(12,4,7,3,4200.00,12600.00);

-- Pagos
INSERT INTO pagos
(id_pago,id_factura,numero_referencia_pago,monto_pagado,fecha_pago,id_tipo_pago,observaciones)
VALUES
(1,2,'REF-TRX-100001',111860.00,'2026-07-10 16:20:00',2,'Pago total por transferencia'),
(2,4,'REF-TRX-100002',80000.00,'2026-08-05 10:30:00',1,'Abono inicial'),
(3,4,'REF-TRX-100003',32700.00,'2026-08-20 11:45:00',2,'Segundo abono');

-- Permisos
INSERT INTO rol_permisos (id_rol_permiso,codigo,descripcion) VALUES
(1,'USUARIOS_VER','Consultar usuarios'),
(2,'USUARIOS_CREAR','Crear usuarios'),
(3,'USUARIOS_EDITAR','Editar usuarios'),
(4,'USUARIOS_ELIMINAR','Eliminar usuarios'),
(5,'PRODUCTOS_VER','Consultar productos'),
(6,'PRODUCTOS_CREAR','Crear productos'),
(7,'PRODUCTOS_EDITAR','Editar productos'),
(8,'PRODUCTOS_ELIMINAR','Eliminar productos'),
(9,'INVENTARIO_MOVIMIENTOS','Gestionar movimientos de inventario'),
(10,'CREDITOS_VER','Consultar créditos'),
(11,'CREDITOS_CREAR','Crear solicitudes de crédito'),
(12,'CREDITOS_APROBAR','Aprobar/rechazar créditos'),
(13,'PAGOS_REGISTRAR','Registrar pagos'),
(14,'FACTURAS_VER','Consultar facturas');

INSERT INTO roles_y_permisos (id_rol,id_rol_permiso) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),
(2,1),(2,2),(2,3),(2,5),(2,6),(2,7),(2,9),(2,10),(2,11),(2,13),(2,14),
(3,1),(3,3),(3,5),(3,7),(3,9),(3,10),(3,12),(3,13),(3,14);

SET FOREIGN_KEY_CHECKS=1;
