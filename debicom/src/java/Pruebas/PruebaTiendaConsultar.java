/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TiendaDAO;
import Modelo.Tiendas;
import java.util.Scanner;
/**
 *
 * @author Jesus
 */
public class PruebaTiendaConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TiendaDAO miTiendaDAO = new TiendaDAO();
        
        System.out.print("Ingrese el ID de la tienda que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Tiendas miTienda = miTiendaDAO.consultarTienda(busqueda);
        if (miTienda != null){
            System.out.println("--- Datos de la Tienda ---");
            System.out.println("Nombre: " + miTienda.getNombreTienda());
            System.out.println("NIT: " + miTienda.getNit());
            System.out.println("Dirección: " + miTienda.getDireccion());
            System.out.println("Teléfono: " + miTienda.getTelefono());
            System.out.println("ID Vendedor: " + miTienda.getIdVendedor());
            System.out.println("Fecha de Registro: " + miTienda.getFechaRegistro());
        } else {
            System.out.println("No se encontró la tienda");
        }
    }
}