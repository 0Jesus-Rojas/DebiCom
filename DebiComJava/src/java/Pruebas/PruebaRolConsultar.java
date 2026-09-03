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
public class PruebaRolConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RolDAO miRolDAO = new RolDAO();
        
        System.out.print("Ingrese el ID del rol que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Roles miRol = miRolDAO.consultarRol(busqueda);
        if (miRol != null){
            System.out.println("ID del rol: " + miRol.getIdRol());
            System.out.println("Nombre del rol: " + miRol.getNombreRol());
        }else{
            System.out.println("No se encontró el rol[cite: 3]");
        }
    }
}