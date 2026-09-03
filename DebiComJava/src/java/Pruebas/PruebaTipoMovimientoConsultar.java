/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.TipoMovimientoDAO;
import Modelo.TiposMovimiento;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaTipoMovimientoConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoMovimientoDAO dao = new TipoMovimientoDAO();
        
        System.out.print("Ingrese el ID del tipo de movimiento que desea buscar: ");
        int busqueda = sc.nextInt();
        
        TiposMovimiento tipoMov = dao.consultarTipoMovimiento(busqueda);
        if (tipoMov != null){
            System.out.println("Nombre del tipo: " + tipoMov.getNombreTipo());
        } else {
            System.out.println("No se encontró el tipo de movimiento");
        }
    }
}