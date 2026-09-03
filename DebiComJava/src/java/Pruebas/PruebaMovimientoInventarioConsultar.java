/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;

import Controlador.MovimientoInventarioDAO;
import Modelo.MovimientosInventario;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaMovimientoInventarioConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MovimientoInventarioDAO dao = new MovimientoInventarioDAO();
        
        System.out.print("Ingrese el ID del movimiento que desea buscar: ");
        int busqueda = sc.nextInt();
        
        MovimientosInventario miMovimiento = dao.consultarMovimiento(busqueda);
        
        if (miMovimiento != null) {
            System.out.println("ID Producto: " + miMovimiento.getIdProducto());
            System.out.println("ID Tipo Movimiento: " + miMovimiento.getIdTipoMovimiento());
            System.out.println("Cantidad: " + miMovimiento.getCantidad());
            System.out.println("Motivo: " + miMovimiento.getMotivo());
            System.out.println("Fecha Movimiento: " + miMovimiento.getFechaMovimiento());
        } else {
            System.out.println("No se encontró el movimiento");
        }
    }
}