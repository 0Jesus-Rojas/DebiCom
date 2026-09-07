/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.UsuarioDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaUsuarioEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuarioDAO miUsuarioDAO = new UsuarioDAO();
        
        System.out.print("Ingrese el ID del usuario que desea eliminar: ");
        int idUsuario = sc.nextInt();
        
        boolean respuesta = miUsuarioDAO.eliminarUsuario(idUsuario);
        
        if (respuesta) {
            System.out.println("El usuario con ID " + idUsuario + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el usuario (verifique si el ID existe).");
        }
    }
}