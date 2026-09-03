/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.DetalleFacturaDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaDetalleFacturaEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DetalleFacturaDAO miDetalleDAO = new DetalleFacturaDAO();
        
        System.out.print("Ingrese el ID del detalle de factura que desea eliminar: ");
        int idDetalleFactura = sc.nextInt();
        
        boolean respuesta = miDetalleDAO.eliminarDetalleFactura(idDetalleFactura);
        
        if (respuesta) {
            System.out.println("El detalle de factura con ID " + idDetalleFactura + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el detalle (verifique si el ID existe).");
        }
    }
}