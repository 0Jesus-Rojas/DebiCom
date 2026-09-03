/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.EstadoSolicitudDAO;
import Modelo.EstadosSolicitud;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaEstadoSolicitudActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoSolicitudDAO miEstadoDAO = new EstadoSolicitudDAO();
        EstadosSolicitud miEstado = new EstadosSolicitud();
        
        System.out.print("Ingrese el ID del estado de solicitud que desea actualizar: ");
        miEstado.setIdEstadoSolicitud(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer del scanner después de leer un int
        
        System.out.print("Ingrese el nuevo nombre del estado: ");
        miEstado.setNombreEstado(sc.nextLine());
        
        boolean respuesta = miEstadoDAO.actualizarEstadoSolicitud(miEstado);
        
        if (respuesta) {
            System.out.println("Los datos del estado de solicitud se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del estado de solicitud.");
        }
    }
}