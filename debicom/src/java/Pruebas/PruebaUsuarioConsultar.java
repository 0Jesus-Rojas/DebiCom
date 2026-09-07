/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.UsuarioDAO;
import Modelo.Usuarios;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaUsuarioConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuarioDAO miUsuarioDAO = new UsuarioDAO();
        
        System.out.print("Ingrese el ID del usuario que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Usuarios miUsuario = miUsuarioDAO.consultarUsuario(busqueda);
        if (miUsuario != null){
            System.out.println("--- DATOS DEL USUARIO ---");
            System.out.println("Nombre: " + miUsuario.getNombre());
            System.out.println("Apellido: " + miUsuario.getApellido());
            System.out.println("Identificación: " + miUsuario.getIdentificacion());
            System.out.println("Fecha Nacimiento: " + miUsuario.getFechaNacimiento());
            System.out.println("Correo: " + miUsuario.getCorreo());
            System.out.println("Teléfono: " + miUsuario.getTelefono());
            System.out.println("Dirección: " + miUsuario.getDireccion());
            System.out.println("Fecha Vencimiento Clave: " + miUsuario.getFechaVencimientoClave());
            System.out.println("Autoriza Datos: " + miUsuario.isAutorizaDatos());
            System.out.println("ID Tipo Identificación: " + miUsuario.getIdTipoIdentificacion());
            System.out.println("ID Rol: " + miUsuario.getIdRol());
            System.out.println("Fecha Registro: " + miUsuario.getFechaRegistro());
        } else {
            System.out.println("No se encontró el usuario");
        }
    }
}