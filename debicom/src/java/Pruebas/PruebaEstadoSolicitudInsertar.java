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
public class PruebaEstadoSolicitudInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadosSolicitud miEstado = new EstadosSolicitud();
        EstadoSolicitudDAO dao = new EstadoSolicitudDAO();
        
        System.out.print("Ingrese el nombre del nuevo estado de solicitud: ");
        miEstado.setNombreEstado(sc.nextLine());
        
        boolean resultado = dao.insertarEstadoSolicitud(miEstado);

        if (resultado) {
            System.out.println("Se registró el estado de solicitud correctamente");
        } else {
            System.out.println("No se pudo registrar el estado de solicitud");
        }
    }
}