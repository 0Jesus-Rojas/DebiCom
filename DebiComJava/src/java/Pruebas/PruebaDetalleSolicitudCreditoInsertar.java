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
public class PruebaDetalleSolicitudCreditoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleSolicitudCredito miDetalle = new DetalleSolicitudCredito();
        DetalleSolicitudCreditoDAO dao = new DetalleSolicitudCreditoDAO();
        
        System.out.print("Ingrese el ID de la solicitud: ");
        miDetalle.setIdSolicitud(sc.nextInt());
        
        System.out.print("Ingrese el ID del producto: ");
        miDetalle.setIdProducto(sc.nextInt());
        
        System.out.print("Ingrese la cantidad: ");
        miDetalle.setCantidad(sc.nextInt());
        
        System.out.print("Ingrese el precio unitario: ");
        miDetalle.setPrecioUnitario(sc.nextFloat());
        
        System.out.print("Ingrese el subtotal: ");
        miDetalle.setSubtotal(sc.nextFloat());
        
        boolean resultado = dao.insertarDetalle(miDetalle);

        if (resultado) {
            System.out.println("Se registró el detalle correctamente");
        } else {
            System.out.println("No se pudo registrar el detalle");
        }
    }
}