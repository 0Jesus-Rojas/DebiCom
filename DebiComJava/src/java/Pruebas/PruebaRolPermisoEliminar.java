/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.RolPermisoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaRolPermisoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolPermisoDAO miRolPermisoDAO = new RolPermisoDAO();
        
        System.out.print("Ingrese el ID del rol permiso que desea eliminar: ");
        int idRolPermiso = sc.nextInt();
        
        boolean respuesta = miRolPermisoDAO.eliminarRolPermiso(idRolPermiso);
        
        if (respuesta) {
            System.out.println("El rol permiso con ID " + idRolPermiso + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el rol permiso (verifique si el ID existe).");
        }
    }
}