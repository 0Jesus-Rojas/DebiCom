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
public class PruebaPagoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Pagos miPago = new Pagos();
        PagoDAO dao = new PagoDAO();
        
        System.out.print("Ingrese el ID de la factura: ");
        miPago.setIdFactura(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese el número de referencia del pago: ");
        miPago.setNumeroReferenciaPago(sc.nextLine());
        
        System.out.print("Ingrese el monto pagado: ");
        miPago.setMontoPagado(sc.nextFloat());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la fecha de pago (YYYY-MM-DD): ");
        String fechaStr = sc.nextLine();
        miPago.setFechaPago(Date.valueOf(fechaStr));
        
        System.out.print("Ingrese el ID del tipo de pago: ");
        miPago.setIdTipoPago(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese las observaciones: ");
        miPago.setObservaciones(sc.nextLine());
        
        boolean resultado = dao.insertarPago(miPago);

        if (resultado) {
            System.out.println("Se registró el pago correctamente.");
        } else {
            System.out.println("No se pudo registrar el pago.");
        }
    }
}