/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TiendaDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaTiendaEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TiendaDAO miTiendaDAO = new TiendaDAO();
        
        System.out.print("Ingrese el ID de la tienda que desea eliminar: ");
        int idTienda = sc.nextInt();
        
        boolean respuesta = miTiendaDAO.eliminarTienda(idTienda);
        
        if (respuesta) {
            System.out.println("La tienda con ID " + idTienda + " fue eliminada exitosamente.");
        } else {
            System.out.println("No se pudo eliminar la tienda (verifique si el ID existe).");
        }
    }
}