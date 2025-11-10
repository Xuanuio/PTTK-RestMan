<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.ArrayList,dao.*,model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dang nhap</title>
<style>
    body {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
        margin: 0;
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
    }

    .container {
        background-color: #fff;
        padding: 20px 40px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        border-radius: 8px;
        width: 100%;
        max-width: 400px;
        text-align: center;
    }

    h2 {
        color: #333;
    }

    h4 {
        color: red;
        margin-bottom: 20px;
    }

    table {
        width: 100%;
        margin-top: 20px;
    }

    td {
        padding: 10px 0;
        text-align: left;
    }

    input[type="text"], input[type="password"] {
        width: 100%;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 4px;
    }

    input[type="submit"] {
        width: 100%;
        padding: 10px;
        background-color: #007bff;
        border: none;
        border-radius: 4px;
        color: #fff;
        font-size: 16px;
        cursor: pointer;
        margin-top: 10px;
    }

    input[type="submit"]:hover {
        background-color: #0056b3;
    }
</style>
</head>
<body>
	<div class="container">
	<%
	if (request.getParameter("err") != null && request.getParameter("err").equalsIgnoreCase("timeout")) {
	%>
	<h4>Hết phiên làm việc. Làm ơn đăng nhập lại!</h4>
	<%
	} else if (request.getParameter("err") != null && request.getParameter("err").equalsIgnoreCase("fail")) {
	%>
	<h4 color="red">Sai tên đăng nhập/mật khẩu!</h4>
	<%
	}
	%>
	<h2>Đăng nhập</h2>
	<form name="dangnhap" action="${pageContext.request.contextPath}/login" method="post">
		<table border="0">
			<tr>
				<td>Tên đăng nhập:</td>
				<td><input type="text" name="username" id="username" required /></td>
			</tr>
			<tr>
				<td>Mật khẩu:</td>
				<td><input type="password" name="password" id="password"
					required /></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="Đăng nhập" /></td>
			</tr>
		</table>
	</form>
	</div>
</body>
</html>