/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.UnidadDAO;
import Modelo.Unidades;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaUnidadActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UnidadDAO miUnidadDAO = new UnidadDAO();
        Unidades miUnidad = new Unidades();
        
        System.out.print("Ingrese el ID de la unidad que desea actualizar: ");
        miUnidad.setIdUnidad(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer
        
        System.out.print("Ingrese el nuevo nombre de la unidad: ");
        miUnidad.setNombreUnidad(sc.nextLine());
        
        boolean respuesta = miUnidadDAO.actualizarUnidad(miUnidad);
        
        if (respuesta) {
            System.out.println("Los datos de la unidad se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información de la unidad.");
        }
    }
}