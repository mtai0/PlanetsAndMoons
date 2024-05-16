package com.revature.utilities;

import com.revature.MainDriver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    
    public static Connection createConnection() throws SQLException{
        InputStream props= MainDriver.class.getClassLoader().getResourceAsStream("database.properties");
        Properties properties=new Properties();
        try{
            properties.load(props);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean useTest = Boolean.parseBoolean(properties.getProperty("test-mode"));
        if (useTest) {
            return DriverManager.getConnection("jdbc:sqlite:src/main/resources/planetariumForTest.db");
        }
        else {
            return DriverManager.getConnection("jdbc:sqlite:src/main/resources/planetarium.db");
        }
    }

    public static void main(String[] args) {
        // run this to create database
        try {
            ConnectionUtil.createConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
