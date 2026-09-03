/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Aprendiz
 */
public class Conexion {

    private Connection conn;
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String user = "root";
    private String password = "";
    private String basedatos = "debicom";
    private String url = "jdbc:mysql://localhost:3306/" + basedatos + "?useTimezone=true&serverTimezone=UTC";

    public Conexion() {
        conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);

            if (conn == null) {
                System.out.println("No se establecio la conexion" + url);
            } else {
                System.out.println("Conexion establecida con " + basedatos);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public Connection getconn() {
        return conn;
    }
};
