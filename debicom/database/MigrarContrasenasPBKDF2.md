# Migración de contraseñas de DebiCom

No existe una sentencia SQL reversible que permita convertir automáticamente una contraseña de texto plano en PBKDF2 sin conocer la contraseña desde Java.

El proyecto incorpora migración transparente: si `LoginServlet` encuentra un valor que no comienza por `PBKDF2$SHA256$`, compara temporalmente el texto recibido y, únicamente cuando la autenticación es correcta, genera un nuevo hash PBKDF2 y lo guarda mediante `UsuarioDAO.actualizarPassword()`.

Las contraseñas incluidas en `DatosDePrueba.sql` ya están almacenadas como hashes PBKDF2. Para las cuentas de demostración, las claves siguen siendo las mismas que estaban documentadas en el proyecto.
