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
public class PruebaUnidadConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UnidadDAO miUnidadDAO = new UnidadDAO();
        
        System.out.print("Ingrese el id de la unidad que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Unidades miUnidad = miUnidadDAO.consultarUnidad(busqueda);
        if (miUnidad != null){
            System.out.println("ID Unidad: " + miUnidad.getIdUnidad());
            System.out.println("Nombre de la unidad: " + miUnidad.getNombreUnidad());
        }else{
            System.out.println("No se encontro la unidad");
        }
    }
}