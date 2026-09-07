/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.FacturaDAO;
import Modelo.Facturas;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaFacturaConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FacturaDAO miFacturaDAO = new FacturaDAO();
        
        System.out.print("Ingrese el id de la factura que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Facturas miFactura = miFacturaDAO.consultarFactura(busqueda);
        
        if (miFactura != null){
            System.out.println("Número de Factura: " + miFactura.getNumeroFactura());
            System.out.println("ID Solicitud: " + miFactura.getIdSolicitud());
            System.out.println("ID Cliente: " + miFactura.getIdCliente());
            System.out.println("ID Tienda: " + miFactura.getIdTienda());
            System.out.println("Subtotal: " + miFactura.getSubtotal());
            System.out.println("Impuestos: " + miFactura.getImpuestos());
            System.out.println("Total: " + miFactura.getTotal());
            System.out.println("Fecha de Emisión: " + miFactura.getFechaEmision());
            System.out.println("Estado de la Factura: " + miFactura.getIdEstadoFactura());
        }else{
            System.out.println("No se encontró la factura");
        }
    }
}