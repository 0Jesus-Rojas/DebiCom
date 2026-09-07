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
public class PruebaTipoIdentificacionConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoIdentificacionDAO miDao = new TipoIdentificacionDAO();
        
        System.out.print("Ingrese el ID del tipo de identificacion que desea buscar: ");
        int busqueda = sc.nextInt();
        
        TipoIdentificaciones miTipo = miDao.consultarTipoIdentificacion(busqueda);
        if (miTipo != null){
            System.out.println("ID Tipo Identificacion: " + miTipo.getIdTipoIdentificacion());
            System.out.println("Nombre del tipo: " + miTipo.getNombreTipo());
        }else{
            System.out.println("No se encontro el tipo de identificacion");
        }
    }
}