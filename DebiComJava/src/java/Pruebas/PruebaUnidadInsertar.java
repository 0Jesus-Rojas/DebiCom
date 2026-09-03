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
public class PruebaUnidadInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Unidades miUnidad = new Unidades();
        UnidadDAO dao = new UnidadDAO();
        
        System.out.print("Ingrese el nombre de la unidad: ");
        miUnidad.setNombreUnidad(sc.nextLine());
        
        boolean resultado = dao.insertarUnidad(miUnidad);

        if (resultado) {
            System.out.println("Se registró la unidad correctamente");
        } else {
            System.out.println("No se pudo registrar la unidad");
        }
    }
}