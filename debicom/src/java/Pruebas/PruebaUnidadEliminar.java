/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.UnidadDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaUnidadEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UnidadDAO miUnidadDAO = new UnidadDAO();
        
        System.out.print("Ingrese el ID de la unidad que desea eliminar: ");
        int idUnidad = sc.nextInt();
        
        boolean respuesta = miUnidadDAO.eliminarUnidad(idUnidad);
        
        if (respuesta) {
            System.out.println("La unidad con ID " + idUnidad + " fue eliminada exitosamente.");
        } else {
            System.out.println("No se pudo eliminar la unidad (verifique si el ID existe).");
        }
    }
}