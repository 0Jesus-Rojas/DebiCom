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
public class PruebaTipoMovimientoInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TiposMovimiento tipoMov = new TiposMovimiento();
        TipoMovimientoDAO dao = new TipoMovimientoDAO();
        
        System.out.print("Ingrese el nombre del tipo de movimiento: ");
        tipoMov.setNombreTipo(sc.nextLine());
        
        boolean resultado = dao.insertarTipoMovimiento(tipoMov);

        if (resultado) {
            System.out.println("Se registró el tipo de movimiento correctamente");
        } else {
            System.out.println("No se pudo registrar el tipo de movimiento");
        }
    }
}