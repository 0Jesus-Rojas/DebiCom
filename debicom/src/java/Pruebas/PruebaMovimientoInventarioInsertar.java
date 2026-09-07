/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;

import Controlador.MovimientoInventarioDAO;
import Modelo.MovimientosInventario;
import java.util.Scanner;
import java.sql.Date;

/**
 *
 * @author Jesus
 */
public class PruebaMovimientoInventarioInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MovimientosInventario miMovimiento = new MovimientosInventario();
        MovimientoInventarioDAO dao = new MovimientoInventarioDAO();
        
        System.out.print("Ingrese el ID del producto: ");
        miMovimiento.setIdProducto(sc.nextInt());
        
        System.out.print("Ingrese el ID del tipo de movimiento: ");
        miMovimiento.setIdTipoMovimiento(sc.nextInt());
        
        System.out.print("Ingrese la cantidad: ");
        miMovimiento.setCantidad(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer
        
        System.out.print("Ingrese el motivo: ");
        miMovimiento.setMotivo(sc.nextLine());
        
        System.out.print("Ingrese la fecha (YYYY-MM-DD): ");
        String fechaInput = sc.nextLine();
        miMovimiento.setFechaMovimiento(Date.valueOf(fechaInput));
        
        boolean resultado = dao.insertarMovimiento(miMovimiento);

        if (resultado) {
            System.out.println("Se registró el movimiento correctamente");
        } else {
            System.out.println("No se pudo registrar el movimiento");
        }
    }
}