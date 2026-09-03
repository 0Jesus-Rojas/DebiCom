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
public class PruebaRolYPermisoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolYPermisoDAO miRolYPermisoDAO = new RolYPermisoDAO();
        
        System.out.print("Ingrese el id del rol que desea buscar: ");
        int idRol = sc.nextInt();
        
        System.out.print("Ingrese el id del rol permiso que desea buscar: ");
        int idRolPermiso = sc.nextInt();
        
        RolesYPermisos miRolYPermiso = miRolYPermisoDAO.consultarRolYPermiso(idRol, idRolPermiso);
        if (miRolYPermiso != null){
            System.out.println("Registro encontrado exitosamente.");
            System.out.println("Id Rol: " + miRolYPermiso.getIdRol());
            System.out.println("Id Rol Permiso: " + miRolYPermiso.getIdRolPermiso());
        } else {
            System.out.println("No se encontro el rol y permiso");
        }
    }
}