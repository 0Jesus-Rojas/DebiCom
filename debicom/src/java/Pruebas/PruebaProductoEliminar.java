/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.ProductoDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaProductoEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProductoDAO miProductoDAO = new ProductoDAO();
        
        System.out.print("Ingrese el ID del producto que desea eliminar: ");
        int idProducto = sc.nextInt();
        
        boolean respuesta = miProductoDAO.eliminarProducto(idProducto);
        
        if (respuesta) {
            System.out.println("El producto con ID " + idProducto + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el producto (verifique si el ID existe).");
        }
    }
}