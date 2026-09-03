/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.PagoDAO;
import Modelo.Pagos;
import java.sql.Date;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaPagoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PagoDAO miPagoDAO = new PagoDAO();
        Pagos miPago = new Pagos();
        
        System.out.print("Ingrese el ID del pago que desea actualizar: ");
        miPago.setIdPagos(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de la factura: ");
        miPago.setIdFactura(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese el nuevo número de referencia del pago: ");
        miPago.setNumeroReferenciaPago(sc.nextLine());
        
        System.out.print("Ingrese el nuevo monto pagado: ");
        miPago.setMontoPagado(sc.nextFloat());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la nueva fecha de pago (YYYY-MM-DD): ");
        String fechaStr = sc.nextLine();
        miPago.setFechaPago(Date.valueOf(fechaStr));
        
        System.out.print("Ingrese el nuevo ID del tipo de pago: ");
        miPago.setIdTipoPago(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese las nuevas observaciones: ");
        miPago.setObservaciones(sc.nextLine());
        
        boolean respuesta = miPagoDAO.actualizarPago(miPago);
        
        if (respuesta) {
            System.out.println("Los datos del pago se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del pago.");
        }
    }
}