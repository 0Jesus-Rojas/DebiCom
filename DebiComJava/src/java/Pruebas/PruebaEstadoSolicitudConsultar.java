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
public class PruebaEstadoSolicitudConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoSolicitudDAO miEstadoDAO = new EstadoSolicitudDAO();
        
        System.out.print("Ingrese el ID del estado de solicitud que desea buscar: ");
        int busqueda = sc.nextInt();
        
        EstadosSolicitud miEstado = miEstadoDAO.consultarEstadoSolicitud(busqueda);
        
        if (miEstado != null){
            System.out.println("ID Estado Solicitud: " + miEstado.getIdEstadoSolicitud());
            System.out.println("Nombre del estado: " + miEstado.getNombreEstado());
        } else {
            System.out.println("No se encontro el estado de solicitud");
        }
    }
}