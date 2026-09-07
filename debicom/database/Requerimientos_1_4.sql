USE debicom;

CREATE TABLE IF NOT EXISTS pagos_presenciales (
    id_pago_presencial INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    id_vendedor INT NOT NULL,
    monto_pagado DECIMAL(12,2) NOT NULL,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    numero_referencia VARCHAR(100) NOT NULL,
    observaciones VARCHAR(255) NULL,
    PRIMARY KEY (id_pago_presencial),
    UNIQUE KEY uk_pagos_presenciales_referencia (numero_referencia),
    INDEX idx_pagos_presenciales_cliente (id_cliente),
    INDEX idx_pagos_presenciales_vendedor (id_vendedor),
    CONSTRAINT fk_pagos_presenciales_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente),
    CONSTRAINT fk_pagos_presenciales_vendedor
        FOREIGN KEY (id_vendedor) REFERENCES usuarios (id_usuario),
    CONSTRAINT chk_pagos_presenciales_monto
        CHECK (monto_pagado > 0)
) ENGINE=InnoDB;
