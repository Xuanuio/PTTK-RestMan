package com.pttk.restman.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TableDAO extends DAO {

    public static ArrayList<Integer> getEmptyTable(java.sql.Date newDate, String timeSlot) {
        ArrayList<Integer> free = new ArrayList<>();
        try {
            String sql =
                    "SELECT t.numberTable " +
                            "FROM tblTable t " +
                            "LEFT JOIN tblTableReservationDetail rd ON rd.tblTableid = t.id " +
                            "LEFT JOIN tblTableReservation r ON r.id = rd.tblTableReservationid " +
                            "AND r.reservationDate = ? AND r.timeSlot = ? " +
                            "WHERE r.id IS NULL " +
                            "ORDER BY t.numberTable";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, newDate);
            ps.setString(2, timeSlot);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                free.add(rs.getInt(1));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return free;
    }
}