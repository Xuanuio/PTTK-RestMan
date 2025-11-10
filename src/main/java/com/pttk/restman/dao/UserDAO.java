package com.pttk.restman.dao;

import com.pttk.restman.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO extends DAO {

    public UserDAO() { super(); }

    public boolean loginCheck(User user) {
        boolean ok = false;
        try {
            String sql = "SELECT id FROM tblUser WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ResultSet rs = ps.executeQuery();
            ok = rs.next();
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }

    public int findUserIdByPhone(String phone) {
        try {
            String sql = "SELECT id FROM tblUser WHERE phoneNumber = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            int id = rs.next() ? rs.getInt(1) : -1;
            rs.close();
            ps.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}