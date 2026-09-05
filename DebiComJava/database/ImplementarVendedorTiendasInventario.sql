-- ============================================================
-- DebiCom - Migración para registro de vendedores,
--           multitienda e inventario
-- Compatible con MySQL 8.x
-- ============================================================

USE debicom;

-- 1. El rol Vendedor debe existir.
INSERT INTO roles (nombre_rol)
SELECT 'Vendedor'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE UPPER(nombre_rol) = 'VENDEDOR'
);

-- 2. Compradores sin rol pueden convertirse en vendedores.
--    La aplicación obtiene el id del rol por nombre; no depende de que sea 2.
--    La columna ya debe permitir NULL para compradores normales.
ALTER TABLE usuarios
    MODIFY COLUMN id_rol INT NULL;

-- 3. Una tienda pertenece a exactamente un usuario vendedor.
CREATE TABLE IF NOT EXISTS tiendas (
    id_tienda INT NOT NULL AUTO_INCREMENT,
    nombre_tienda VARCHAR(100) NOT NULL,
    nit VARCHAR(45) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    id_vendedor INT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_tienda),
    UNIQUE KEY nit_UNIQUE (nit),
    KEY fk_tiendas_usuarios_idx (id_vendedor),
    CONSTRAINT fk_tiendas_usuarios
        FOREIGN KEY (id_vendedor)
        REFERENCES usuarios (id_usuario)
) ENGINE=InnoDB;

-- 4. Cada producto queda ligado a una tienda concreta.
CREATE TABLE IF NOT EXISTS productos (
    id_producto INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    id_tienda INT NOT NULL,
    id_unidad INT NOT NULL,
    id_estado_producto INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id_producto),
    KEY fk_productos_tiendas_idx (id_tienda),
    KEY fk_productos_unidades_idx (id_unidad),
    KEY fk_productos_estados_idx (id_estado_producto),
    CONSTRAINT fk_productos_tiendas
        FOREIGN KEY (id_tienda)
        REFERENCES tiendas (id_tienda),
    CONSTRAINT fk_productos_unidades
        FOREIGN KEY (id_unidad)
        REFERENCES unidades (id_unidad),
    CONSTRAINT fk_productos_estados
        FOREIGN KEY (id_estado_producto)
        REFERENCES estados_producto (id_estado_producto)
) ENGINE=InnoDB;

-- 5. Catálogos mínimos necesarios para el formulario de inventario.
INSERT INTO unidades (nombre_unidad)
SELECT 'Unidad'
WHERE NOT EXISTS (
    SELECT 1 FROM unidades WHERE UPPER(nombre_unidad) = 'UNIDAD'
);

INSERT INTO estados_producto (nombre_estado)
SELECT 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM estados_producto WHERE UPPER(nombre_estado) = 'ACTIVO'
);

-- 6. Consultas de verificación.
SELECT id_rol, nombre_rol
FROM roles
WHERE UPPER(nombre_rol) = 'VENDEDOR';

SELECT id_usuario, correo
FROM usuarios
WHERE id_rol IS NULL;

SELECT t.id_tienda, t.nombre_tienda, t.id_vendedor
FROM tiendas t
ORDER BY t.id_vendedor, t.nombre_tienda;

SELECT p.id_producto, p.nombre, p.id_tienda
FROM productos p
ORDER BY p.id_tienda, p.nombre;
