/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;

import Controlador.MovimientoInventarioDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaMovimientoInventarioEliminar1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MovimientoInventarioDAO dao = new MovimientoInventarioDAO();
        
        System.out.print("Ingrese el ID del movimiento que desea eliminar: ");
        int idMovimiento = sc.nextInt();
        
        boolean respuesta = dao.eliminarMovimiento(idMovimiento);
        
        if (respuesta) {
            System.out.println("El movimiento con ID " + idMovimiento + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el movimiento (verifique si el ID existe).");
        }
    }
}