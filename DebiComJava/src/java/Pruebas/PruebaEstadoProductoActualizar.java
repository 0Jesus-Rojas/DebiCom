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
public class PruebaEstadoProductoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoProductoDAO dao = new EstadoProductoDAO();
        EstadosProducto miEstado = new EstadosProducto();
        
        System.out.print("Ingrese el ID del estado de producto que desea actualizar: ");
        miEstado.setIdEstadoProducto(sc.nextInt());
        sc.nextLine(); // Limpiar el buffer
        
        System.out.print("Ingrese el nuevo nombre del estado de producto: ");
        miEstado.setNomrebEstadpo(sc.nextLine());
        
        boolean respuesta = dao.actualizarEstadoProducto(miEstado);
        
        if (respuesta) {
            System.out.println("Los datos del estado se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del estado de producto.");
        }
    }
}