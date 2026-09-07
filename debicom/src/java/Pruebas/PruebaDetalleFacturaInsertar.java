/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.DetalleFacturaDAO;
import Modelo.DetalleFacturas;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaDetalleFacturaInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleFacturas miDetalle = new DetalleFacturas();
        DetalleFacturaDAO dao = new DetalleFacturaDAO();
        
        System.out.print("Ingrese el ID de la factura: ");
        miDetalle.setIdFactura(sc.nextInt());
        
        System.out.print("Ingrese el ID del producto: ");
        miDetalle.setIdProducto(sc.nextInt());
        
        System.out.print("Ingrese la cantidad: ");
        miDetalle.setCantidad(sc.nextInt());
        
        System.out.print("Ingrese el precio unitario: ");
        miDetalle.setPrecioUnitarrio(sc.nextFloat());
        
        System.out.print("Ingrese el subtotal: ");
        miDetalle.setSubtotal(sc.nextFloat());
        
        boolean resultado = dao.insertarDetalleFactura(miDetalle);

        if (resultado) {
            System.out.println("Se registró el detalle de factura correctamente");
        } else {
            System.out.println("No se pudo registrar el detalle de factura");
        }
    }
}