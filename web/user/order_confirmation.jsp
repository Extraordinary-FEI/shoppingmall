<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Redirect to login if user is not logged in or no order in session --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?redirect=order_confirmation"/>
</c:if>
<c:if test="${empty sessionScope.latestOrder}">
    <c:redirect url="${pageContext.request.contextPath}/user/products.jsp?message=NoOrderToConfirm"/>
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>订单已提交 - 购物商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        .success-icon {
            font-size: 5em;
            color: #c792ea; /* Purple for success icon */
            margin-bottom: 20px;
            animation: pulse 1.5s infinite;
        }
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }
        .order-details-card {
            background-color: #2c2c2c; /* Darker card background */
            padding: 25px;
            border-radius: 8px;
            margin-top: 30px;
            text-align: left;
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
        }
        .order-details-card h3 {
            color: #c792ea; /* Purple for heading */
            border-bottom: 1px solid #444;
            padding-bottom: 10px;
            margin-top: 0;
            margin-bottom: 20px;
            font-size: 1.6em;
        }
        .detail-item {
            margin-bottom: 10px;
            color: #e0e0e0;
            font-size: 1.1em;
        }
        .detail-item strong {
            color: #f0f0f0;
            margin-right: 8px;
        }
        .order-details-card ul {
            list-style-type: none;
            padding-left: 0;
        }
        .order-details-card ul li {
            padding: 5px 0;
            border-bottom: 1px dashed #444;
        }
        .order-details-card ul li:last-child {
            border-bottom: none;
        }
        .actions {
            margin-top: 30px;
        }
        .actions .button {
            margin: 0 10px;
        }
    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user/products.jsp">商品首页</a></li>
            <li class="cart-info"><a href="${pageContext.request.contextPath}/cart">购物车</a></li>
            <c:if test="${not empty sessionScope.currentUser}">
                <li><a href="#">欢迎, ${sessionScope.currentUser.username}!</a></li>
                <li><a href="${pageContext.request.contextPath}/login?action=logout">退出登录</a></li>
            </c:if>
        </ul>
    </nav>

    <div class="container fade-in" style="text-align: center;">
        <div class="card">
            <div class="success-icon">&#10004;</div>
            <h1>订单已成功提交!</h1>
            <p style="font-size: 1.2em; color: #e0e0e0;">感谢您的购买。您的订单正在处理中。</p>
            <p style="font-size: 1.1em; color: #ccc;">订单号为: <strong>${sessionScope.latestOrder.orderId}</strong></p>
            
            <c:if test="${not empty sessionScope.latestOrder}">
                <div class="order-details-card">
                    <h3>订单详情</h3>
                    <div class="detail-item"><strong>总金额:</strong> <fmt:formatNumber value="${sessionScope.latestOrder.totalAmount}" type="currency"/></div>
                    <div class="detail-item"><strong>收货地址:</strong> ${sessionScope.latestOrder.shippingAddress}</div>
                    <div class="detail-item"><strong>联系电话:</strong> ${sessionScope.latestOrder.phone}</div>
                    <div class="detail-item"><strong>支付方式:</strong> ${sessionScope.latestOrder.paymentMethod}</div>
                    <div class="detail-item"><strong>预计送达时间:</strong> 3-5个工作日 (仅供参考)</div>
                    <h4 style="color: #c792ea; margin-top: 20px; margin-bottom: 10px;">购买的商品:</h4>
                    <ul>
                        <c:forEach var="orderItem" items="${sessionScope.latestOrder.items}">
                            <li>
                                ${orderItem.product.name} (x${orderItem.quantity}) - 
                                单价: <fmt:formatNumber value="${orderItem.priceAtPurchase}" type="currency"/>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <div class="actions">
                <a href="${pageContext.request.contextPath}/user/products.jsp" class="button">继续购物</a>
                <a href="${pageContext.request.contextPath}/order?action=history" class="button" style="background-color: #555;">查看订单历史</a>
            </div>
        </div>
    </div>

    <footer>
        <p>&copy; 2025 炫酷商城. 版权所有.</p>
    </footer>
</body>
</html>

