/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;
import Controlador.ClienteDAO;
import Modelo.Cliente;
import java.util.Scanner;

/**
 *
 * @author Jesus
 */
public class PruebaClienteInsertar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Cliente miCliente = new Cliente();
        ClienteDAO dao = new ClienteDAO();
        
        System.out.print("Ingrese el cupo actual: ");
        miCliente.setCreditoActual(sc.nextFloat());
        
        System.out.print("Ingrese el ID del usuario: ");
        miCliente.setIdUsuario(sc.nextInt());
        
        boolean resultado = dao.insertarCliente(miCliente);

        if (resultado) {
            System.out.println("Se registró el cliente correctamente");
        } else {
            System.out.println("No se pudo registrar el cliente");
        }
    }
}
