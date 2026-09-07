/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TipoMovimientoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaTipoMovimientoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoMovimientoDAO dao = new TipoMovimientoDAO();
        
        System.out.print("Ingrese el ID del tipo de movimiento que desea eliminar: ");
        int idTipoMovimiento = sc.nextInt();
        
        boolean respuesta = dao.eliminarTipoMovimiento(idTipoMovimiento);
        
        if (respuesta) {
            System.out.println("El tipo de movimiento con ID " + idTipoMovimiento + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el tipo de movimiento (verifique si el ID existe).");
        }
    }
}