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
public class PruebaSolicitudCreditoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SolicitudCreditoDAO miSolicitudDAO = new SolicitudCreditoDAO();
        SolicitudesCredito miSolicitud = new SolicitudesCredito();
        
        System.out.print("Ingrese el ID de la solicitud de crédito que desea actualizar: ");
        miSolicitud.setIdSolicitud(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del cliente: ");
        miSolicitud.setIdCliente(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de la tienda: ");
        miSolicitud.setIdTienda(sc.nextInt());
        
        System.out.print("Ingrese el nuevo monto total: ");
        miSolicitud.setMontoTotal(sc.nextFloat());
        
        System.out.print("Ingrese el nuevo saldo pendiente: ");
        miSolicitud.setSaldoPendiente(sc.nextFloat());
        
        System.out.print("Ingrese el nuevo ID del estado de la solicitud: ");
        miSolicitud.setIdEstadoSolicitud(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la nueva fecha de solicitud (YYYY-MM-DD): ");
        miSolicitud.setFechaSolicitud(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese la nueva fecha de aprobación (YYYY-MM-DD): ");
        miSolicitud.setFechaAprovacion(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese la nueva fecha de vencimiento (YYYY-MM-DD): ");
        miSolicitud.setFechaVencimiento(Date.valueOf(sc.nextLine()));
        
        System.out.print("Ingrese las nuevas observaciones: ");
        miSolicitud.setObservaciones(sc.nextLine());
        
        boolean respuesta = miSolicitudDAO.actualizarSolicitudCredito(miSolicitud);
        
        if (respuesta) {
            System.out.println("Los datos de la solicitud de crédito se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información de la solicitud de crédito.");
        }
    }
}