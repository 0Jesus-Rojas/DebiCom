/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TiendaDAO;
import Modelo.Tiendas;
import java.sql.Date;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaTiendaActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TiendaDAO miTiendaDAO = new TiendaDAO();
        Tiendas miTienda = new Tiendas();
        
        System.out.print("Ingrese el ID de la tienda que desea actualizar: ");
        miTienda.setIdTienda(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese el nuevo nombre de la tienda: ");
        miTienda.setNombreTienda(sc.nextLine());
        
        System.out.print("Ingrese el nuevo NIT: ");
        miTienda.setNit(sc.nextLine());
        
        System.out.print("Ingrese la nueva dirección: ");
        miTienda.setDireccion(sc.nextLine());
        
        System.out.print("Ingrese el nuevo teléfono: ");
        miTienda.setTelefono(sc.nextLine());
        
        System.out.print("Ingrese el nuevo ID del vendedor: ");
        miTienda.setIdVendedor(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la nueva fecha de registro (Formato YYYY-MM-DD): ");
        String fechaStr = sc.nextLine();
        miTienda.setFechaRegistro(Date.valueOf(fechaStr));
        
        boolean respuesta = miTiendaDAO.actualizarTienda(miTienda);
        
        if (respuesta) {
            System.out.println("Los datos de la tienda se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información de la tienda.");
        }
    }
}