/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TipoIdentificacionDAO;
import Modelo.TipoIdentificaciones;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaTipoIdentificacionActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoIdentificacionDAO miDao = new TipoIdentificacionDAO();
        TipoIdentificaciones miTipo = new TipoIdentificaciones();
        
        System.out.print("Ingrese el ID del tipo de identificacion que desea actualizar: ");
        miTipo.setIdTipoIdentificacion(sc.nextInt());
        sc.nextLine(); // Limpiar buffer
        
        System.out.print("Ingrese el nuevo nombre del tipo de identificacion: ");
        miTipo.setNombreTipo(sc.nextLine());
        
        boolean respuesta = miDao.actualizarTipoIdentificacion(miTipo);
        
        if (respuesta) {
            System.out.println("Los datos del tipo de identificacion se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del tipo de identificacion.");
        }
    }
}