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
public class PruebaProductoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Productos miProducto = new Productos();
        ProductoDAO dao = new ProductoDAO();
        
        System.out.print("Ingrese el nombre del producto: ");
        miProducto.setNombre(sc.nextLine());
        
        System.out.print("Ingrese la descripcion del producto: ");
        miProducto.setDescripcion(sc.nextLine());
        
        System.out.print("Ingrese el precio unitario: ");
        miProducto.setPrecioUnitario(sc.nextFloat());
        
        System.out.print("Ingrese el stock: ");
        miProducto.setStock(sc.nextInt());
        
        System.out.print("Ingrese el ID de la tienda: ");
        miProducto.setIdTienda(sc.nextInt());
        
        System.out.print("Ingrese el ID de la unidad: ");
        miProducto.setIdUnidad(sc.nextInt());
        
        System.out.print("Ingrese el ID del estado del producto: ");
        miProducto.setIdEstadoProducto(sc.nextInt());
        
        boolean resultado = dao.insertarProducto(miProducto);

        if (resultado) {
            System.out.println("Se registró el producto correctamente");
        } else {
            System.out.println("No se pudo registrar el producto");
        }
    }
}