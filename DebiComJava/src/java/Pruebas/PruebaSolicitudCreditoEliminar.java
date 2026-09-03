/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.SolicitudCreditoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaSolicitudCreditoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SolicitudCreditoDAO miSolicitudDAO = new SolicitudCreditoDAO();
        
        System.out.print("Ingrese el ID de la solicitud de crédito que desea eliminar: ");
        int idSolicitud = sc.nextInt();
        
        boolean respuesta = miSolicitudDAO.eliminarSolicitudCredito(idSolicitud);
        
        if (respuesta) {
            System.out.println("La solicitud de crédito con ID " + idSolicitud + " fue eliminada exitosamente.");
        } else {
            System.out.println("No se pudo eliminar la solicitud de crédito (verifique si el ID existe).");
        }
    }
}