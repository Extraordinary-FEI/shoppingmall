<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的订单 - 炫酷商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        .order-history-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 25px;
            background-color: #2c2c2c;
            border-radius: 8px;
            overflow: hidden;
        }
        .order-history-table th, .order-history-table td {
            border: 1px solid #444;
            padding: 12px 15px;
            text-align: left;
            color: #f0f0f0;
        }
        .order-history-table th {
            background-color: #383838;
            color: #c792ea;
            font-weight: bold;
        }
        .order-details-link {
            color: #82aaff;
            text-decoration: none;
        }
        .order-details-link:hover {
            text-decoration: underline;
        }
        .status-PENDING { color: #ffcc00; font-weight: bold; }
        .status-PAID { color: #2ecc71; font-weight: bold; }
        .status-SHIPPED { color: #3498db; font-weight: bold; }
        .status-DELIVERED { color: #9b59b6; font-weight: bold; }
        .status-CANCELLED { color: #e74c3c; font-weight: bold; }
    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/products">商品首页</a></li>
            <li class="cart-info"><a href="${pageContext.request.contextPath}/cart">购物车 (<span id="cartItemCount">${empty sessionScope.cart.items ? 0 : sessionScope.cart.totalItems}</span>)</a></li>
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <li><a href="#">欢迎, ${sessionScope.currentUser.username}!</a></li>
                    <li><a href="${pageContext.request.contextPath}/order?action=history">我的订单</a></li>
                    <li><a href="${pageContext.request.contextPath}/login?action=logout">退出登录</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageContext.request.contextPath}/user/login.jsp">登录</a></li>
                    <li><a href="${pageContext.request.contextPath}/user/register.jsp">注册</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </nav>

    <div class="container fade-in">
        <div class="card">
            <h1>我的订单历史</h1>

            <c:if test="${not empty requestScope.error}">
                <p class="error-message">${requestScope.error}</p>
            </c:if>

            <c:choose>
                <c:when test="${not empty requestScope.orderHistory}">
                    <table class="order-history-table">
                        <thead>
                            <tr>
                                <th>订单ID</th>
                                <th>下单日期</th>
                                <th>总金额</th>
                                <th>状态</th>
                                <th>支付方式</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${requestScope.orderHistory}">
                                <tr>
                                    <td>${order.orderId}</td>
                                    <td><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                    <td><fmt:formatNumber value="${order.totalAmount}" type="currency"/></td>
                                    <td><span class="status-${order.status}">${order.status}</span></td>
                                    <td>${order.paymentMethod}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/order?orderId=${order.orderId}" class="order-details-link">查看详情</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>您还没有任何订单记录。</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <footer>
        <p>&copy; <c:set var="currentYear"><jsp:useBean id="date" class="java.util.Date" /><fmt:formatDate value="${date}" pattern="yyyy" /></c:set>${currentYear} 炫酷商城. 版权所有.</p>
    </footer>
</body>
</html>

