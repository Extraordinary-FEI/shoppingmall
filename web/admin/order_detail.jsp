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
    <title>订单详情 - 炫酷商城后台</title>
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
            color: #e0e0e0;
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
        }
        .order-detail-container h2 {
            margin-top: 0;
            color: #c792ea; /* Purple for section titles */
            font-size: 1.8em;
            margin-bottom: 25px;
            border-bottom: 1px solid #444;
            padding-bottom: 10px;
        }
        .detail-section {
            margin-bottom: 30px;
            padding: 20px;
            background-color: #2c2c2c; /* Slightly lighter dark for sections */
            border-radius: 8px;
            border: 1px solid #444;
        }
        .detail-section h3 {
            font-size: 1.4em;
            color: #c792ea; /* Purple for sub-headings */
            margin-top: 0;
            margin-bottom: 20px;
            border-bottom: 1px dashed #555;
            padding-bottom: 10px;
        }
        .detail-item {
            display: flex;
            margin-bottom: 12px;
            font-size: 1.1em;
        }
        .detail-item strong {
            color: #b0b0b0; /* Lighter grey for labels */
            width: 160px;
            margin-right: 10px;
            flex-shrink: 0;
        }
        .detail-item span {
            color: #e0e0e0;
        }
        .product-list-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .product-list-table th, .product-list-table td {
            border: 1px solid #444;
            padding: 12px 15px;
            text-align: left;
            color: #e0e0e0;
        }
        .product-list-table th {
            background-color: #383838;
            color: #c792ea;
            font-weight: bold;
        }
        .product-list-table tr:nth-child(even) {
            background-color: #252525;
        }
        .product-thumbnail {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 6px;
            margin-right: 10px;
            vertical-align: middle;
            border: 1px solid #555;
        }
        .alert-cool.danger {
            background-color: rgba(231, 76, 60, 0.2);
            border-color: #e74c3c;
            color: #f0f0f0;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .status-update-form select {
            margin-right: 10px;
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
            <h1>订单详情</h1>
            <div class="user-info">
                <span>管理员: ${sessionScope.currentUser.username} | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
            </div>
        </header>

        <div class="content-area">
            <div class="order-detail-container">
                <c:if test="${not empty errorMessage}">
                    <div class="alert-cool danger">${errorMessage}</div>
                </c:if>
                <c:if test="${empty orderDetails}">
                    <p>未找到指定的订单信息，或订单信息加载失败。</p>
                </c:if>

                <c:if test="${not empty orderDetails}">
                    <h2>订单号: ${orderDetails.orderId}</h2>

                    <div class="detail-section">
                        <h3>基本信息</h3>
                        <div class="detail-item"><strong>用户ID:</strong> <span>${orderDetails.userId}</span></div>
                        <div class="detail-item"><strong>用户名:</strong> <span>${orderDetails.user.username}</span></div>
                        <div class="detail-item"><strong>订单日期:</strong> <span><fmt:formatDate value="${orderDetails.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span></div>
                        <div class="detail-item"><strong>订单状态:</strong> <span>${orderDetails.status}</span></div>
                        <div class="detail-item"><strong>总金额:</strong> <span><fmt:formatNumber value="${orderDetails.totalAmount}" type="currency"/></span></div>
                    </div>

                    <div class="detail-section">
                        <h3>配送与支付信息</h3>
                        <div class="detail-item"><strong>收货人:</strong> <span><c:out value="${orderDetails.fullName}"/></span></div>
                        <div class="detail-item"><strong>联系电话:</strong> <span><c:out value="${orderDetails.phone}"/></span></div>
                        <div class="detail-item"><strong>收货地址:</strong> <span><c:out value="${orderDetails.shippingAddress}"/></span></div>
                        <div class="detail-item"><strong>支付方式:</strong> <span><c:out value="${orderDetails.paymentMethod}"/></span></div>
                    </div>

                    <div class="detail-section product-list">
                        <h3>商品列表</h3>
                        <table class="product-list-table">
                            <thead>
                                <tr>
                                    <th>商品图片</th>
                                    <th>商品名称</th>
                                    <th>购买单价</th>
                                    <th>数量</th>
                                    <th>小计</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${orderDetails.items}">
                                    <tr>
                                        <c:choose>
                                            <c:when test="${not empty item.product}">
                                                <td>
                                                    <img src="${pageContext.request.contextPath}/${not empty item.product.imageUrl ? item.product.imageUrl : 'images/default_product.png'}" alt="${item.product.name}" class="product-thumbnail">
                                                </td>
                                                <td><c:out value="${item.product.name}"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td><img src="${pageContext.request.contextPath}/images/default_product.png" alt="商品信息不可用" class="product-thumbnail"></td>
                                                <td>商品信息不可用 (可能已下架或ID无效)</td>
                                            </c:otherwise>
                                        </c:choose>
                                        <td><fmt:formatNumber value="${item.priceAtPurchase}" type="currency"/></td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.priceAtPurchase * item.quantity}" type="currency"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="detail-section">
                        <h3>更新订单状态</h3>
                        <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="status-update-form form-group" style="display:flex; align-items:center;">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="orderId" value="${orderDetails.orderId}">
                            <input type="hidden" name="fromDetail" value="true"> <%-- To redirect back here --%>
                            <label for="status" style="margin-right:10px; color: #b0b0b0;">新状态:</label>
                            <select name="status" id="status" class="form-control" style="padding: 10px; background-color: #333; color: #f0f0f0; border: 1px solid #555; border-radius: 4px;">
                                <option value="PENDING" ${orderDetails.status == 'PENDING' ? 'selected' : ''}>待处理</option>
                                <option value="PROCESSING" ${orderDetails.status == 'PROCESSING' ? 'selected' : ''}>处理中</option>
                                <option value="SHIPPED" ${orderDetails.status == 'SHIPPED' ? 'selected' : ''}>已发货</option>
                                <option value="DELIVERED" ${orderDetails.status == 'DELIVERED' ? 'selected' : ''}>已送达</option>
                                <option value="CANCELLED" ${orderDetails.status == 'CANCELLED' ? 'selected' : ''}>已取消</option>
                            </select>
                            <button type="submit" class="button" style="margin-left:15px;">更新状态</button>
                        </form>
                    </div>

                </c:if>
                <a href="${pageContext.request.contextPath}/admin/orders" class="button" style="background-color: #555; margin-top: 20px;">&laquo; 返回订单列表</a>
            </div>
        </div>
    </div>
</body>
</html>

