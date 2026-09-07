/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.EstadoSolicitudDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaEstadoSolicitudEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoSolicitudDAO miEstadoDAO = new EstadoSolicitudDAO();
        
        System.out.print("Ingrese el ID del estado de solicitud que desea eliminar: ");
        int idEstado = sc.nextInt();
        
        boolean respuesta = miEstadoDAO.eliminarEstadoSolicitud(idEstado);
        
        if (respuesta) {
            System.out.println("El estado de solicitud con ID " + idEstado + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el estado de solicitud (verifique si el ID existe).");
        }
    }
}