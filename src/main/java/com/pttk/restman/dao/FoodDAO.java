package com.pttk.restman.dao;

import com.pttk.restman.dao.DAO;
import com.pttk.restman.model.Food;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.*;

public class FoodDAO extends DAO {
    public FoodDAO() {
        // TODO Auto-generated constructor stub
    }

    public static boolean saveNewFood(Food food) {
        boolean kq = false;
        String sqlSaveFood = "INSERT INTO tblfood(name, price, description, photo) VALUES(?,?,?,?)";
        try {
            if (con == null || con.isClosed()) {
                System.out.println("Connection is closed or null. Reconnecting...");
                new DAO();
            }
            CallableStatement cs = con.prepareCall(sqlSaveFood);
            cs.setString(1, food.getName());
            cs.setFloat(2, food.getPrice());
            cs.setString(3, food.getDescription());
            cs.setString(4, food.getPhoto());
            cs.execute();
            kq = true;
        } catch (Exception e) {
            e.printStackTrace();
            kq = false;
        }
        return kq;
    }
}