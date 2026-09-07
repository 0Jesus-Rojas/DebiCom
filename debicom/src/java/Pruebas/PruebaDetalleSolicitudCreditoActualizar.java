/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.DetalleSolicitudCreditoDAO;
import Modelo.DetalleSolicitudCredito;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaDetalleSolicitudCreditoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleSolicitudCreditoDAO miDetalleDAO = new DetalleSolicitudCreditoDAO();
        DetalleSolicitudCredito miDetalle = new DetalleSolicitudCredito();
        
        System.out.print("Ingrese el ID del detalle que desea actualizar: ");
        miDetalle.setIdDetalleSolicitud(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de solicitud: ");
        miDetalle.setIdSolicitud(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de producto: ");
        miDetalle.setIdProducto(sc.nextInt());
        
        System.out.print("Ingrese la nueva cantidad: ");
        miDetalle.setCantidad(sc.nextInt());
        
        System.out.print("Ingrese el nuevo precio unitario (ej. 15.5): ");
        miDetalle.setPrecioUnitario(sc.nextFloat());
        
        System.out.print("Ingrese el nuevo subtotal (ej. 31.0): ");
        miDetalle.setSubtotal(sc.nextFloat());
        
        boolean respuesta = miDetalleDAO.actualizarDetalle(miDetalle);
        
        if (respuesta) {
            System.out.println("Los datos del detalle se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del detalle.");
        }
    }
}