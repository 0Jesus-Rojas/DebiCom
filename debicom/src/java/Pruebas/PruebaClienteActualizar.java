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
public class PruebaClienteActualizar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ClienteDAO miClienteDAO = new ClienteDAO();
        Cliente miCliente = new Cliente();
        
        System.out.print("Ingrese el ID del cliente que desea actualizar: ");
        miCliente.setIdCliente(sc.nextInt());
        sc.nextLine();
        System.out.print("Ingrese el nuevo credito del cliente: ");
        miCliente.setCreditoActual(sc.nextInt());
        sc.nextLine();
        System.out.print("Ingrese el nuevo id del cliente: ");
        miCliente.setIdUsuario(sc.nextInt());
        
        boolean respuesta = miClienteDAO.actualizarCliente(miCliente);
        
        if (respuesta) {
            System.out.println("Los datos del cliente se actualizaron correctamente.");
        } else {
            System.out.println("No se pudo actualizar la información del cliente.");
        }
    }
}
