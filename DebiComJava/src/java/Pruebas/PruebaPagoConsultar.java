/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.PagoDAO;
import Modelo.Pagos;
import java.util.Scanner;
/**
 *
 * @author Jesus
 */
public class PruebaPagoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PagoDAO miPagoDAO = new PagoDAO();
        
        System.out.print("Ingrese el ID del pago que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Pagos miPago = miPagoDAO.consultarPago(busqueda);
        if (miPago != null){
            System.out.println("ID Factura: " + miPago.getIdFactura());
            System.out.println("Número de Referencia: " + miPago.getNumeroReferenciaPago());
            System.out.println("Monto Pagado: " + miPago.getMontoPagado());
            System.out.println("Fecha de Pago: " + miPago.getFechaPago());
            System.out.println("ID Tipo de Pago: " + miPago.getIdTipoPago());
            System.out.println("Observaciones: " + miPago.getObservaciones());
        } else {
            System.out.println("No se encontró el pago con ese ID.");
        }
    }
}