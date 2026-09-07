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
public class PruebaProductoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProductoDAO miProductoDAO = new ProductoDAO();
        
        System.out.print("Ingrese el ID del producto que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Productos miProducto = miProductoDAO.consultarProducto(busqueda);
        if (miProducto != null){
            System.out.println("--- DATOS DEL PRODUCTO ---");
            System.out.println("Nombre: " + miProducto.getNombre());
            System.out.println("Descripcion: " + miProducto.getDescripcion());
            System.out.println("Precio Unitario: " + miProducto.getPrecioUnitario());
            System.out.println("Stock: " + miProducto.getStock());
            System.out.println("Id Tienda: " + miProducto.getIdTienda());
            System.out.println("Id Unidad: " + miProducto.getIdUnidad());
            System.out.println("Id Estado Producto: " + miProducto.getIdEstadoProducto());
        } else {
            System.out.println("No se encontro el producto");
        }
    }
}