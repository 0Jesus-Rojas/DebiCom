/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.UsuarioDAO;
import Modelo.Usuarios;
import java.sql.Date;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaUsuarioInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Usuarios miUsuario = new Usuarios();
        UsuarioDAO dao = new UsuarioDAO();
        
        System.out.print("Ingrese el nombre: ");
        miUsuario.setNombre(sc.nextLine());
        
        System.out.print("Ingrese el apellido: ");
        miUsuario.setApellido(sc.nextLine());
        
        System.out.print("Ingrese la identificación: ");
        miUsuario.setIdentificacion(sc.nextLine());
        
        System.out.print("Ingrese la fecha de nacimiento (YYYY-MM-DD): ");
        miUsuario.setFechaNacimiento(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese el correo: ");
        miUsuario.setCorreo(sc.nextLine());
        
        System.out.print("Ingrese el teléfono: ");
        miUsuario.setTelefono(sc.nextLine());
        
        System.out.print("Ingrese la dirección: ");
        miUsuario.setDireccion(sc.nextLine());
        
        System.out.print("Ingrese la contraseña (password): ");
        miUsuario.setPassword(sc.nextLine());
        
        System.out.print("Ingrese la fecha de vencimiento de clave (YYYY-MM-DD): ");
        miUsuario.setFechaVencimientoClave(Date.valueOf(sc.nextLine()));
        
        System.out.print("Autoriza datos (true/false): ");
        miUsuario.setAutorizaDatos(sc.nextBoolean());
        
        System.out.print("Ingrese el ID del tipo de identificación: ");
        miUsuario.setIdTipoIdentificacion(sc.nextInt());
        
        System.out.print("Ingrese el ID del rol: ");
        miUsuario.setIdRol(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la fecha de registro (YYYY-MM-DD): ");
        miUsuario.setFechaRegistro(Date.valueOf(sc.nextLine()));
        
        boolean resultado = dao.insertarUsuario(miUsuario);

        if (resultado) {
            System.out.println("Se registró el usuario correctamente");
        } else {
            System.out.println("No se pudo registrar el usuario");
        }
    }
}