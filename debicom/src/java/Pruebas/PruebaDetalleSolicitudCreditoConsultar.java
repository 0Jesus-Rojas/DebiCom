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
public class PruebaDetalleSolicitudCreditoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleSolicitudCreditoDAO miDetalleDAO = new DetalleSolicitudCreditoDAO();
        
        System.out.print("Ingrese el id del detalle de solicitud que desea buscar: ");
        int busqueda = sc.nextInt();
        
        DetalleSolicitudCredito miDetalle = miDetalleDAO.consultarDetalle(busqueda);
        if (miDetalle != null){
            System.out.println("ID Solicitud: " + miDetalle.getIdSolicitud());
            System.out.println("ID Producto: " + miDetalle.getIdProducto());
            System.out.println("Cantidad: " + miDetalle.getCantidad());
            System.out.println("Precio Unitario: " + miDetalle.getPrecioUnitario());
            System.out.println("Subtotal: " + miDetalle.getSubtotal());
        }else{
            System.out.println("No se encontro el detalle de solicitud");
        }
    }
}