/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;

import Controlador.EstadoFacturaDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaEstadoFacturaEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoFacturaDAO miEstadoDAO = new EstadoFacturaDAO();
        
        System.out.print("Ingrese el ID del estado de factura que desea eliminar: ");
        int idEstadoFactura = sc.nextInt();
        
        boolean respuesta = miEstadoDAO.eliminarEstadoFactura(idEstadoFactura);
        
        if (respuesta) {
            System.out.println("El estado de factura con ID " + idEstadoFactura + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el estado de factura (verifique si el ID existe o si está siendo usado en otras tablas).");
        }
    }
}