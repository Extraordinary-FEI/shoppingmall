<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的购物车 - 购物商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        /* Additional specific styles for cart page if needed, otherwise cool_style.css handles most */
        .cart-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 25px;
            background-color: #2c2c2c; /* Darker card background for table */
            border-radius: 8px;
            overflow: hidden; /* Ensures border-radius is applied to table */
        }
        .cart-table th, .cart-table td {
            border: 1px solid #444; /* Darker borders */
            padding: 12px 15px;
            text-align: left;
            color: #f0f0f0; /* Light text for table content */
        }
        .cart-table th {
            background-color: #383838; /* Slightly lighter header for table */
            color: #c792ea; /* Purple for table headers */
            font-weight: bold;
        }
        .cart-table img {
            border-radius: 4px;
        }
        .cart-table input[type="number"] {
            width: 60px;
            padding: 8px;
            text-align: center;
            border: 1px solid #555;
            border-radius: 4px;
            background-color: #333;
            color: #f0f0f0;
        }
        .cart-summary h3 {
            color: #e0e0e0;
        }
        .cart-actions .button,
        .cart-actions a.button {
            margin-top:10px; /* Add some space if they stack on mobile */
        }
    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/products">商品首页</a></li>
            <li class="cart-info"><a href="${pageContext.request.contextPath}/cart">购物车 (<span id="cartItemCount">${empty sessionScope.cart.items ? 0 : sessionScope.cart.totalItems}</span>)</a></li>
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                        <span>欢迎，<a href="${pageContext.request.contextPath}/user_profile"><%= session.getAttribute("username") %></a>！</span>
                        <a href="login?action=logout">退出登录</a>
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
            <h1>我的购物车</h1>

            <c:if test="${not empty cartMessage}">
                <p class="success-message" style="color: #2ecc71; background-color: rgba(46, 204, 113, 0.1); padding: 10px; border-radius: 5px; margin-bottom: 15px; border: 1px solid #2ecc71; text-align: center;">${cartMessage}</p>
            </c:if>
            <c:if test="${not empty cartError}">
                <p class="error-message">${cartError}</p>
            </c:if>

            <c:choose>
                <c:when test="${not empty sessionScope.cart && not empty sessionScope.cart.items}">
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th>商品图片</th>
                                <th class="product-name">商品名称</th>
                                <th class="product-price">单价</th>
                                <th class="product-quantity">数量</th>
                                <th class="product-subtotal">小计</th>
                                <th class="actions">操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="entry" items="${sessionScope.cart.items}">
                                <c:set var="item" value="${entry.value}"/>
                                <c:set var="product" value="${item.product}"/>
                                <tr>
                                    <td>
                                        <img src="${pageContext.request.contextPath}/${not empty product.imageUrl ? product.imageUrl : 'images/default_product.png'}" alt="${product.name}" style="width: 80px; height: 80px; object-fit: cover;">
                                    </td>
                                    <td class="product-name">${product.name}</td>
                                    <td class="product-price">
                                        <fmt:formatNumber value="${product.price}" type="currency"/>
                                    </td>
                                    <td class="product-quantity">
                                        <form action="${pageContext.request.contextPath}/cart" method="post" style="display: inline-flex; align-items: center;">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="productId" value="${product.productId}">
                                            <input type="number" name="quantity" value="${item.quantity}" min="1" max="${product.stockQuantity > 0 ? product.stockQuantity : 1}">
                                            <button type="submit" class="button" style="margin-left: 10px; padding: 8px 12px; font-size:0.9em;">更新</button>
                                        </form>
                                    </td>
                                    <td class="product-subtotal">
                                        <fmt:formatNumber value="${item.subtotal}" type="currency"/>
                                    </td>
                                    <td class="actions">
                                        <form action="${pageContext.request.contextPath}/cart" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="productId" value="${product.productId}">
                                            <button type="submit" class="button" style="background-color: #e74c3c; padding: 8px 12px; font-size:0.9em;">移除</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="cart-summary" style="text-align: right; margin-top: 20px; margin-bottom: 20px;">
                        <h3>总计: <fmt:formatNumber value="${sessionScope.cart.totalAmount}" type="currency"/></h3>
                    </div>

                    <div class="cart-actions" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap;">
                        <a href="${pageContext.request.contextPath}/products" class="button" style="background-color: #555;">继续购物</a>
                        <c:if test="${not empty sessionScope.currentUser}">
                             <a href="${pageContext.request.contextPath}/user/checkout.jsp" class="button">去结算</a>
                        </c:if>
                         <c:if test="${empty sessionScope.currentUser}">
                             <a href="${pageContext.request.contextPath}/user/login.jsp?redirect=cart" class="button">请先登录后结算</a>
                        </c:if>
                    </div>

                </c:when>
                <c:otherwise>
                    <div style="text-align: center; padding: 40px;">
                        <p>您的购物车是空的。快去<a href="${pageContext.request.contextPath}/products">挑选商品</a>吧！</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <footer>
        <p>&copy; 2025 炫酷商城. 版权所有.</p>
    </footer>
</body>
</html>

