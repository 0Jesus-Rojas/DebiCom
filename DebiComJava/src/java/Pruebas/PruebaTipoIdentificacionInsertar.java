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
public class PruebaTipoIdentificacionInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoIdentificaciones miTipo = new TipoIdentificaciones();
        TipoIdentificacionDAO dao = new TipoIdentificacionDAO();
        
        System.out.print("Ingrese el nombre del tipo de identificacion: ");
        miTipo.setNombreTipo(sc.nextLine());
        
        boolean resultado = dao.insertarTipoIdentificacion(miTipo);

        if (resultado) {
            System.out.println("Se registró el tipo de identificación correctamente");
        } else {
            System.out.println("No se pudo registrar el tipo de identificación");
        }
    }
}