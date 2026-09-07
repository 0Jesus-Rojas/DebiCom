/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;

import Controlador.EstadoFacturaDAO;
import Modelo.EstadosFactura;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaEstadoFacturaActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoFacturaDAO miEstadoDAO = new EstadoFacturaDAO();
        EstadosFactura miEstado = new EstadosFactura();
        
        System.out.print("Ingrese el ID del estado de factura que desea actualizar: ");
        miEstado.setIdEstadoFactura(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer después de leer un entero
        
        System.out.print("Ingrese el nuevo nombre del estado: ");
        miEstado.setNombreEstado(sc.nextLine());
        
        boolean respuesta = miEstadoDAO.actualizarEstadoFactura(miEstado);
        
        if (respuesta) {
            System.out.println("Los datos del estado de factura se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del estado de factura.");
        }
    }
}