package com.pttk.restman.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    protected static Connection con;

    public DAO() {
        try {
            if (con == null || con.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/restman?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false";
                String user = "root";
                String pass = "12345";
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection(url, user, pass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}