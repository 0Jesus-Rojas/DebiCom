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
public class PruebaTipoMovimientoActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoMovimientoDAO dao = new TipoMovimientoDAO();
        TiposMovimiento tipoMov = new TiposMovimiento();
        
        System.out.print("Ingrese el ID del tipo de movimiento que desea actualizar: ");
        tipoMov.setIdTipoMovimiento(sc.nextInt());
        sc.nextLine();
        System.out.print("Ingrese el nuevo nombre del tipo de movimiento: ");
        tipoMov.setNombreTipo(sc.nextLine());
        
        boolean respuesta = dao.actualizarTipoMovimiento(tipoMov);
        
        if (respuesta) {
            System.out.println("Los datos del tipo de movimiento se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del tipo de movimiento.");
        }
    }
}