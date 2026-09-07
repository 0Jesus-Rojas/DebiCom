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
public class PruebaUsuarioActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuarioDAO miUsuarioDAO = new UsuarioDAO();
        Usuarios miUsuario = new Usuarios();
        
        System.out.print("Ingrese el ID del usuario que desea actualizar: ");
        miUsuario.setIdUsuario(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese el nuevo nombre: ");
        miUsuario.setNombre(sc.nextLine());
        
        System.out.print("Ingrese el nuevo apellido: ");
        miUsuario.setApellido(sc.nextLine());
        
        System.out.print("Ingrese la nueva identificación: ");
        miUsuario.setIdentificacion(sc.nextLine());
        
        System.out.print("Ingrese la nueva fecha de nacimiento (YYYY-MM-DD): ");
        miUsuario.setFechaNacimiento(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese el nuevo correo: ");
        miUsuario.setCorreo(sc.nextLine());
        
        System.out.print("Ingrese el nuevo teléfono: ");
        miUsuario.setTelefono(sc.nextLine());
        
        System.out.print("Ingrese la nueva dirección: ");
        miUsuario.setDireccion(sc.nextLine());
        
        System.out.print("Ingrese la nueva contraseña (password): ");
        miUsuario.setPassword(sc.nextLine());
        
        System.out.print("Ingrese la nueva fecha de vencimiento de clave (YYYY-MM-DD): ");
        miUsuario.setFechaVencimientoClave(Date.valueOf(sc.nextLine()));
        
        System.out.print("Autoriza datos (true/false): ");
        miUsuario.setAutorizaDatos(sc.nextBoolean());
        
        System.out.print("Ingrese el nuevo ID del tipo de identificación: ");
        miUsuario.setIdTipoIdentificacion(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del rol: ");
        miUsuario.setIdRol(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la nueva fecha de registro (YYYY-MM-DD): ");
        miUsuario.setFechaRegistro(Date.valueOf(sc.nextLine()));
        
        boolean respuesta = miUsuarioDAO.actualizarUsuario(miUsuario);
        
        if (respuesta) {
            System.out.println("Los datos del usuario se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del usuario.");
        }
    }
}