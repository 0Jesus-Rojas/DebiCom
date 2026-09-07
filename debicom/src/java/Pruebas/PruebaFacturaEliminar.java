/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.FacturaDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaFacturaEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FacturaDAO miFacturaDAO = new FacturaDAO();
        
        System.out.print("Ingrese el ID de la factura que desea eliminar: ");
        int idFactura = sc.nextInt();
        
        boolean respuesta = miFacturaDAO.eliminarFactura(idFactura);
        
        if (respuesta) {
            System.out.println("La factura con ID " + idFactura + " fue eliminada exitosamente.");
        } else {
            System.out.println("No se pudo eliminar la factura (verifique si el ID existe).");
        }
    }
}