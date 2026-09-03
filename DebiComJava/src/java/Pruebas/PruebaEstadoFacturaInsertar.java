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
public class PruebaEstadoFacturaInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadosFactura miEstado = new EstadosFactura();
        EstadoFacturaDAO dao = new EstadoFacturaDAO();
        
        System.out.print("Ingrese el nombre del nuevo estado de factura: ");
        miEstado.setNombreEstado(sc.nextLine());
        
        boolean resultado = dao.insertarEstadoFactura(miEstado);

        if (resultado) {
            System.out.println("Se registró el estado de factura correctamente");
        } else {
            System.out.println("No se pudo registrar el estado de factura");
        }
    }
}