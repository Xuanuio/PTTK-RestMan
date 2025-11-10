<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<%@ page import="com.pttk.restman.model.User" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="addFoodStyles.css">
<title>Trang chủ quản lí</title>
<script>
    // Hàm để chuyển hướng trang
    function openPage(pageUrl) {
        window.location.href = pageUrl;
    }
</script>
</head>
<body>
<div class="container">
<%--<%--%>
<%--User user = (User)session.getAttribute("manager");--%>
<%--if(user ==null){--%>
<%--response.sendRedirect("LoginView.jsp?err=timeout");--%>
<%--}--%>
<%--%>--%>
<h2> Trang chủ quản lí</h2>
<button onclick="openPage('FoodManagementView.jsp')">Quản lí thông tin món ăn</button>
</div>
</body>
</html>