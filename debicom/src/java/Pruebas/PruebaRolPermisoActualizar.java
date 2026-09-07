/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.RolPermisoDAO;
import Modelo.RolPermisos;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaRolPermisoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolPermisoDAO miRolPermisoDAO = new RolPermisoDAO();
        RolPermisos miRolPermiso = new RolPermisos();
        
        System.out.print("Ingrese el ID del rol permiso que desea actualizar: ");
        miRolPermiso.setIdRolPermiso(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer
        
        System.out.print("Ingrese el nuevo codigo: ");
        miRolPermiso.setCodigo(sc.nextLine());
        
        System.out.print("Ingrese la nueva descripcion: ");
        miRolPermiso.setDescripcion(sc.nextLine());
        
        boolean respuesta = miRolPermisoDAO.actualizarRolPermiso(miRolPermiso);
        
        if (respuesta) {
            System.out.println("Los datos del rol permiso se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del rol permiso.");
        }
    }
}