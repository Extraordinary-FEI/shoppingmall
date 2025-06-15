<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户登录 - 购物商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user/products.jsp">首页</a></li>
            <li><a href="${pageContext.request.contextPath}/user/cart.jsp">购物车</a></li>
            <li><a href="${pageContext.request.contextPath}/user/login.jsp">登录</a></li>
            <li><a href="${pageContext.request.contextPath}/user/register.jsp">注册</a></li>
        </ul>
    </nav>
    <div class="container fade-in">
        <div class="card">
            <h2>用户登录</h2>
            <c:if test="${not empty errorMessage}">
                <p class="error-message">${errorMessage}</p>
            </c:if>
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="username">用户名:</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div class="form-group">
                    <label for="password">密码:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit">登录</button>
            </form>
            <div style="text-align: center; margin-top: 20px;">
                <p>还没有账户? <a href="${pageContext.request.contextPath}/user/register.jsp">立即注册</a></p>
            </div>
        </div>
    </div>
    <footer>
        <p>&copy; 2025 炫酷商城. 版权所有.</p>
    </footer>
</body>
</html>

