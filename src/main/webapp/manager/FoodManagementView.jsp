<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<%@ page import="com.pttk.restman.model.User" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Quản lí thông tin món ăn</title>
<script>
    function openPage(pageUrl) {
        window.location.href = pageUrl;
    }
</script>
<style>
    * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
        font-family: Arial, sans-serif;
    }

    body {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        background-color: #f7f7f7;
        padding: 20px;
    }

    .container {
        background-color: #ffffff;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 500px;
        text-align: center;
    }

    h2 {
        font-size: 24px;
        color: #333;
        margin-bottom: 20px;
    }

    button {
        padding: 12px 20px;
        font-size: 16px;
        color: #ffffff;
        background-color: #00aaff;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    button:hover {
        background-color: #0088cc;
    }
</style>
</head>
<body>
<%--<%--%>
<%--User user = (User)session.getAttribute("manager");--%>
<%--if(user ==null){--%>
<%--response.sendRedirect("LoginView.jsp?err=timeout");--%>
<%--}--%>
<%--%>--%>
<div class="container">
<h2> Quản lí thông tin món ăn</h2>
<button onclick="openPage('AddFoodView.jsp')">Thêm món ăn</button>
</div>
</body>
</html>