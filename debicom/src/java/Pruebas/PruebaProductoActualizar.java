/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.ProductoDAO;
import Modelo.Productos;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaProductoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProductoDAO miProductoDAO = new ProductoDAO();
        Productos miProducto = new Productos();
        
        System.out.print("Ingrese el ID del producto que desea actualizar: ");
        miProducto.setIdProducto(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese el nuevo nombre: ");
        miProducto.setNombre(sc.nextLine());
        
        System.out.print("Ingrese la nueva descripcion: ");
        miProducto.setDescripcion(sc.nextLine());
        
        System.out.print("Ingrese el nuevo precio unitario: ");
        miProducto.setPrecioUnitario(sc.nextFloat());
        
        System.out.print("Ingrese el nuevo stock: ");
        miProducto.setStock(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de la tienda: ");
        miProducto.setIdTienda(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID de la unidad: ");
        miProducto.setIdUnidad(sc.nextInt());
        
        System.out.print("Ingrese el nuevo ID del estado del producto: ");
        miProducto.setIdEstadoProducto(sc.nextInt());
        
        boolean respuesta = miProductoDAO.actualizarProducto(miProducto);
        
        if (respuesta) {
            System.out.println("Los datos del producto se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del producto.");
        }
    }
}