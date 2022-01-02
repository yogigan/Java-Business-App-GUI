package com.myapp.helper;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    public static Connection con;
    public static Statement stm;
    private String url = "jdbc:mysql://localhost/jklaptop";
    private String user = "root";
    private String pass = "";
    private String driver = "com.mysql.cj.jdbc.Driver";

    //TODO connection to database with JDBC
    public Connection connect() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();
            return con;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to connect Database \n Error @ :" + e.getMessage());
            return null;
        }
    }

}
