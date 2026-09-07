/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.DetalleSolicitudCreditoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaDetalleSolicitudCreditoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleSolicitudCreditoDAO miDetalleDAO = new DetalleSolicitudCreditoDAO();
        
        System.out.print("Ingrese el ID del detalle que desea eliminar: ");
        int idDetalle = sc.nextInt();
        
        boolean respuesta = miDetalleDAO.eliminarDetalle(idDetalle);
        
        if (respuesta) {
            System.out.println("El detalle con ID " + idDetalle + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el detalle (verifique si el ID existe).");
        }
    }
}