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
public class PruebaRolYPermisoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolesYPermisos miRolYPermiso = new RolesYPermisos();
        RolYPermisoDAO dao = new RolYPermisoDAO();
        
        System.out.print("Ingrese el ID del rol: ");
        miRolYPermiso.setIdRol(sc.nextInt());
        
        System.out.print("Ingrese el ID del rol permiso: ");
        miRolYPermiso.setIdRolPermiso(sc.nextInt());
        
        boolean resultado = dao.insertarRolYPermiso(miRolYPermiso);

        if (resultado) {
            System.out.println("Se registró el rol y permiso correctamente");
        } else {
            System.out.println("No se pudo registrar el rol y permiso");
        }
    }
}