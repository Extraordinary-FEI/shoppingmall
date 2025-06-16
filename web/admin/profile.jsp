<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Redirect to login if not admin --%>
<c:if test="${empty sessionScope.currentUser || sessionScope.currentUser.role ne 'ADMIN'}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?error=AdminAccessRequired"/>
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员个人中心 - 炫酷商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            background-color: #121212;
        }
        .sidebar {
            width: 260px;
            background-color: #1e1e1e;
            color: #e0e0e0;
            padding: 20px;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            overflow-y: auto;
            box-shadow: 2px 0 5px rgba(0,0,0,0.5);
        }
        .sidebar h2 {
            text-align: center;
            margin-bottom: 30px;
            font-size: 1.8em;
            color: #c792ea;
            border-bottom: 1px solid #444;
            padding-bottom: 15px;
        }
        .sidebar ul {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }
        .sidebar ul li a {
            color: #b0b0b0;
            text-decoration: none;
            padding: 15px 20px;
            display: block;
            border-radius: 6px;
            margin-bottom: 10px;
            transition: background-color 0.3s ease, color 0.3s ease, transform 0.2s ease;
        }
        .sidebar ul li a:hover,
        .sidebar ul li a.active {
            background-color: #c792ea;
            color: #1e1e1e;
            transform: translateX(5px);
        }
        .main-content {
            margin-left: 260px;
            padding: 30px;
            width: calc(100% - 260px);
            background-color: #121212;
        }
        .admin-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            background-color: #1e1e1e;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.3);
            margin-bottom: 30px;
        }
        .admin-header h1 {
            margin: 0;
            font-size: 2em;
            color: #c792ea;
        }
        .admin-header .user-info a {
            color: #c792ea;
            text-decoration: none;
            font-weight: bold;
        }
        .admin-header .user-info a:hover {
            text-decoration: underline;
        }
        .profile-content {
            background-color: #1e1e1e;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.3);
        }
        .profile-content h2 {
            color: #c792ea;
            font-size: 2.2em;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #444;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #e0e0e0;
            font-size: 1.05em;
        }
        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="password"],
        .form-group input[type="number"],
        .form-group textarea {
            width: calc(100% - 24px);
            padding: 12px;
            border: 1px solid #555;
            border-radius: 6px;
            background-color: #3a3a3a;
            color: #e0e0e0;
            font-size: 1em;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
        }
        .form-group input[type="text"]:focus,
        .form-group input[type="email"]:focus,
        .form-group input[type="password"]:focus,
        .form-group input[type="number"]:focus,
        .form-group textarea:focus {
            border-color: #c792ea;
            box-shadow: 0 0 0 3px rgba(199, 146, 234, 0.3);
            outline: none;
        }
        .profile-content button {
            background-color: #c792ea;
            color: #1e1e1e;
            padding: 12px 25px;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .profile-content button:hover {
            background-color: #b47eda;
            transform: translateY(-2px);
        }
        .message-success {
            color: #2ecc71;
            background-color: rgba(46, 204, 113, 0.1);
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            border: 1px solid #2ecc71;
            text-align: center;
        }
        .message-error {
            color: #e74c3c;
            background-color: rgba(231, 76, 60, 0.1);
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            border: 1px solid #e74c3c;
            text-align: center;
        }
        .admin-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: linear-gradient(145deg, #8e44ad, #5e3370);
            color: white;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .stat-card h3 {
            margin: 0 0 10px 0;
            font-size: 2.5em;
        }
        .stat-card p {
            margin: 0;
            font-size: 1.1em;
        }
        @media (max-width: 768px) {
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
            }
            .main-content {
                margin-left: 0;
                width: 100%;
                padding: 15px;
            }
        }
    </style>
</head>
<body>
<div class="sidebar">
    <h2>炫酷商城后台</h2>
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">&#128200; 仪表盘</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">&#128101; 用户管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/products">&#128722; 商品管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/orders">&#128230; 订单管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/profile.jsp" class="active">&#128100; 个人中心</a></li>
        <li><a href="${pageContext.request.contextPath}/login?action=logout">&#128682; 退出登录</a></li>
    </ul>
</div>

<div class="main-content fade-in">
    <header class="admin-header">
        <h1>管理员个人中心</h1>
        <div class="user-info">
            <span>管理员: <a href="${pageContext.request.contextPath}/admin/profile.jsp">${sessionScope.currentUser.username}</a> | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
        </div>
    </header>

    <div class="profile-content">
        <div id="personalInfoSection">
            <h2>个人信息</h2>
            <c:if test="${not empty successMessage}">
                <p class="message-success">${successMessage}</p>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <p class="message-error">${errorMessage}</p>
            </c:if>

            <div class="admin-stats">
                <div class="stat-card">
                    <h3>${requestScope.totalUsers != null ? requestScope.totalUsers : 0}</h3>
                    <p>管理用户数</p>
                </div>
                <div class="stat-card">
                    <h3>${requestScope.totalProducts != null ? requestScope.totalProducts : 0}</h3>
                    <p>管理商品数</p>
                </div>
                <div class="stat-card">
                    <h3>${requestScope.pendingOrders != null ? requestScope.pendingOrders : 0}</h3>
                    <p>待处理订单</p>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/updateAdminProfile" method="post">
                <div class="form-group">
                    <label for="username">管理员账号:</label>
                    <input type="text" id="username" name="username" value="${sessionScope.currentUser.username}" readonly>
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" value="${sessionScope.currentUser.email}">
                </div>
                <div class="form-group">
                    <label for="fullName">真实姓名:</label>
                    <input type="text" id="fullName" name="fullName" value="${sessionScope.currentUser.fullName}">
                </div>
                <div class="form-group">
                    <label for="phone">联系电话:</label>
                    <input type="text" id="phone" name="phone" value="${sessionScope.currentUser.phone}">
                </div>
                <div class="form-group">
                    <label for="address">办公地址:</label>
                    <textarea id="address" name="address" rows="3">${sessionScope.currentUser.address}</textarea>
                </div>
                <button type="submit">保存信息</button>
            </form>

            <hr style="margin: 40px 0; border: 1px solid #444;">

            <h2>修改密码</h2>
            <c:if test="${not empty passwordSuccessMessage}">
                <p class="message-success">${passwordSuccessMessage}</p>
            </c:if>
            <c:if test="${not empty passwordErrorMessage}">
                <p class="message-error">${passwordErrorMessage}</p>
            </c:if>
            <form action="${pageContext.request.contextPath}/changeAdminPassword" method="post">
                <div class="form-group">
                    <label for="currentPassword">当前密码:</label>
                    <input type="password" id="currentPassword" name="currentPassword" required>
                </div>
                <div class="form-group">
                    <label for="newPassword">新密码:</label>
                    <input type="password" id="newPassword" name="newPassword" required>
                </div>
                <div class="form-group">
                    <label for="confirmNewPassword">确认新密码:</label>
                    <input type="password" id="confirmNewPassword" name="confirmNewPassword" required>
                </div>
                <button type="submit">修改密码</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>

