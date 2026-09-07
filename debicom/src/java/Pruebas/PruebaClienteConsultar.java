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
public class PruebaClienteConsultar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ClienteDAO miClienteDAO = new ClienteDAO();
        
        System.out.print("Ingrese el id del cliente que desea buscar: ");
        int busqueda = sc.nextInt();
        
        Cliente miCliente = miClienteDAO.consultarCliente(busqueda);
        if (miCliente != null){
            System.out.println("Credito actual: " + miCliente.getCreditoActual());
            System.out.println("Id usuario: " + miCliente.getIdUsuario());
        }else{
            System.out.println("No se encontro el cliente");
        }
    }
}
