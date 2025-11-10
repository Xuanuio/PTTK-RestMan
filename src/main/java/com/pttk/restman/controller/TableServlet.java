package com.pttk.restman.controller;

import com.pttk.restman.dao.TableDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

@WebServlet(name = "TableServlet", urlPatterns = {"/table/free"})
public class TableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String dateStr = req.getParameter("reservationDate");
        String timeSlot = req.getParameter("timeSlot");
        Date d = null;
        try { d = Date.valueOf(dateStr); } catch (Exception ignored) {}
        ArrayList<Integer> free = new ArrayList<>();
        if (d != null && timeSlot != null && !timeSlot.isEmpty()) {
            free = TableDAO.getEmptyTable(d, timeSlot);
        }
        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < free.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(free.get(i)).append("\"");
        }
        sb.append("]");
        resp.getWriter().write(sb.toString());
    }

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // đảm bảo load driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}