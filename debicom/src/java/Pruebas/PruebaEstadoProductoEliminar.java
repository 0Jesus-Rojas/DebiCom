/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.EstadoProductoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaEstadoProductoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoProductoDAO dao = new EstadoProductoDAO();
        
        System.out.print("Ingrese el ID del estado de producto que desea eliminar: ");
        int idEstadoProducto = sc.nextInt();
        
        boolean respuesta = dao.eliminarEstadoProducto(idEstadoProducto);
        
        if (respuesta) {
            System.out.println("El estado con ID " + idEstadoProducto + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el estado del producto (verifique si el ID existe).");
        }
    }
}