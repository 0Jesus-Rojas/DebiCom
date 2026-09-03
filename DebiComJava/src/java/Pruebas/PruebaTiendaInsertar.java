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
public class PruebaTiendaInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Tiendas miTienda = new Tiendas();
        TiendaDAO dao = new TiendaDAO();
        
        System.out.print("Ingrese el nombre de la tienda: ");
        miTienda.setNombreTienda(sc.nextLine());
        
        System.out.print("Ingrese el NIT: ");
        miTienda.setNit(sc.nextLine());
        
        System.out.print("Ingrese la dirección: ");
        miTienda.setDireccion(sc.nextLine());
        
        System.out.print("Ingrese el teléfono: ");
        miTienda.setTelefono(sc.nextLine());
        
        System.out.print("Ingrese el ID del vendedor: ");
        miTienda.setIdVendedor(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese la fecha de registro (Formato YYYY-MM-DD): ");
        String fechaStr = sc.nextLine();
        miTienda.setFechaRegistro(Date.valueOf(fechaStr)); // Convierte String a java.sql.Date
        
        boolean resultado = dao.insertarTienda(miTienda);

        if (resultado) {
            System.out.println("Se registró la tienda correctamente");
        } else {
            System.out.println("No se pudo registrar la tienda");
        }
    }
}