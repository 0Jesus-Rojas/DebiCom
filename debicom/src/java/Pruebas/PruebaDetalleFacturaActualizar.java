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
public class PruebaDetalleFacturaActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleFacturaDAO miDetalleDAO = new DetalleFacturaDAO();
        DetalleFacturas miDetalle = new DetalleFacturas();
        
        System.out.print("Ingrese el ID del detalle de factura que desea actualizar: ");
        miDetalle.setIdDetalleFactura(sc.nextInt());
        sc.nextLine(); // Limpieza del buffer
        
        System.out.print("Ingrese el nuevo ID de la factura: ");
        miDetalle.setIdFactura(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese el nuevo ID del producto: ");
        miDetalle.setIdProducto(sc.nextInt());
        sc.nextLine();
        
        System.out.print("Ingrese la nueva cantidad: ");
        miDetalle.setCantidad(sc.nextInt());
        
        System.out.print("Ingrese el nuevo precio unitario: ");
        miDetalle.setPrecioUnitarrio(sc.nextFloat());
        
        System.out.print("Ingrese el nuevo subtotal: ");
        miDetalle.setSubtotal(sc.nextFloat());
        
        boolean respuesta = miDetalleDAO.actualizarDetalleFactura(miDetalle);
        
        if (respuesta) {
            System.out.println("Los datos del detalle se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del detalle de factura.");
        }
    }
}