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
public class PruebaMovimientoInventarioActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MovimientoInventarioDAO dao = new MovimientoInventarioDAO();
        MovimientosInventario miMovimiento = new MovimientosInventario();
        
        System.out.print("Ingrese el ID del movimiento que desea actualizar: ");
        miMovimiento.setIdMovimiento(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del producto: ");
        miMovimiento.setIdProducto(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del tipo de movimiento: ");
        miMovimiento.setIdTipoMovimiento(sc.nextInt());
        
        System.out.print("Ingrese la nueva cantidad: ");
        miMovimiento.setCantidad(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer
        
        System.out.print("Ingrese el nuevo motivo: ");
        miMovimiento.setMotivo(sc.nextLine());
        
        System.out.print("Ingrese la nueva fecha (YYYY-MM-DD): ");
        String fechaInput = sc.nextLine();
        miMovimiento.setFechaMovimiento(Date.valueOf(fechaInput));
        
        boolean respuesta = dao.actualizarMovimiento(miMovimiento);
        
        if (respuesta) {
            System.out.println("Los datos del movimiento se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del movimiento.");
        }
    }
}