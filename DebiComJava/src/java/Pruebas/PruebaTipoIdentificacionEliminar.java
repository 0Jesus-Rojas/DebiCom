/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TipoIdentificacionDAO;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaTipoIdentificacionEliminar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoIdentificacionDAO miDao = new TipoIdentificacionDAO();
        
        System.out.print("Ingrese el ID del tipo de identificacion que desea eliminar: ");
        int idTipo = sc.nextInt();
        
        boolean respuesta = miDao.eliminarTipoIdentificacion(idTipo);
        
        if (respuesta) {
            System.out.println("El tipo de identificacion con ID " + idTipo + " fue eliminado exitosamente.");
        } else {
            System.out.println("No se pudo eliminar el tipo de identificacion (verifique si el ID existe).");
        }
    }
}