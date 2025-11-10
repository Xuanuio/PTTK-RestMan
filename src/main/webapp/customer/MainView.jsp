<%@ page import="com.pttk.restman.model.User" %>
<%@ page import="com.pttk.restman.model.Customer" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Trang chủ khách hàng</title>
    <style>
        html, body {
            height: 100%;
            margin: 0;
        }
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f5f7fb;
            font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
            color: #0f172a;
        }
        .card {
            background: #ffffff;
            padding: 32px 28px;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(2, 6, 23, 0.08);
            text-align: center;
            min-width: 320px;
        }
        h2 {
            margin: 0 0 16px;
            font-weight: 700;
            font-size: 22px;
        }
        .btn {
            display: inline-block;
            padding: 12px 18px;
            border-radius: 10px;
            border: none;
            cursor: pointer;
            background: #1976d2;           /* xanh dương */
            color: #ffffff;
            font-weight: 600;
            font-size: 15px;
            transition: transform .05s ease, background .2s ease, box-shadow .2s ease;
            box-shadow: 0 6px 16px rgba(25, 118, 210, 0.25);
        }
        .btn:hover {
            background: #1565c0;           /* đậm hơn khi hover */
        }
        .btn:active {
            transform: translateY(1px);
            box-shadow: 0 4px 12px rgba(25, 118, 210, 0.25);
        }
    </style>
    <script>
        function openPage(pageUrl) {
            window.location.href = pageUrl;
        }
    </script>
</head>
<body>
<%
    // giả lập phiên đăng nhập khách hàng
    Customer customer = new Customer();
    customer.setId(2);
    session.setAttribute("customer", customer);

    // Nếu muốn chặn truy cập khi chưa có session, dùng khối dưới:
    // User user = (User) session.getAttribute("customer");
    // if (user == null) {
    //     response.sendRedirect("LoginView.jsp?err=timeout");
    //     return;
    // }
%>

<div class="card">
    <h2>Trang chủ khách hàng</h2>
    <button class="btn" onclick="openPage('OrderView.jsp')">Đặt bàn</button>
</div>

</body>
</html>
