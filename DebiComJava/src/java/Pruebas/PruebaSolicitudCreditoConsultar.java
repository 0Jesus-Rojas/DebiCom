/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.SolicitudCreditoDAO;
import Modelo.SolicitudesCredito;
import java.util.Scanner;
/**
 *
 * @author Jesus
 */
public class PruebaSolicitudCreditoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SolicitudCreditoDAO miSolicitudDAO = new SolicitudCreditoDAO();
        
        System.out.print("Ingrese el ID de la solicitud de crédito que desea buscar: ");
        int busqueda = sc.nextInt();
        
        SolicitudesCredito miSolicitud = miSolicitudDAO.consultarSolicitudCredito(busqueda);
        if (miSolicitud != null){
            System.out.println("--- Datos de la Solicitud ---");
            System.out.println("ID Cliente: " + miSolicitud.getIdCliente());
            System.out.println("ID Tienda: " + miSolicitud.getIdTienda());
            System.out.println("Monto Total: " + miSolicitud.getMontoTotal());
            System.out.println("Saldo Pendiente: " + miSolicitud.getSaldoPendiente());
            System.out.println("ID Estado Solicitud: " + miSolicitud.getIdEstadoSolicitud());
            System.out.println("Fecha Solicitud: " + miSolicitud.getFechaSolicitud());
            System.out.println("Fecha Aprobación: " + miSolicitud.getFechaAprovacion());
            System.out.println("Fecha Vencimiento: " + miSolicitud.getFechaVencimiento());
            System.out.println("Observaciones: " + miSolicitud.getObservaciones());
        } else {
            System.out.println("No se encontró la solicitud de crédito.");
        }
    }
}