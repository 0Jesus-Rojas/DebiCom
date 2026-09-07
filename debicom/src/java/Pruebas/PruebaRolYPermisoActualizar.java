/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.RolYPermisoDAO;
import Modelo.RolesYPermisos;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaRolYPermisoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolYPermisoDAO miRolYPermisoDAO = new RolYPermisoDAO();
        RolesYPermisos miRolYPermiso = new RolesYPermisos();
        
        System.out.print("Ingrese el ID del rol que desea actualizar: ");
        miRolYPermiso.setIdRol(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del rol permiso: ");
        miRolYPermiso.setIdRolPermiso(sc.nextInt());
        
        boolean respuesta = miRolYPermisoDAO.actualizarRolYPermiso(miRolYPermiso);
        
        if (respuesta) {
            System.out.println("Los datos del rol y permiso se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información.");
        }
    }
}