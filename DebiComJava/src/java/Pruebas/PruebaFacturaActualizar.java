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
public class PruebaFacturaActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FacturaDAO miFacturaDAO = new FacturaDAO();
        Facturas miFactura = new Facturas();
        
        System.out.print("Ingrese el ID de la factura que desea actualizar: ");
        miFactura.setIdFactura(sc.nextInt());
        
        System.out.print("Ingrese el nuevo número de la factura: ");
        miFactura.setNumeroFactura(sc.next());
        
        System.out.print("Ingrese el nuevo ID de la solicitud: ");
        miFactura.setIdSolicitud(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del cliente: ");
        miFactura.setIdCliente(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de la tienda: ");
        miFactura.setIdTienda(sc.nextInt());
        
        System.out.print("Ingrese el nuevo subtotal: ");
        miFactura.setSubtotal(sc.nextFloat());
        
        System.out.print("Ingrese los nuevos impuestos: ");
        miFactura.setImpuestos(sc.nextFloat());
        
        System.out.print("Ingrese el total: ");
        miFactura.setTotal(sc.nextFloat());
        
        System.out.print("Ingrese la nueva fecha de emisión (YYYY-MM-DD): ");
        miFactura.setFechaEmision(Date.valueOf(sc.next()));
        
        System.out.print("Ingrese el nuevo ID del estado de la factura: ");
        miFactura.setIdEstadoFactura(sc.nextInt());
        
        boolean respuesta = miFacturaDAO.actualizarFactura(miFactura);
        
        if (respuesta) {
            System.out.println("Los datos de la factura se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información de la factura.");
        }
    }
}