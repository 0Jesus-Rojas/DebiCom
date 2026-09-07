/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.RolDAO;
import Modelo.Roles;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaRolActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolDAO miRolDAO = new RolDAO();
        Roles miRol = new Roles();
        
        System.out.print("Ingrese el ID del rol que desea actualizar: ");
        miRol.setIdRol(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer
        
        System.out.print("Ingrese el nuevo nombre del rol: ");
        miRol.setNombreRol(sc.nextLine());
        
        boolean respuesta = miRolDAO.actualizarRol(miRol);
        
        if (respuesta) {
            System.out.println("Los datos del rol se actualizaron correctamente.[cite: 2]");
        } else {
            System.out.println("No se pudo actualizar la información del rol.[cite: 2]");
        }
    }
}