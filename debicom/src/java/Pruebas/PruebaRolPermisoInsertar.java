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
public class PruebaRolPermisoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolPermisos miRolPermiso = new RolPermisos();
        RolPermisoDAO dao = new RolPermisoDAO();
        
        System.out.print("Ingrese el codigo del rol permiso: ");
        miRolPermiso.setCodigo(sc.nextLine());
        
        System.out.print("Ingrese la descripcion del rol permiso: ");
        miRolPermiso.setDescripcion(sc.nextLine());
        
        boolean resultado = dao.insertarRolPermiso(miRolPermiso);

        if (resultado) {
            System.out.println("Se registró el rol permiso correctamente");
        } else {
            System.out.println("No se pudo registrar el rol permiso");
        }
    }
}