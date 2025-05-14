<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品分类 - 炫酷商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        /* Additional styles for category tabs/sections if not in cool_style.css yet */
        .category-tabs {
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 1px solid #444;
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
        }
        .category-tab {
            padding: 10px 20px;
            color: #e0e0e0;
            background-color: #2c2c2c;
            border-radius: 8px;
            text-decoration: none;
            font-weight: bold;
            transition: background-color 0.3s ease, color 0.3s ease, transform 0.2s ease;
        }
        .category-tab:hover,
        .category-tab.active {
            background-color: #c792ea;
            color: #1e1e1e;
            transform: translateY(-2px);
        }
        .category-section {
            margin-bottom: 40px;
            padding: 25px;
            background-color: #1e1e1e; /* Darker section background */
            border-radius: 12px;
            box-shadow: 0 6px 20px rgba(0,0,0,0.35);
        }
        .category-section h2 {
            color: #c792ea; /* Purple for category titles */
            font-size: 2.2em;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #555; /* Enhanced separator */
            text-align: left;
        }
        /* Styles for the success message auto-dismiss */
        .success-message {
            color: #2ecc71;
            background-color: rgba(46, 204, 113, 0.1);
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            border: 1px solid #2ecc71;
            text-align: center;
            opacity: 1;
            transition: opacity 0.5s ease-out;
        }
        .success-message.fade-out {
            opacity: 0;
        }
    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/products">商品首页</a></li>
            <li class="cart-info"><a href="${pageContext.request.contextPath}/cart">购物车 (<span id="cartItemCount">${empty sessionScope.cart ? 0 : sessionScope.cart.totalItems}</span>)</a></li>
<%--            <li class="cart-info"><a onclick="submitCartPost(event)" >购物车adsad (<span id="cartItemCount">${empty sessionScope.cart.items ? 0 : sessionScope.cart.totalItems}</span>)</a></li>--%>

            <!-- 隐藏的 POST 表单,目的时为了在不影响页面的情况下，发起post请求 -->
            <form id="cartPostForm" action="${pageContext.request.contextPath}/cart" method="post" style="display: none;"></form>


            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <li><a href="#">欢迎, ${sessionScope.currentUser.username}!</a></li>
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
        <h1 style="text-align: center; font-size: 2.8em; margin-bottom: 40px;">探索我们的商品</h1>

        <c:if test="${not empty cartMessage}">
            <p class="success-message" id="cartSuccessMessage">${cartMessage}</p>
        </c:if>
        <c:if test="${not empty cartError}">
            <p class="error-message">${cartError}</p>
        </c:if>

        <!-- Category Tabs Navigation -->
        <div class="category-tabs">
            <a href="${pageContext.request.contextPath}/products?category=all" class="category-tab ${empty param.category || param.category == 'all' ? 'active' : ''}">所有商品</a>
            <c:forEach var="cat" items="${distinctCategories}"> <%-- distinctCategories should be provided by servlet --%>
                <a href="${pageContext.request.contextPath}/products?category=${cat}" class="category-tab ${param.category == cat ? 'active' : ''}">${cat}</a>
            </c:forEach>
        </div>

        <c:choose>
            <c:when test="${not empty productList && productList.size() > 0}">
                <div class="product-grid">
                    <c:forEach var="product" items="${productList}">  <%-- productList is now filtered by servlet based on param.category --%>
                        <div class="card product-item">
                            <img src="${pageContext.request.contextPath}/${not empty product.imageUrl ? product.imageUrl : 'images/default_product.png'}" alt="${product.name}" style="width:100%; height: 220px; object-fit: cover; border-bottom: 1px solid #444; border-top-left-radius: 10px; border-top-right-radius: 10px;">
                            <div style="padding: 20px;">
                                <h3 style="font-size: 1.4em; margin-bottom: 8px;">${product.name}</h3>
                                <p style="font-size: 0.9em; color: #aaa; margin-bottom: 12px;">分类: ${product.category}</p>
                                <p style="font-size: 0.95em; color: #ccc; margin-bottom: 18px; min-height: 70px; line-height: 1.5;">${product.description}</p>
                                <p style="font-size: 1.4em; font-weight: bold; color: #c792ea; margin-bottom: 15px;">
                                    <fmt:setLocale value="zh_CN"/>
                                    <fmt:formatNumber value="${product.price}" type="currency"/>
                                </p>
                                <p style="font-size: 0.9em; color: #aaa; margin-bottom: 20px;">库存: ${product.stockQuantity > 0 ? product.stockQuantity : '无货'}</p>
                                <form action="${pageContext.request.contextPath}/cart" method="post" style="margin-top: auto;">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${product.productId}">
                                    <input type="hidden" name="quantity" value="1">
                                    <button type="submit" ${product.stockQuantity <= 0 ? 'disabled' : ''} style="width: 100%; padding: 12px 20px; font-size: 1.1em;">
                                        ${product.stockQuantity > 0 ? '加入购物车' : '暂时缺货'}
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card" style="text-align: center; padding: 50px; background-color: #1e1e1e;">
                    <p style="font-size: 1.2em; color: #bbb;">此分类下暂无商品，或所有商品已售罄。看看其他分类吧！</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <footer>
        <p>&copy; ${currentYear} 炫酷商城. 版权所有.</p>
    </footer>
    <c:set var="currentYear"><jsp:useBean id="date" class="java.util.Date" /><fmt:formatDate value="${date}" pattern="yyyy" /></c:set>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const cartSuccessMessage = document.getElementById('cartSuccessMessage');
            if (cartSuccessMessage) {
                setTimeout(function() {
                    cartSuccessMessage.classList.add('fade-out');
                }, 3000); // Start fade out after 3 seconds
                setTimeout(function() {
                    if (cartSuccessMessage.parentNode) {
                         cartSuccessMessage.parentNode.removeChild(cartSuccessMessage);
                    }
                }, 3500); // Remove from DOM after fade out (0.5s transition)
            }
        });
        function submitCartPost(event) {
            event.preventDefault(); // 阻止默认的 GET 请求
            document.getElementById("cartPostForm").submit(); // 提交 POST 表单
        }
    </script>
</body>
</html>

