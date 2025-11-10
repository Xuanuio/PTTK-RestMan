<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm Thông Tin Món Ăn</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            width: 400px;
        }

        .container h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .form-group input[type="text"], .form-group input[type="number"],
        .form-group input[type="file"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        .form-group input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
        }

        .form-group input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Thêm Thông Tin Món Ăn</h2>

    <c:if test="${not empty message}">
        <p style="color: ${message == 'Lưu thành công' ? 'green' : 'red'};">${message}</p>
    </c:if>

    <form action="<%= request.getContextPath() %>/FoodServlet" method="post">
        <div class="form-group">
            <label for="name">Tên Món Ăn:</label>
            <input type="text" id="name" name="name" required>
        </div>
        <div class="form-group">
            <label for="price">Giá:</label>
            <input type="text" id="price" name="price" required>
        </div>
        <div class="form-group">
            <label for="description">Mô Tả:</label>
            <input type="text" id="description" name="description" required>
        </div>
        <div class="form-group">
            <label for="photo">Ảnh:</label>
            <input type="file" id="photo" name="photo" accept="image/*" required>
        </div>
        <div class="form-group">
            <input type="submit" value="Thêm Món Ăn">
        </div>
    </form>
</div>
</body>
</html>