/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.PagoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaPagoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PagoDAO miPagoDAO = new PagoDAO();
        
        System.out.print("Ingrese el ID del pago que desea eliminar: ");
        int idPago = sc.nextInt();
        
        boolean respuesta = miPagoDAO.eliminarPago(idPago);
        
        if (respuesta) {
            System.out.println("El pago con ID " + idPago + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el pago (verifique si el ID existe).");
        }
    }
}