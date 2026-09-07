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
public class PruebaEstadoFacturaConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoFacturaDAO miEstadoDAO = new EstadoFacturaDAO();
        
        System.out.print("Ingrese el ID del estado de factura que desea buscar: ");
        int busqueda = sc.nextInt();
        
        EstadosFactura miEstado = miEstadoDAO.consultarEstadoFactura(busqueda);
        
        if (miEstado != null){
            System.out.println("ID del Estado: " + miEstado.getIdEstadoFactura());
            System.out.println("Nombre del Estado: " + miEstado.getNombreEstado());
        } else {
            System.out.println("No se encontro el estado de factura");
        }
    }
}