/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.FacturaDAO;
import Modelo.Facturas;
import java.util.Scanner;
import java.sql.Date;

/**
 *
 * @author Jesus
 */
public class PruebaFacturaInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Facturas miFactura = new Facturas();
        FacturaDAO dao = new FacturaDAO();
        
        System.out.print("Ingrese el número de la factura: ");
        miFactura.setNumeroFactura(sc.next());

        System.out.print("Ingrese el ID de la solicitud: ");
        miFactura.setIdSolicitud(sc.nextInt());

        System.out.print("Ingrese el ID del cliente: ");
        miFactura.setIdCliente(sc.nextInt());

        System.out.print("Ingrese el ID de la tienda: ");
        miFactura.setIdTienda(sc.nextInt());

        System.out.print("Ingrese el subtotal: ");
        miFactura.setSubtotal(sc.nextFloat());

        System.out.print("Ingrese los impuestos: ");
        miFactura.setImpuestos(sc.nextFloat());
        
        System.out.print("Ingrese el total: ");
        miFactura.setTotal(sc.nextFloat());

        System.out.print("Ingrese la fecha de emisión (YYYY-MM-DD): ");
        miFactura.setFechaEmision(Date.valueOf(sc.next()));
        
        System.out.print("Ingrese el ID del estado de la factura: ");
        miFactura.setIdEstadoFactura(sc.nextInt());
        
        boolean resultado = dao.insertarFactura(miFactura);

        if (resultado) {
            System.out.println("Se registró la factura correctamente");
        } else {
            System.out.println("No se pudo registrar la factura");
        }
    }
}