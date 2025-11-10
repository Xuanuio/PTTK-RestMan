package com.pttk.restman.dao;

import com.pttk.restman.model.Table;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TableReservationDAO extends DAO{
    public List<Table> getAvailableTablesFull(Date date, String slot) throws SQLException {
        String sql =
                "SELECT t.id, t.numberTable " +
                        "FROM tblTable t " +
                        "WHERE t.id NOT IN ( " +
                        "  SELECT d.tblTableid " +
                        "  FROM tblTableReservationDetail d " +
                        "  JOIN tblTableReservation r ON r.id = d.tblTableReservationid " +
                        "  WHERE r.reservationDate = ? AND r.timeSlot = ? " +
                        ") " +
                        "ORDER BY t.numberTable";
        List<Table> result = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, date);
            ps.setString(2, slot);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Table t = new Table();
                    t.setId(rs.getInt("id"));
                    t.setNumberTable(rs.getString("numberTable"));
                    result.add(t);
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<Table> getTablesByIds(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) in.append(",");
            in.append("?");
        }
        String sql = "SELECT id, numberTable FROM tblTable WHERE id IN (" + in + ") ORDER BY numberTable";
        List<Table> result = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) ps.setInt(i+1, ids.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Table t = new Table();
                    t.setId(rs.getInt("id"));
                    t.setNumberTable(rs.getString("numberTable"));
                    result.add(t);
                }
            }
        }
        return result;
    }

    public Integer createReservationForTablesReturnId(
            List<Integer> tableIds, int customerId, Date date, String slot,
            String customerName, String customerEmail, String customerPhone
    ) throws SQLException {
        if (tableIds == null || tableIds.isEmpty()) return null;

        String conflictSql =
                "SELECT COUNT(*) " +
                        "FROM tblTableReservationDetail d " +
                        "JOIN tblTableReservation r ON r.id = d.tblTableReservationid " +
                        "WHERE d.tblTableid = ? AND r.reservationDate = ? AND r.timeSlot = ?";

        String insertReservation =
                "INSERT INTO tblTableReservation(" +
                        "  timeSlot, reservationDate, isCheckedIn, bookingType, tblInvoiceid, tblCustomerid," +
                        "  customerName, customerEmail, customerPhone" +
                        ") VALUES (?, ?, 'NO', 'ONLINE', NULL, ?, ?, ?, ?)";

        String insertDetail =
                "INSERT INTO tblTableReservationDetail(tblTableReservationid, tblTableid) VALUES (?, ?)";

        boolean oldAutoCommit = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            try (PreparedStatement chk = con.prepareStatement(conflictSql)) {
                for (Integer tid : tableIds) {
                    chk.setInt(1, tid);
                    chk.setDate(2, date);
                    chk.setString(3, slot);
                    try (ResultSet rs = chk.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            con.rollback();
                            return null;
                        }
                    }
                }
            }

            int reservationId;
            try (PreparedStatement ins = con.prepareStatement(insertReservation, Statement.RETURN_GENERATED_KEYS)) {
                ins.setString(1, slot);            // ?1  timeSlot
                ins.setDate(2, date);              // ?2  reservationDate
                ins.setInt(3, customerId);         // ?3  tblCustomerid
                ins.setString(4, customerName);    // ?4  customerName   <-- thêm
                ins.setString(5, customerEmail);   // ?5  customerEmail  <-- thêm
                ins.setString(6, customerPhone);   // ?6  customerPhone  <-- thêm

                if (ins.executeUpdate() == 0) { con.rollback(); return null; }
                try (ResultSet gk = ins.getGeneratedKeys()) {
                    if (!gk.next()) { con.rollback(); return null; }
                    reservationId = gk.getInt(1);
                }
            }

            try (PreparedStatement insD = con.prepareStatement(insertDetail)) {
                for (Integer tid : tableIds) {
                    insD.setInt(1, reservationId);
                    insD.setInt(2, tid);
                    insD.addBatch();
                }
                insD.executeBatch();
            }

            con.commit();
            return reservationId;
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ignore) {}
            throw new RuntimeException(e);
        } finally {
            try { con.setAutoCommit(oldAutoCommit); } catch (Exception ignore) {}
        }
    }


    public Integer findOrCreateCustomer(String name, String phone, String email) throws SQLException {
        if (phone != null && !phone.isEmpty()) {
            Integer cidByPhone = findCustomerIdByPhone(phone);
            if (cidByPhone != null) return cidByPhone;
        }

        if (email != null && !email.isEmpty()) {
            Integer cidByEmail = findCustomerIdByEmail(email);
            if (cidByEmail != null) return cidByEmail;
        }

        int userId = insertUserMinimal(name, phone, email);
        return insertCustomerForUser(userId);
    }

    private Integer findCustomerIdByPhone(String phone) throws SQLException {
        String sql =
                "SELECT c.id " +
                        "FROM tblCustomer c JOIN tblUser u ON u.id = c.tblUserid " +
                        "WHERE u.phoneNumber = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

    private Integer findCustomerIdByEmail(String email) throws SQLException {
        String sql =
                "SELECT c.id " +
                        "FROM tblCustomer c JOIN tblUser u ON u.id = c.tblUserid " +
                        "WHERE u.email = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

    private int insertUserMinimal(String name, String phone, String email) throws SQLException {

        String sql =
                "INSERT INTO tblUser(name, username, password, dateOfBirth, email, address, phoneNumber, role, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String username = (phone != null && !phone.isEmpty()) ? ("u" + phone) :
                (email != null && !email.isEmpty()) ? email :
                        ("guest_" + UUID.randomUUID());
        String password = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Date dob = Date.valueOf(LocalDate.of(1970,1,1));
        String addr = "";
        String role = "CUSTOMER";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name != null ? name : "Guest");
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setDate(4, dob);
            ps.setString(5, email != null ? email : "");
            ps.setString(6, addr);
            ps.setString(7, phone != null ? phone : "");
            ps.setString(8, role);
            ps.setString(9, null);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                gk.next();
                return gk.getInt(1);
            }
        }
    }

    private Integer insertCustomerForUser(int userId) throws SQLException {
        String sql = "INSERT INTO tblCustomer(tblUserid) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                gk.next();
                return gk.getInt(1);
            }
        }
    }
}