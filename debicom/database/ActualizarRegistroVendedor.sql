USE debicom;

-- Asegura que el rol Vendedor exista.
INSERT INTO roles (nombre_rol)
SELECT 'Vendedor'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE UPPER(nombre_rol) = 'VENDEDOR'
);

-- Compradores normales: id_rol NULL. Un vendedor se identifica por el rol VENDEDOR.
-- Consulta parametrizada equivalente a la utilizada por UsuarioDAO:
-- :id_usuario representa el identificador del usuario autenticado.
UPDATE usuarios
SET id_rol = (
    SELECT r.id_rol
    FROM roles r
    WHERE UPPER(r.nombre_rol) = 'VENDEDOR'
    LIMIT 1
)
WHERE id_usuario = :id_usuario
  AND id_rol IS NULL;

-- Verificación del cambio.
SELECT u.id_usuario, u.correo, r.nombre_rol
FROM usuarios u
LEFT JOIN roles r ON r.id_rol = u.id_rol
WHERE u.id_usuario = :id_usuario;
