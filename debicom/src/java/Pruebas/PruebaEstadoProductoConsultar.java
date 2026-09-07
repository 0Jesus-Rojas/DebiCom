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
public class PruebaEstadoProductoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EstadoProductoDAO dao = new EstadoProductoDAO();
        
        System.out.print("Ingrese el ID del estado de producto que desea buscar: ");
        int busqueda = sc.nextInt();
        
        EstadosProducto miEstado = dao.consultarEstadoProducto(busqueda);
        if (miEstado != null){
            System.out.println("ID Estado Producto: " + miEstado.getIdEstadoProducto());
            System.out.println("Nombre Estado: " + miEstado.getNomrebEstadpo());
        }else{
            System.out.println("No se encontro el estado del producto");
        }
    }
}