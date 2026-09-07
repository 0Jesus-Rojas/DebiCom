-- MySQL Workbench Forward Engineering
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema debicom
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `debicom` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `debicom` ;

-- -----------------------------------------------------
-- Table `debicom`.`tipo_identificaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tipo_identificaciones` (
  `id_tipo_identificacion` INT NOT NULL AUTO_INCREMENT,
  `nombre_tipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_identificacion`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`roles` (
  `id_rol` INT NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_rol`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`usuarios` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `apellido` VARCHAR(100) NOT NULL,
  `identificacion` VARCHAR(45) NOT NULL,
  `fecha_nacimiento` DATE NOT NULL,
  `correo` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(30) NOT NULL,
  `direccion` VARCHAR(150) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `fecha_vencimiento_clave` DATE NULL,
  `autoriza_datos` TINYINT NOT NULL DEFAULT 1,
  `id_tipo_identificacion` INT NOT NULL,
  `id_rol` INT NULL, -- AHORA PERMITE NULL: Los compradores normales no necesitan rol.
  `fecha_registro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `identificacion_UNIQUE` (`identificacion` ASC),
  UNIQUE INDEX `correo_UNIQUE` (`correo` ASC),
  INDEX `fk_usuarios_tipo_identificaciones_idx` (`id_tipo_identificacion` ASC),
  INDEX `fk_usuarios_roles_idx` (`id_rol` ASC),
  CONSTRAINT `fk_usuarios_tipo_identificaciones`
    FOREIGN KEY (`id_tipo_identificacion`)
    REFERENCES `debicom`.`tipo_identificaciones` (`id_tipo_identificacion`),
  CONSTRAINT `fk_usuarios_roles`
    FOREIGN KEY (`id_rol`)
    REFERENCES `debicom`.`roles` (`id_rol`)
    ON DELETE SET NULL)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`clientes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`clientes` (
  `id_cliente` INT NOT NULL AUTO_INCREMENT,
  `credito_actual` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `id_usuario` INT NOT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE INDEX `id_usuario_UNIQUE` (`id_usuario` ASC),
  CONSTRAINT `fk_clientes_usuarios`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `debicom`.`usuarios` (`id_usuario`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`tiendas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tiendas` (
  `id_tienda` INT NOT NULL AUTO_INCREMENT,
  `nombre_tienda` VARCHAR(100) NOT NULL,
  `nit` VARCHAR(45) NOT NULL,
  `direccion` VARCHAR(150) NOT NULL,
  `telefono` VARCHAR(30) NOT NULL,
  `id_vendedor` INT NOT NULL,
  `fecha_registro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_tienda`),
  UNIQUE INDEX `nit_UNIQUE` (`nit` ASC),
  INDEX `fk_tiendas_usuarios_idx` (`id_vendedor` ASC),
  CONSTRAINT `fk_tiendas_usuarios`
    FOREIGN KEY (`id_vendedor`)
    REFERENCES `debicom`.`usuarios` (`id_usuario`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`unidades`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`unidades` (
  `id_unidad` INT NOT NULL AUTO_INCREMENT,
  `nombre_unidad` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_unidad`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`estados_producto` (NUEVA TABLA)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`estados_producto` (
  `id_estado_producto` INT NOT NULL AUTO_INCREMENT,
  `nombre_estado` VARCHAR(45) NOT NULL, -- Ej: 'ACTIVO', 'INACTIVO'
  PRIMARY KEY (`id_estado_producto`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`productos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`productos` (
  `id_producto` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `stock` INT NOT NULL DEFAULT 0,
  `id_tienda` INT NOT NULL,
  `id_unidad` INT NOT NULL,
  `id_estado_producto` INT NOT NULL DEFAULT 1, -- Reemplaza al ENUM
  PRIMARY KEY (`id_producto`),
  INDEX `fk_productos_tiendas_idx` (`id_tienda` ASC),
  INDEX `fk_productos_unidades_idx` (`id_unidad` ASC),
  INDEX `fk_productos_estados_idx` (`id_estado_producto` ASC),
  CONSTRAINT `fk_productos_tiendas`
    FOREIGN KEY (`id_tienda`)
    REFERENCES `debicom`.`tiendas` (`id_tienda`),
  CONSTRAINT `fk_productos_unidades`
    FOREIGN KEY (`id_unidad`)
    REFERENCES `debicom`.`unidades` (`id_unidad`),
  CONSTRAINT `fk_productos_estados`
    FOREIGN KEY (`id_estado_producto`)
    REFERENCES `debicom`.`estados_producto` (`id_estado_producto`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`tipos_movimiento` (NUEVA TABLA)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tipos_movimiento` (
  `id_tipo_movimiento` INT NOT NULL AUTO_INCREMENT,
  `nombre_tipo` VARCHAR(45) NOT NULL, -- Ej: 'ENTRADA', 'SALIDA'
  PRIMARY KEY (`id_tipo_movimiento`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`movimientos_inventario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`movimientos_inventario` (
  `id_movimiento` INT NOT NULL AUTO_INCREMENT,
  `id_producto` INT NOT NULL,
  `id_tipo_movimiento` INT NOT NULL, -- Reemplaza al ENUM
  `cantidad` INT NOT NULL,
  `motivo` VARCHAR(150) NOT NULL, 
  `fecha_movimiento` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_movimiento`),
  INDEX `fk_movimientos_productos_idx` (`id_producto` ASC),
  INDEX `fk_movimientos_tipos_idx` (`id_tipo_movimiento` ASC),
  CONSTRAINT `fk_movimientos_productos`
    FOREIGN KEY (`id_producto`)
    REFERENCES `debicom`.`productos` (`id_producto`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_movimientos_tipos`
    FOREIGN KEY (`id_tipo_movimiento`)
    REFERENCES `debicom`.`tipos_movimiento` (`id_tipo_movimiento`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`estados_solicitud` (NUEVA TABLA)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`estados_solicitud` (
  `id_estado_solicitud` INT NOT NULL AUTO_INCREMENT,
  `nombre_estado` VARCHAR(45) NOT NULL, -- Ej: 'PENDIENTE', 'APROBADO', etc.
  PRIMARY KEY (`id_estado_solicitud`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`solicitudes_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`solicitudes_credito` (
  `id_solicitud` INT NOT NULL AUTO_INCREMENT,
  `id_cliente` INT NOT NULL,
  `id_tienda` INT NOT NULL,
  `monto_total` DECIMAL(12,2) NOT NULL,
  `saldo_pendiente` DECIMAL(12,2) NOT NULL,
  `id_estado_solicitud` INT NOT NULL DEFAULT 1, -- Reemplaza al ENUM
  `fecha_solicitud` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_aprobacion` DATETIME NULL,
  `fecha_vencimiento` DATE NOT NULL,
  `observaciones` VARCHAR(255) NULL,
  PRIMARY KEY (`id_solicitud`),
  INDEX `fk_solicitudes_clientes_idx` (`id_cliente` ASC),
  INDEX `fk_solicitudes_tiendas_idx` (`id_tienda` ASC),
  INDEX `fk_solicitudes_estados_idx` (`id_estado_solicitud` ASC),
  CONSTRAINT `fk_solicitudes_clientes`
    FOREIGN KEY (`id_cliente`)
    REFERENCES `debicom`.`clientes` (`id_cliente`),
  CONSTRAINT `fk_solicitudes_tiendas`
    FOREIGN KEY (`id_tienda`)
    REFERENCES `debicom`.`tiendas` (`id_tienda`),
  CONSTRAINT `fk_solicitudes_estados`
    FOREIGN KEY (`id_estado_solicitud`)
    REFERENCES `debicom`.`estados_solicitud` (`id_estado_solicitud`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`detalle_solicitud_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`detalle_solicitud_credito` (
  `id_detalle_solicitud` INT NOT NULL AUTO_INCREMENT,
  `id_solicitud` INT NOT NULL,
  `id_producto` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_solicitud`),
  INDEX `fk_det_solic_solicitudes_idx` (`id_solicitud` ASC),
  INDEX `fk_det_solic_productos_idx` (`id_producto` ASC),
  CONSTRAINT `fk_det_solic_solicitudes`
    FOREIGN KEY (`id_solicitud`)
    REFERENCES `debicom`.`solicitudes_credito` (`id_solicitud`),
  CONSTRAINT `fk_det_solic_productos`
    FOREIGN KEY (`id_producto`)
    REFERENCES `debicom`.`productos` (`id_producto`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`estados_factura` (NUEVA TABLA)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`estados_factura` (
  `id_estado_factura` INT NOT NULL AUTO_INCREMENT,
  `nombre_estado` VARCHAR(45) NOT NULL, -- Ej: 'PENDIENTE', 'PAGADA', etc.
  PRIMARY KEY (`id_estado_factura`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`facturas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`facturas` (
  `id_factura` INT NOT NULL AUTO_INCREMENT,
  `numero_factura` VARCHAR(45) NOT NULL,
  `id_solicitud` INT NOT NULL,
  `id_cliente` INT NOT NULL,
  `id_tienda` INT NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  `impuestos` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total` DECIMAL(12,2) NOT NULL,
  `fecha_emision` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_estado_factura` INT NOT NULL DEFAULT 1, -- Reemplaza al ENUM
  PRIMARY KEY (`id_factura`),
  UNIQUE INDEX `numero_factura_UNIQUE` (`numero_factura` ASC),
  INDEX `fk_facturas_solicitudes_idx` (`id_solicitud` ASC),
  INDEX `fk_facturas_clientes_idx` (`id_cliente` ASC),
  INDEX `fk_facturas_tiendas_idx` (`id_tienda` ASC),
  INDEX `fk_facturas_estados_idx` (`id_estado_factura` ASC),
  CONSTRAINT `fk_facturas_solicitudes`
    FOREIGN KEY (`id_solicitud`)
    REFERENCES `debicom`.`solicitudes_credito` (`id_solicitud`),
  CONSTRAINT `fk_facturas_clientes`
    FOREIGN KEY (`id_cliente`)
    REFERENCES `debicom`.`clientes` (`id_cliente`),
  CONSTRAINT `fk_facturas_tiendas`
    FOREIGN KEY (`id_tienda`)
    REFERENCES `debicom`.`tiendas` (`id_tienda`),
  CONSTRAINT `fk_facturas_estados`
    FOREIGN KEY (`id_estado_factura`)
    REFERENCES `debicom`.`estados_factura` (`id_estado_factura`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`detalle_facturas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`detalle_facturas` (
  `id_detalle_factura` INT NOT NULL AUTO_INCREMENT,
  `id_factura` INT NOT NULL,
  `id_producto` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_factura`),
  INDEX `fk_detalle_facturas_facturas_idx` (`id_factura` ASC),
  INDEX `fk_detalle_facturas_productos_idx` (`id_producto` ASC),
  CONSTRAINT `fk_detalle_facturas_facturas`
    FOREIGN KEY (`id_factura`)
    REFERENCES `debicom`.`facturas` (`id_factura`),
  CONSTRAINT `fk_detalle_facturas_productos`
    FOREIGN KEY (`id_producto`)
    REFERENCES `debicom`.`productos` (`id_producto`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`tipo_pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tipo_pago` (
  `id_tipo_pago` INT NOT NULL AUTO_INCREMENT,
  `nombre_pago` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_pago`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`pagos`
-- -----------------------------------------------------
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Table `debicom`.`pagos_presenciales`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`pagos_presenciales` (
  `id_pago_presencial` INT NOT NULL AUTO_INCREMENT,
  `id_solicitud` INT NOT NULL,
  `id_cliente` INT NOT NULL,
  `id_vendedor` INT NOT NULL,
  `monto_pagado` DECIMAL(12,2) NOT NULL,
  `fecha_pago` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `numero_referencia` VARCHAR(100) NOT NULL,
  `observaciones` VARCHAR(255) NULL,
  PRIMARY KEY (`id_pago_presencial`),
  UNIQUE INDEX `numero_referencia_pago_presencial_UNIQUE` (`numero_referencia` ASC),
  INDEX `fk_pagos_presenciales_solicitud_idx` (`id_solicitud` ASC),
  INDEX `fk_pagos_presenciales_cliente_idx` (`id_cliente` ASC),
  INDEX `fk_pagos_presenciales_vendedor_idx` (`id_vendedor` ASC),
  CONSTRAINT `fk_pagos_presenciales_solicitud` FOREIGN KEY (`id_solicitud`) REFERENCES `debicom`.`solicitudes_credito` (`id_solicitud`),
  CONSTRAINT `fk_pagos_presenciales_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `debicom`.`clientes` (`id_cliente`),
  CONSTRAINT `fk_pagos_presenciales_vendedor` FOREIGN KEY (`id_vendedor`) REFERENCES `debicom`.`usuarios` (`id_usuario`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`rol_permisos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`rol_permisos` (
  `id_rol_permiso` INT NOT NULL AUTO_INCREMENT,
  `codigo` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_rol_permiso`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `debicom`.`roles_y_permisos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`roles_y_permisos` (
  `id_rol` INT NOT NULL,
  `id_rol_permiso` INT NOT NULL,
  PRIMARY KEY (`id_rol`, `id_rol_permiso`),
  INDEX `fk_roles_has_permisos_permiso_idx` (`id_rol_permiso` ASC),
  INDEX `fk_roles_has_permisos_rol_idx` (`id_rol` ASC),
  CONSTRAINT `fk_roles_has_permisos_rol`
    FOREIGN KEY (`id_rol`)
    REFERENCES `debicom`.`roles` (`id_rol`),
  CONSTRAINT `fk_roles_has_permisos_permiso`
    FOREIGN KEY (`id_rol_permiso`)
    REFERENCES `debicom`.`rol_permisos` (`id_rol_permiso`))
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Índices utilizados por los historiales del vendedor.
CREATE INDEX idx_solicitudes_creditos_tienda_estado_fecha
    ON solicitudes_credito (id_tienda, id_estado_solicitud, fecha_solicitud);
CREATE INDEX idx_pagos_presenciales_vendedor_fecha
    ON pagos_presenciales (id_vendedor, fecha_pago);
