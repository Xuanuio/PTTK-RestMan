package com.pttk.restman.dao;

import com.pttk.restman.model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomerDAO extends DAO {

    public static int saveCustomer(Customer customer) {
        int customerID = -1;
        try {
            String findUser = "SELECT id FROM tblUser WHERE phoneNumber = ?";
            PreparedStatement ps = con.prepareStatement(findUser);
            ps.setString(1, customer.getPhoneNumber());
            ResultSet rs = ps.executeQuery();
            Integer userId = null;
            if (rs.next()) userId = rs.getInt(1);
            rs.close();
            ps.close();

            if (userId == null) {
                String insUser =
                        "INSERT INTO tblUser(name, username, password, dateOfBirth, email, address, phoneNumber, role, note) " +
                                "VALUES(?,?,?,?,?,?,?,?,?)";
                PreparedStatement iu = con.prepareStatement(insUser, Statement.RETURN_GENERATED_KEYS);
                iu.setString(1, customer.getName());
                iu.setString(2, customer.getUserName());
                iu.setString(3, customer.getPassword());
                iu.setDate(4, java.sql.Date.valueOf(customer.getDateOfBirth())); // giả định model là LocalDate
                iu.setString(5, customer.getEmail());
                iu.setString(6, customer.getAddress());
                iu.setString(7, customer.getPhoneNumber());
                iu.setString(8, "CUSTOMER");
                iu.setString(9, customer.getNote() == null ? "" : customer.getNote());
                iu.executeUpdate();
                ResultSet k = iu.getGeneratedKeys();
                if (k.next()) userId = k.getInt(1);
                k.close();
                iu.close();
            }

            if (userId == null) return -1;

            String findCus = "SELECT id FROM tblCustomer WHERE tblUserid = ?";
            PreparedStatement ps2 = con.prepareStatement(findCus);
            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                customerID = rs2.getInt(1);
            } else {
                String insCus = "INSERT INTO tblCustomer(tblUserid) VALUES(?)";
                PreparedStatement ic = con.prepareStatement(insCus, Statement.RETURN_GENERATED_KEYS);
                ic.setInt(1, userId);
                ic.executeUpdate();
                ResultSet k2 = ic.getGeneratedKeys();
                if (k2.next()) customerID = k2.getInt(1);
                k2.close();
                ic.close();
            }
            rs2.close();
            ps2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerID;
    }
}