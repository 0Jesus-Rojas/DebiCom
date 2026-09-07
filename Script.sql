-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema debicom
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `debicom` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `debicom` ;

-- -----------------------------------------------------
-- Table `debicom`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`roles` (
  `id_rol` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_rol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`tipo_identificaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tipo_identificaciones` (
  `id_tipo_identificacion` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_tipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_identificacion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`usuarios` (
  `id_usuario` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `apellido` VARCHAR(100) NOT NULL,
  `identificacion` VARCHAR(45) NOT NULL,
  `fecha_nacimiento` DATE NOT NULL,
  `correo` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(30) NOT NULL,
  `direccion` VARCHAR(150) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `fecha_vencimiento_clave` DATE NULL DEFAULT NULL,
  `autoriza_datos` TINYINT(4) NOT NULL DEFAULT 1,
  `id_tipo_identificacion` INT(11) NOT NULL,
  `id_rol` INT(11) NULL DEFAULT NULL,
  `fecha_registro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `identificacion_UNIQUE` (`identificacion` ASC) ,
  UNIQUE INDEX `correo_UNIQUE` (`correo` ASC) ,
  INDEX `fk_usuarios_tipo_identificaciones_idx` (`id_tipo_identificacion` ASC) ,
  INDEX `fk_usuarios_roles_idx` (`id_rol` ASC) ,
  CONSTRAINT `fk_usuarios_roles`
    FOREIGN KEY (`id_rol`)
    REFERENCES `debicom`.`roles` (`id_rol`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_usuarios_tipo_identificaciones`
    FOREIGN KEY (`id_tipo_identificacion`)
    REFERENCES `debicom`.`tipo_identificaciones` (`id_tipo_identificacion`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`clientes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`clientes` (
  `id_cliente` INT(11) NOT NULL AUTO_INCREMENT,
  `credito_actual` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `id_usuario` INT(11) NOT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE INDEX `id_usuario_UNIQUE` (`id_usuario` ASC) ,
  CONSTRAINT `fk_clientes_usuarios`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `debicom`.`usuarios` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`estados_factura`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`estados_factura` (
  `id_estado_factura` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_estado` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_estado_factura`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`estados_solicitud`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`estados_solicitud` (
  `id_estado_solicitud` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_estado` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_estado_solicitud`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`tiendas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tiendas` (
  `id_tienda` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_tienda` VARCHAR(100) NOT NULL,
  `nit` VARCHAR(45) NOT NULL,
  `direccion` VARCHAR(150) NOT NULL,
  `telefono` VARCHAR(30) NOT NULL,
  `id_vendedor` INT(11) NOT NULL,
  `fecha_registro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id_tienda`),
  UNIQUE INDEX `nit_UNIQUE` (`nit` ASC) ,
  INDEX `fk_tiendas_usuarios_idx` (`id_vendedor` ASC) ,
  CONSTRAINT `fk_tiendas_usuarios`
    FOREIGN KEY (`id_vendedor`)
    REFERENCES `debicom`.`usuarios` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`solicitudes_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`solicitudes_credito` (
  `id_solicitud` INT(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` INT(11) NOT NULL,
  `id_tienda` INT(11) NOT NULL,
  `monto_total` DECIMAL(12,2) NOT NULL,
  `saldo_pendiente` DECIMAL(12,2) NOT NULL,
  `id_estado_solicitud` INT(11) NOT NULL DEFAULT 1,
  `fecha_solicitud` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  `fecha_aprobacion` DATETIME NULL DEFAULT NULL,
  `fecha_vencimiento` DATE NOT NULL,
  `observaciones` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_solicitud`),
  INDEX `fk_solicitudes_clientes_idx` (`id_cliente` ASC) ,
  INDEX `fk_solicitudes_tiendas_idx` (`id_tienda` ASC) ,
  INDEX `fk_solicitudes_estados_idx` (`id_estado_solicitud` ASC) ,
  CONSTRAINT `fk_solicitudes_clientes`
    FOREIGN KEY (`id_cliente`)
    REFERENCES `debicom`.`clientes` (`id_cliente`),
  CONSTRAINT `fk_solicitudes_estados`
    FOREIGN KEY (`id_estado_solicitud`)
    REFERENCES `debicom`.`estados_solicitud` (`id_estado_solicitud`),
  CONSTRAINT `fk_solicitudes_tiendas`
    FOREIGN KEY (`id_tienda`)
    REFERENCES `debicom`.`tiendas` (`id_tienda`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`facturas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`facturas` (
  `id_factura` INT(11) NOT NULL AUTO_INCREMENT,
  `numero_factura` VARCHAR(45) NOT NULL,
  `id_solicitud` INT(11) NOT NULL,
  `id_cliente` INT(11) NOT NULL,
  `id_tienda` INT(11) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  `impuestos` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total` DECIMAL(12,2) NOT NULL,
  `fecha_emision` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  `id_estado_factura` INT(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_factura`),
  UNIQUE INDEX `numero_factura_UNIQUE` (`numero_factura` ASC) ,
  INDEX `fk_facturas_solicitudes_idx` (`id_solicitud` ASC) ,
  INDEX `fk_facturas_clientes_idx` (`id_cliente` ASC) ,
  INDEX `fk_facturas_tiendas_idx` (`id_tienda` ASC) ,
  INDEX `fk_facturas_estados_idx` (`id_estado_factura` ASC) ,
  CONSTRAINT `fk_facturas_clientes`
    FOREIGN KEY (`id_cliente`)
    REFERENCES `debicom`.`clientes` (`id_cliente`),
  CONSTRAINT `fk_facturas_estados`
    FOREIGN KEY (`id_estado_factura`)
    REFERENCES `debicom`.`estados_factura` (`id_estado_factura`),
  CONSTRAINT `fk_facturas_solicitudes`
    FOREIGN KEY (`id_solicitud`)
    REFERENCES `debicom`.`solicitudes_credito` (`id_solicitud`),
  CONSTRAINT `fk_facturas_tiendas`
    FOREIGN KEY (`id_tienda`)
    REFERENCES `debicom`.`tiendas` (`id_tienda`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`estados_producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`estados_producto` (
  `id_estado_producto` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_estado` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_estado_producto`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`unidades`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`unidades` (
  `id_unidad` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_unidad` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_unidad`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`productos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`productos` (
  `id_producto` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NULL DEFAULT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `stock` INT(11) NOT NULL DEFAULT 0,
  `id_tienda` INT(11) NOT NULL,
  `id_unidad` INT(11) NOT NULL,
  `id_estado_producto` INT(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_producto`),
  INDEX `fk_productos_tiendas_idx` (`id_tienda` ASC) ,
  INDEX `fk_productos_unidades_idx` (`id_unidad` ASC) ,
  INDEX `fk_productos_estados_idx` (`id_estado_producto` ASC) ,
  CONSTRAINT `fk_productos_estados`
    FOREIGN KEY (`id_estado_producto`)
    REFERENCES `debicom`.`estados_producto` (`id_estado_producto`),
  CONSTRAINT `fk_productos_tiendas`
    FOREIGN KEY (`id_tienda`)
    REFERENCES `debicom`.`tiendas` (`id_tienda`),
  CONSTRAINT `fk_productos_unidades`
    FOREIGN KEY (`id_unidad`)
    REFERENCES `debicom`.`unidades` (`id_unidad`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`detalle_facturas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`detalle_facturas` (
  `id_detalle_factura` INT(11) NOT NULL AUTO_INCREMENT,
  `id_factura` INT(11) NOT NULL,
  `id_producto` INT(11) NOT NULL,
  `cantidad` INT(11) NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_factura`),
  INDEX `fk_detalle_facturas_facturas_idx` (`id_factura` ASC) ,
  INDEX `fk_detalle_facturas_productos_idx` (`id_producto` ASC) ,
  CONSTRAINT `fk_detalle_facturas_facturas`
    FOREIGN KEY (`id_factura`)
    REFERENCES `debicom`.`facturas` (`id_factura`),
  CONSTRAINT `fk_detalle_facturas_productos`
    FOREIGN KEY (`id_producto`)
    REFERENCES `debicom`.`productos` (`id_producto`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`detalle_solicitud_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`detalle_solicitud_credito` (
  `id_detalle_solicitud` INT(11) NOT NULL AUTO_INCREMENT,
  `id_solicitud` INT(11) NOT NULL,
  `id_producto` INT(11) NOT NULL,
  `cantidad` INT(11) NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_solicitud`),
  INDEX `fk_det_solic_solicitudes_idx` (`id_solicitud` ASC) ,
  INDEX `fk_det_solic_productos_idx` (`id_producto` ASC) ,
  CONSTRAINT `fk_det_solic_productos`
    FOREIGN KEY (`id_producto`)
    REFERENCES `debicom`.`productos` (`id_producto`),
  CONSTRAINT `fk_det_solic_solicitudes`
    FOREIGN KEY (`id_solicitud`)
    REFERENCES `debicom`.`solicitudes_credito` (`id_solicitud`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`tipos_movimiento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tipos_movimiento` (
  `id_tipo_movimiento` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_tipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_movimiento`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`movimientos_inventario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`movimientos_inventario` (
  `id_movimiento` INT(11) NOT NULL AUTO_INCREMENT,
  `id_producto` INT(11) NOT NULL,
  `id_tipo_movimiento` INT(11) NOT NULL,
  `cantidad` INT(11) NOT NULL,
  `motivo` VARCHAR(150) NOT NULL,
  `fecha_movimiento` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id_movimiento`),
  INDEX `fk_movimientos_productos_idx` (`id_producto` ASC) ,
  INDEX `fk_movimientos_tipos_idx` (`id_tipo_movimiento` ASC) ,
  CONSTRAINT `fk_movimientos_productos`
    FOREIGN KEY (`id_producto`)
    REFERENCES `debicom`.`productos` (`id_producto`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_movimientos_tipos`
    FOREIGN KEY (`id_tipo_movimiento`)
    REFERENCES `debicom`.`tipos_movimiento` (`id_tipo_movimiento`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`tipo_pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`tipo_pago` (
  `id_tipo_pago` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre_pago` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_pago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`pagos` (
  `id_pago` INT(11) NOT NULL AUTO_INCREMENT,
  `id_factura` INT(11) NOT NULL,
  `numero_referencia_pago` VARCHAR(100) NOT NULL,
  `monto_pagado` DECIMAL(12,2) NOT NULL,
  `fecha_pago` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  `id_tipo_pago` INT(11) NOT NULL,
  `observaciones` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  INDEX `fk_pagos_facturas_idx` (`id_factura` ASC) ,
  INDEX `fk_pagos_tipo_pago_idx` (`id_tipo_pago` ASC) ,
  CONSTRAINT `fk_pagos_facturas`
    FOREIGN KEY (`id_factura`)
    REFERENCES `debicom`.`facturas` (`id_factura`),
  CONSTRAINT `fk_pagos_tipo_pago`
    FOREIGN KEY (`id_tipo_pago`)
    REFERENCES `debicom`.`tipo_pago` (`id_tipo_pago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`pagos_presenciales`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`pagos_presenciales` (
  `id_pago_presencial` INT(11) NOT NULL AUTO_INCREMENT,
  `id_solicitud` INT(11) NOT NULL,
  `id_cliente` INT(11) NOT NULL,
  `id_vendedor` INT(11) NOT NULL,
  `monto_pagado` DECIMAL(12,2) NOT NULL,
  `fecha_pago` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  `numero_referencia` VARCHAR(100) NOT NULL,
  `observaciones` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_pago_presencial`),
  UNIQUE INDEX `uk_pagos_presenciales_referencia` (`numero_referencia` ASC) ,
  INDEX `idx_pagos_presenciales_solicitud` (`id_solicitud` ASC) ,
  INDEX `idx_pagos_presenciales_cliente` (`id_cliente` ASC) ,
  INDEX `idx_pagos_presenciales_vendedor` (`id_vendedor` ASC) ,
  CONSTRAINT `fk_pagos_presenciales_cliente`
    FOREIGN KEY (`id_cliente`)
    REFERENCES `debicom`.`clientes` (`id_cliente`),
  CONSTRAINT `fk_pagos_presenciales_solicitud`
    FOREIGN KEY (`id_solicitud`)
    REFERENCES `debicom`.`solicitudes_credito` (`id_solicitud`),
  CONSTRAINT `fk_pagos_presenciales_vendedor`
    FOREIGN KEY (`id_vendedor`)
    REFERENCES `debicom`.`usuarios` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`rol_permisos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`rol_permisos` (
  `id_rol_permiso` INT(11) NOT NULL AUTO_INCREMENT,
  `codigo` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_rol_permiso`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `debicom`.`roles_y_permisos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `debicom`.`roles_y_permisos` (
  `id_rol` INT(11) NOT NULL,
  `id_rol_permiso` INT(11) NOT NULL,
  PRIMARY KEY (`id_rol`, `id_rol_permiso`),
  INDEX `fk_roles_has_permisos_permiso_idx` (`id_rol_permiso` ASC) ,
  INDEX `fk_roles_has_permisos_rol_idx` (`id_rol` ASC) ,
  CONSTRAINT `fk_roles_has_permisos_permiso`
    FOREIGN KEY (`id_rol_permiso`)
    REFERENCES `debicom`.`rol_permisos` (`id_rol_permiso`),
  CONSTRAINT `fk_roles_has_permisos_rol`
    FOREIGN KEY (`id_rol`)
    REFERENCES `debicom`.`roles` (`id_rol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;