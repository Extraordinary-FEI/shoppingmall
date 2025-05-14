<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty sessionScope.currentUser || sessionScope.currentUser.role ne 'ADMIN'}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?error=AdminAccessRequired"/>
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>订单管理 - 炫酷商城后台</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            background-color: #121212;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
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
        .admin-header .user-info span {
            color: #e0e0e0;
        }
        .admin-header .user-info a {
            color: #c792ea;
            text-decoration: none;
            font-weight: bold;
        }
        .content-area {
            background-color: #1e1e1e;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
            color: #e0e0e0;
        }
        .table-actions {
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
        }
        .search-form {
            display: flex;
            align-items: center;
        }
        .search-form input[type="text"] {
            margin-right: 10px;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #444;
            background-color: #333;
            color: #e0e0e0;
        }
        .admin-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            color: #e0e0e0;
        }
        .admin-table th, .admin-table td {
            border: 1px solid #444;
            padding: 12px 15px;
            text-align: left;
            vertical-align: middle;
        }
        .admin-table th {
            background-color: #383838;
            color: #c792ea;
            font-weight: bold;
        }
        .admin-table tr:nth-child(even) {
            background-color: #2c2c2c;
        }
        .admin-table tr:hover {
            background-color: #3c3c3c;
        }
        .order-items-list {
            list-style-type: none;
            padding-left: 0;
            font-size: 1em;
        }
        .order-items-list li {
            margin-bottom: 8px;
            padding: 5px 0;
            border-bottom: 1px dashed #444;
        }
        .order-items-list li:last-child {
            border-bottom: none;
        }
        .alert-cool {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            color: #f0f0f0;
            border: 1px solid;
        }
        .alert-cool.success {
            background-color: rgba(46, 204, 113, 0.2);
            border-color: #2ecc71;
        }
        .alert-cool.danger {
            background-color: rgba(231, 76, 60, 0.2);
            border-color: #e74c3c;
        }
        /* Unified Button Styles */
        .button {
            display: inline-block;
            padding: 10px 18px;
            font-size: 0.9em;
            font-weight: bold;
            text-align: center;
            text-decoration: none;
            color: #ffffff;
            background-color: #c792ea; /* Default: Purple */
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
            margin-right: 8px;
            line-height: 1.5; /* Ensure text is vertically centered */
        }
        .button:hover {
            transform: translateY(-1px);
        }
        .button:active {
            transform: translateY(0px);
        }
        .button.btn-info { /* For "View Details" */
            background-color: #5bc0de;
        }
        .button.btn-info:hover {
            background-color: #46b8da;
        }
        .button.btn-primary { /* For "Update Status", "Search" */
            background-color: #c792ea;
        }
        .button.btn-primary:hover {
            background-color: #b37cd9;
        }
        .button.btn-secondary { /* For "Back to Order List" */
            background-color: #555555;
        }
        .button.btn-secondary:hover {
            background-color: #444444;
        }
        .status-update-form {
            display: inline-flex;
            align-items: center;
            gap: 8px; /* Spacing between select and button */
        }
        .status-update-form select {
            padding: 9px 10px; /* Adjusted to align height with button */
            background-color: #333;
            color: #f0f0f0;
            border: 1px solid #555;
            border-radius: 5px;
            height: 40px; /* Match button approximate height */
            box-sizing: border-box;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>炫酷商城后台</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">&#128200; 仪表盘</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/users">&#128101; 用户管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/products">&#128722; 商品管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/orders" class="active">&#128230; 订单管理</a></li>
            <li><a href="${pageContext.request.contextPath}/login?action=logout">&#128682; 退出登录</a></li>
        </ul>
    </div>

    <div class="main-content fade-in">
        <header class="admin-header">
            <h1>订单管理</h1>
            <div class="user-info">
                <span>管理员: ${sessionScope.currentUser.username} | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
            </div>
        </header>

        <div class="content-area">
            <div class="table-actions">
                <div>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="button btn-secondary">&laquo; 返回订单列表</a>
                </div>
                <form action="${pageContext.request.contextPath}/admin/orders" method="get" class="search-form">
                    <input type="hidden" name="action" value="search">
                    <input type="text" name="searchTerm" placeholder="搜索订单..." value="${param.searchTerm}">
                    <button type="submit" class="button btn-primary">搜索</button>
                </form>
            </div>
            
            <c:if test="${not empty message}">
                <div class="alert-cool success">${message}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert-cool danger">${errorMessage}</div>
            </c:if>

            <div style="overflow-x: auto;">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>订单ID</th>
                            <th>用户ID</th>
                            <th>订单日期</th>
                            <th>总金额</th>
                            <th>状态</th>
                            <th>收货地址</th>
                            <th>支付方式</th>
                            <th>商品详情</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orderList}">
                            <tr>
                                <td>${order.orderId}</td>
                                <td>${order.userId}</td>
                                <td><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td><fmt:formatNumber value="${order.totalAmount}" type="currency"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="status-update-form">
                                        <input type="hidden" name="action" value="updateStatus">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <select name="status" class="form-control">
                                            <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>待处理</option>
                                            <option value="PROCESSING" ${order.status == 'PROCESSING' ? 'selected' : ''}>处理中</option>
                                            <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>已发货</option>
                                            <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>已送达</option>
                                            <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>已取消</option>
                                        </select>
                                        <button type="submit" class="button btn-primary">更新状态</button>
                                    </form>
                                </td>
                                <td><c:out value="${order.shippingAddress}"/></td>
                                <td><c:out value="${order.paymentMethod}"/></td>
                                <td>
                                    <ul class="order-items-list">
                                    <c:forEach var="item" items="${order.items}">
                                        <li>
                                            ${item.product.name} (ID: ${item.productId}) x ${item.quantity} @ <fmt:formatNumber value="${item.priceAtPurchase}" type="currency"/>
                                        </li>
                                    </c:forEach>
                                    </ul>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/orders?action=view&orderId=${order.orderId}" class="button btn-info">查看详情</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty orderList}">
                            <tr>
                                <td colspan="9" style="text-align:center; padding: 20px;">未找到订单数据。</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>

