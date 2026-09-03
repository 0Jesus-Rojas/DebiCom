/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pruebas;

import Controlador.Conexion;
import java.sql.Connection;

/**
 *
 * @author Aprendiz
 */
public class PruebaConexion {
    public static void main(String[] args) {
        Conexion Conex = new Conexion();
        Connection con = Conex.getconn();
    }
    
}
