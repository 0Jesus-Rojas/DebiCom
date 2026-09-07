/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.EstadoProductoDAO;
import Modelo.EstadosProducto;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaEstadoProductoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadosProducto miEstado = new EstadosProducto();
        EstadoProductoDAO dao = new EstadoProductoDAO();
        
        System.out.print("Ingrese el nombre del nuevo estado de producto: ");
        miEstado.setNomrebEstadpo(sc.nextLine());
        
        // El ID no se suele pedir aquí porque normalmente es autoincrementable en la base de datos
        
        boolean resultado = dao.insertarEstadoProducto(miEstado);

        if (resultado) {
            System.out.println("Se registró el estado del producto correctamente");
        } else {
            System.out.println("No se pudo registrar el estado del producto");
        }
    }
}