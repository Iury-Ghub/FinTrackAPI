package org.example;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String DEFAULT_URL = "jdbc:sqlite:fintrack.db";

    public static Connection getConexao(){
        String url = System.getProperty("JDBC_URL");
        if (url == null || url.isBlank()) {
            url = System.getenv("JDBC_URL");
        }
        if (url == null || url.isBlank()) {
            url = DEFAULT_URL;
        }

        try{
            return DriverManager.getConnection(url);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
