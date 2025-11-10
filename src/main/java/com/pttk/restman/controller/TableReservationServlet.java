package com.pttk.restman.controller;

import com.pttk.restman.dao.TableReservationDAO;
import com.pttk.restman.model.Table;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

@WebServlet(name = "TableReservationServlet", urlPatterns = {"/TableReservationServlet"})
public class TableReservationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final TableReservationDAO dao = new TableReservationDAO();

    private static final Set<String> ALLOWED_SLOTS = new HashSet<>(Arrays.asList(
            "07:00-09:00","09:00-11:00","11:00-13:00","13:00-15:00","17:00-19:00","19:00-21:00"
    ));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String dateStr = request.getParameter("date");
        String slot = request.getParameter("slot");

        if (dateStr == null || dateStr.isEmpty()) {
            String sessDate = (String) session.getAttribute("tempDate");
            if (sessDate != null) dateStr = sessDate;
        }
        if (slot == null || slot.isEmpty()) {
            String sessSlot = (String) session.getAttribute("tempSlot");
            if (sessSlot != null) slot = sessSlot;
        }

        if (dateStr != null && slot != null && !dateStr.isEmpty() && !slot.isEmpty()) {
            if (!ALLOWED_SLOTS.contains(slot)) {
                request.setAttribute("flash_error", "Khung giờ không hợp lệ.");
            } else {
                try {
                    Date date = Date.valueOf(dateStr);
                    List<Table> available = dao.getAvailableTablesFull(date, slot);
                    request.setAttribute("availableTables", available);
                    request.setAttribute("selectedDate", dateStr);
                    request.setAttribute("selectedSlot", slot);
                } catch (IllegalArgumentException e) {
                    request.setAttribute("flash_error", "Ngày không hợp lệ.");
                } catch (Exception e) {
                    request.setAttribute("flash_error", "Lỗi tải bàn trống: " + e.getMessage());
                }
            }
        }

        @SuppressWarnings("unchecked")
        Set<Integer> tempIds = (Set<Integer>) session.getAttribute("tempSelectedTableIds");
        if (tempIds == null) tempIds = new LinkedHashSet<>();
        try {
            List<Table> tempTables = dao.getTablesByIds(new ArrayList<>(tempIds));
            request.setAttribute("tempSelectedTables", tempTables);
        } catch (Exception e) {
            request.setAttribute("flash_error", "Lỗi tải danh sách tạm: " + e.getMessage());
        }

        request.getRequestDispatcher("/customer/OrderView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "";

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Set<Integer> tempIds = (Set<Integer>) session.getAttribute("tempSelectedTableIds");
        if (tempIds == null) {
            tempIds = new LinkedHashSet<>();
            session.setAttribute("tempSelectedTableIds", tempIds);
        }

        String reqDate = request.getParameter("date");
        String reqSlot = request.getParameter("slot");
        if (reqDate != null && !reqDate.isEmpty()) session.setAttribute("tempDate", reqDate);
        if (reqSlot != null && !reqSlot.isEmpty()) session.setAttribute("tempSlot", reqSlot);

        switch (action) {
            case "addTemp": {
                String tableIdStr = request.getParameter("tableId");
                try {
                    int tableId = Integer.parseInt(tableIdStr);
                    tempIds.add(tableId);
                } catch (Exception e) {
                    session.setAttribute("flash_error_once", "Không thể thêm bàn tạm: " + e.getMessage());
                }
                String d = (String) session.getAttribute("tempDate");
                String s = (String) session.getAttribute("tempSlot");
                response.sendRedirect(request.getContextPath() + "/TableReservationServlet?date=" + (d==null?"":d) + "&slot=" + (s==null?"":s));
                return;
            }
            case "removeTemp": {
                String tableIdStr = request.getParameter("tableId");
                try {
                    int tableId = Integer.parseInt(tableIdStr);
                    tempIds.remove(tableId);
                } catch (Exception e) {
                    session.setAttribute("flash_error_once", "Không thể bỏ chọn: " + e.getMessage());
                }
                String d = (String) session.getAttribute("tempDate");
                String s = (String) session.getAttribute("tempSlot");
                response.sendRedirect(request.getContextPath() + "/TableReservationServlet?date=" + (d==null?"":d) + "&slot=" + (s==null?"":s));
                return;
            }
            case "clearTemp": {
                tempIds.clear();
                String d = (String) session.getAttribute("tempDate");
                String s = (String) session.getAttribute("tempSlot");
                response.sendRedirect(request.getContextPath() + "/TableReservationServlet?date=" + (d==null?"":d) + "&slot=" + (s==null?"":s));
                return;
            }
            case "commitReservation": {
                String displayName = request.getParameter("customerName");
                String displayPhone = request.getParameter("customerPhone");
                String displayEmail = request.getParameter("customerEmail");
                Integer customerId = null;
                Object cid = session.getAttribute("customerId");
                if (cid instanceof Integer) customerId = (Integer) cid;
                else {
                    Object customerObj = session.getAttribute("customer");
                    if (customerObj != null) {
                        try { customerId = (Integer) customerObj.getClass().getMethod("getId").invoke(customerObj); }
                        catch (Exception e) { try { customerId = (Integer) customerObj.getClass().getMethod("getCustomerID").invoke(customerObj);} catch (Exception ignored) {} }
                    }
                }

                if (customerId == null) {
                    String name = request.getParameter("customerName");
                    String phone = request.getParameter("customerPhone");
                    String email = request.getParameter("customerEmail");
                    if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
                        request.setAttribute("flash_error", "Vui lòng nhập Họ tên và Số điện thoại.");
                        doGet(request, response);
                        return;
                    }
                    try {
                        customerId = dao.findOrCreateCustomer(name.trim(), phone.trim(), (email==null?null:email.trim()));
                        if (customerId != null) {
                            session.setAttribute("customerId", customerId);
                        } else {
                            request.setAttribute("flash_error", "Không thể tạo khách hàng.");
                            doGet(request, response);
                            return;
                        }
                    } catch (Exception e) {
                        request.setAttribute("flash_error", "Lỗi tạo khách hàng: " + e.getMessage());
                        doGet(request, response);
                        return;
                    }
                }

                String dateStr = (reqDate != null && !reqDate.isEmpty()) ? reqDate : (String) session.getAttribute("tempDate");
                String slot = (reqSlot != null && !reqSlot.isEmpty()) ? reqSlot : (String) session.getAttribute("tempSlot");
                if (dateStr == null || slot == null || !ALLOWED_SLOTS.contains(slot)) {
                    request.setAttribute("flash_error", "Ngày/khung giờ không hợp lệ.");
                    doGet(request, response);
                    return;
                }
                if (tempIds.isEmpty()) {
                    request.setAttribute("flash_error", "Chưa chọn bàn nào.");
                    doGet(request, response);
                    return;
                }

                try {
                    Date date = Date.valueOf(dateStr);
                    Integer newResId = dao.createReservationForTablesReturnId(
                            new ArrayList<>(tempIds), customerId, date, slot,
                            (displayName!=null?displayName.trim():""),
                            (displayEmail!=null?displayEmail.trim():""),
                            (displayPhone!=null?displayPhone.trim():"")
                    );

                    if (newResId != null) {
                        session.setAttribute("currentReservationId", newResId);
                        List<Table> committed = dao.getTablesByIds(new ArrayList<>(tempIds));
                        tempIds.clear();
                        String list = committed.stream()
                                .map(Table::getNumberTable)
                                .reduce((a,b) -> a + ", " + b)
                                .orElse("(trống)");

                        request.setAttribute(
                                "flash_success",
                                "Đặt bàn thành công (#" + newResId + ") cho " + dateStr + " - " + slot + ". Bàn: " + list
                        );

                    } else {
                        request.setAttribute("flash_error", "Không thể đặt. Có bàn đã bị giữ trước.");
                    }
                } catch (Exception e) {
                    request.setAttribute("flash_error", "Lỗi đặt bàn: " + e.getMessage());
                }
                doGet(request, response);
                return;
            }
            default: {
                request.setAttribute("flash_error", "Hành động không hợp lệ.");
                doGet(request, response);
            }
        }
    }
}