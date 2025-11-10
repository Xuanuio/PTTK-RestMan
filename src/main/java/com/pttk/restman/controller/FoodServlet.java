package com.pttk.restman.controller;

import com.pttk.restman.dao.FoodDAO;
import com.pttk.restman.model.Food;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@WebServlet(urlPatterns = {"/FoodServlet", "/food", "/food/add"})
@MultipartConfig
public class FoodServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FoodDAO foodDAO = new FoodDAO();

    public FoodServlet() { super(); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("manager/AddFoodView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        boolean isMultipart = false;
        String ct = request.getContentType();
        if (ct != null) isMultipart = ct.toLowerCase().startsWith("multipart/");

        String name        = isMultipart ? readField(request, "name")        : nz(request.getParameter("name"));
        String priceStr    = isMultipart ? readField(request, "price")       : nz(request.getParameter("price"));
        String description = isMultipart ? readField(request, "description") : nz(request.getParameter("description"));

        String photoText   = isMultipart ? readField(request, "photo")       : nz(request.getParameter("photo"));
        String photoFile   = getFileName(request, "photoFile");
        String photo       = firstNonEmpty(trim(photoText), photoFile);

        if (isBlank(name)) {
            back(request, response, "Thiếu tên món.", name, priceStr, description, photo);
            return;
        }
        if (isBlank(priceStr)) {
            back(request, response, "Thiếu giá.", name, priceStr, description, photo);
            return;
        }

        Float price = tryParseFloat(priceStr);
        if (price == null) {
            back(request, response, "Giá không hợp lệ.", name, priceStr, description, photo);
            return;
        }
        if (isBlank(description)) {
            back(request, response, "Thiếu mô tả.", name, priceStr, description, photo);
            return;
        }
        if (isBlank(photo)) {
            back(request, response, "Thiếu ảnh (URL hoặc file).", name, priceStr, description, photo);
            return;
        }

        Food food = new Food();
        food.setName(name.trim());
        food.setPrice(price);
        food.setDescription(description.trim());
        food.setPhoto(photo.trim());

        boolean ok;
        try {
            ok = foodDAO.saveNewFood(food);
        } catch (Exception e) {
            ok = false;
        }

        if (ok) {
            request.setAttribute("message", "Lưu thành công");
            // clear các giá trị đã nhập sau khi lưu
            request.setAttribute("v_name", "");
            request.setAttribute("v_price", "");
            request.setAttribute("v_desc", "");
            request.setAttribute("v_photo", "");
        } else {
            String err = "";
            String msg = switch (nz(err)) {
                case "DUP_NAME" -> "Tên món đã tồn tại.";
                case "DUP_PHOTO" -> "Ảnh đã được dùng cho món khác.";
                case "EMPTY_NAME" -> "Thiếu tên món.";
                case "EMPTY_DESC" -> "Thiếu mô tả.";
                case "EMPTY_PHOTO" -> "Thiếu ảnh.";
                case "BAD_PRICE" -> "Giá không hợp lệ.";
                case "DB_CONN_ERROR" -> "Không kết nối được CSDL.";
                default -> "Lưu thất bại";
            };
            back(request, response, msg, name, priceStr, description, photo);
            return;
        }

        request.getRequestDispatcher("manager/AddFoodView.jsp").forward(request, response);
    }

    private static String readField(HttpServletRequest req, String name) throws IOException, ServletException {
        String v = req.getParameter(name);
        if (v != null && !v.trim().isEmpty()) return v;
        Part p = null;
        try { p = req.getPart(name); } catch (Exception ignore) {}
        if (p != null && p.getSize() > 0) {
            try (InputStream is = p.getInputStream()) {
                String s = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                if (s != null && !s.trim().isEmpty()) return s;
            }
        }
        return null;
    }

    private static String getFileName(HttpServletRequest req, String field) throws IOException, ServletException {
        Part p = null;
        try { p = req.getPart(field); } catch (Exception ignore) {}
        if (p == null || p.getSize() == 0) return null;
        String fn = p.getSubmittedFileName();
        return (fn == null || fn.isBlank()) ? null : fn.trim();
    }

    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static String trim(String s){ return s == null ? null : s.trim(); }
    private static String nz(String s){ return s == null ? "" : s; }
    private static String firstNonEmpty(String a, String b){ return !isBlank(a) ? a : (!isBlank(b) ? b : null); }

    private static Float tryParseFloat(String s) {
        try {
            String t = (s == null) ? null : s.trim();
            if (t == null || t.isEmpty()) return null;
            return Float.parseFloat(t);
        } catch (Exception e) {
            return null;
        }
    }

    private void back(HttpServletRequest req, HttpServletResponse resp,
                      String message, String name, String price, String desc, String photo) throws ServletException, IOException {
        req.setAttribute("message", message);
        req.setAttribute("v_name", nz(name));
        req.setAttribute("v_price", nz(price));
        req.setAttribute("v_desc", nz(desc));
        req.setAttribute("v_photo", nz(photo));
        req.getRequestDispatcher("manager/AddFoodView.jsp").forward(req, resp);
    }
}