/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.SolicitudCreditoDAO;
import Modelo.SolicitudesCredito;
import java.sql.Date;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaSolicitudCreditoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SolicitudesCredito miSolicitud = new SolicitudesCredito();
        SolicitudCreditoDAO dao = new SolicitudCreditoDAO();
        
        System.out.print("Ingrese el ID del cliente: ");
        miSolicitud.setIdCliente(sc.nextInt());
        
        System.out.print("Ingrese el ID de la tienda: ");
        miSolicitud.setIdTienda(sc.nextInt());
        
        System.out.print("Ingrese el monto total: ");
        miSolicitud.setMontoTotal(sc.nextFloat());
        
        System.out.print("Ingrese el saldo pendiente: ");
        miSolicitud.setSaldoPendiente(sc.nextFloat());
        
        System.out.print("Ingrese el ID del estado de la solicitud: ");
        miSolicitud.setIdEstadoSolicitud(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la fecha de solicitud (YYYY-MM-DD): ");
        miSolicitud.setFechaSolicitud(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese la fecha de aprobación (YYYY-MM-DD): ");
        miSolicitud.setFechaAprovacion(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese la fecha de vencimiento (YYYY-MM-DD): ");
        miSolicitud.setFechaVencimiento(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese las observaciones: ");
        miSolicitud.setObservaciones(sc.nextLine());
        
        boolean resultado = dao.insertarSolicitudCredito(miSolicitud);

        if (resultado) {
            System.out.println("Se registró la solicitud de crédito correctamente.");
        } else {
            System.out.println("No se pudo registrar la solicitud de crédito.");
        }
    }
}