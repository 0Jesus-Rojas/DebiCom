/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.RolDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaRolEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolDAO miRolDAO = new RolDAO();
        
        System.out.print("Ingrese el ID del rol que desea eliminar: ");
        int idRol = sc.nextInt();
        
        boolean respuesta = miRolDAO.eliminarRol(idRol);
        
        if (respuesta) {
            System.out.println("El rol con ID " + idRol + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el rol (verifique si el ID existe).");
        }
    }
}