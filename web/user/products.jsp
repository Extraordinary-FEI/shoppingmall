<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>商品分类 - 购物商城</title>
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
            padding: 12px 20px;
            color: #e0e0e0;
            background-color: #2c2c2c;
            border-radius: 8px;
            text-decoration: none;
            font-weight: bold;
            transition: background-color 0.3s ease, color 0.3s ease, transform 0.2s ease;
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.95em;
        }
        .category-tab:hover,
        .category-tab.active {
            background-color: #c792ea;
            color: #1e1e1e;
            transform: translateY(-2px);
        }
        .category-tab .icon {
            font-size: 1.1em;
            display: inline-block;
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

        /* Search box styles */
        .search-container {
            margin: 30px 0;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 15px;
        }
        .search-form {
            display: flex;
            align-items: center;
            background-color: #2c2c2c;
            border-radius: 12px;
            padding: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.3);
            transition: box-shadow 0.3s ease;
            max-width: 600px;
            width: 100%;
        }
        .search-form:focus-within {
            box-shadow: 0 6px 25px rgba(199, 146, 234, 0.3);
        }
        .search-input {
            flex: 1;
            padding: 15px 20px;
            background: transparent;
            border: none;
            color: #e0e0e0;
            font-size: 1.1em;
            outline: none;
        }
        .search-input::placeholder {
            color: #888;
        }
        .search-button {
            padding: 15px 25px;
            background-color: #c792ea;
            color: #1e1e1e;
            border: none;
            border-radius: 8px;
            font-weight: bold;
            font-size: 1em;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .search-button:hover {
            background-color: #b47eda;
            transform: translateY(-1px);
        }
        .search-button:active {
            transform: translateY(0);
        }
        .clear-search {
            margin-left: 10px;
            padding: 8px 15px;
            background-color: #555;
            color: #e0e0e0;
            border: none;
            border-radius: 6px;
            font-size: 0.9em;
            cursor: pointer;
            transition: background-color 0.3s ease;
            text-decoration: none;
        }
        .clear-search:hover {
            background-color: #666;
        }

        /* Search results info */
        .search-info {
            text-align: center;
            margin-bottom: 20px;
            color: #aaa;
            font-size: 1.1em;
        }
        .search-info .highlight {
            color: #c792ea;
            font-weight: bold;
        }

        @media (max-width: 768px) {
            .search-container {
                flex-direction: column;
                gap: 10px;
            }
            .search-form {
                max-width: 100%;
            }
            .clear-search {
                margin-left: 0;
                margin-top: 10px;
            }
            .category-tab {
                padding: 10px 16px;
                font-size: 0.9em;
            }
            .category-tab .icon {
                font-size: 1em;
            }
        }
    </style>
</head>
<body>

<nav>
    <ul>
        <li><a href="${pageContext.request.contextPath}/products">商品首页</a></li>
        <li class="cart-info"><a href="${pageContext.request.contextPath}/cart">购物车 (<span id="cartItemCount">${empty sessionScope.cart ? 0 : sessionScope.cart.totalItems}</span>)</a></li>

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

    <!-- Search Container -->
    <div class="search-container">
        <form class="search-form" action="${pageContext.request.contextPath}/products" method="get">
            <input type="text"
                   name="search"
                   class="search-input"
                   placeholder="搜索商品名称或描述..."
                   value="${param.search}"
                   maxlength="100">
            <!-- Preserve category filter when searching -->
            <c:if test="${not empty param.category && param.category != 'all'}">
                <input type="hidden" name="category" value="${param.category}">
            </c:if>
            <button type="submit" class="search-button">🔍 搜索</button>
        </form>
        <c:if test="${not empty param.search}">
            <a href="${pageContext.request.contextPath}/products${not empty param.category && param.category != 'all' ? '?category='.concat(param.category) : ''}"
               class="clear-search">清除搜索</a>
        </c:if>
    </div>

    <!-- Search Results Info -->
    <c:if test="${not empty param.search}">
        <div class="search-info">
            搜索 "<span class="highlight">${param.search}</span>" 的结果
            <c:if test="${not empty param.category && param.category != 'all'}">
                在分类 "<span class="highlight">${param.category}</span>" 中
            </c:if>
            - 找到 <span class="highlight">${not empty productList ? productList.size() : 0}</span> 个商品
        </div>
    </c:if>

    <c:if test="${not empty cartMessage}">
        <p class="success-message" id="cartSuccessMessage">${cartMessage}</p>
    </c:if>
    <c:if test="${not empty cartError}">
        <p class="error-message">${cartError}</p>
    </c:if>

    <!-- Category Tabs Navigation with Icons -->
    <div class="category-tabs">
        <a href="${pageContext.request.contextPath}/products?category=all${not empty param.search ? '&search='.concat(param.search) : ''}"
           class="category-tab ${empty param.category || param.category == 'all' ? 'active' : ''}">
            <span class="icon">🏷️</span>
            <span>所有商品</span>
        </a>
        <c:forEach var="cat" items="${distinctCategories}">
            <a href="${pageContext.request.contextPath}/products?category=${cat}${not empty param.search ? '&search='.concat(param.search) : ''}"
               class="category-tab ${param.category == cat ? 'active' : ''}">
                <c:choose>
                    <c:when test="${cat == '图书音像' || cat == '图书' || cat == '音像' || cat == '书籍'}">
                        <span class="icon">📚</span>
                    </c:when>
                    <c:when test="${cat == '宠物' || cat == '宠物用品'}">
                        <span class="icon">🐾</span>
                    </c:when>
                    <c:when test="${cat == '电子类' || cat == '电子产品' || cat == '数码' || cat == '电子设备'}">
                        <span class="icon">📱</span>
                    </c:when>
                    <c:when test="${cat == '化妆品类' || cat == '化妆品' || cat == '美妆' || cat == '护肤'}">
                        <span class="icon">💄</span>
                    </c:when>
                    <c:when test="${cat == '运动户外' || cat == '运动' || cat == '户外' || cat == '体育'}">
                        <span class="icon">⚽</span>
                    </c:when>
                    <c:when test="${cat == '智能家居' || cat == '智能设备' || cat == '家电'}">
                        <span class="icon">🏠</span>
                    </c:when>
                    <c:when test="${cat == '服装配饰类' || cat == '服装' || cat == '配饰' || cat == '时尚'}">
                        <span class="icon">👕</span>
                    </c:when>
                    <c:when test="${cat == '家居类' || cat == '家居' || cat == '家具' || cat == '装饰'}">
                        <span class="icon">🛋️</span>
                    </c:when>
                    <c:when test="${cat == '食品' || cat == '食物' || cat == '零食' || cat == '饮料'}">
                        <span class="icon">🍎</span>
                    </c:when>
                    <c:when test="${cat == '汽车' || cat == '汽车用品' || cat == '车载'}">
                        <span class="icon">🚗</span>
                    </c:when>
                    <c:when test="${cat == '母婴' || cat == '婴儿' || cat == '儿童'}">
                        <span class="icon">👶</span>
                    </c:when>
                    <c:when test="${cat == '玩具' || cat == '游戏'}">
                        <span class="icon">🧸</span>
                    </c:when>
                    <c:when test="${cat == '办公' || cat == '文具' || cat == '办公用品'}">
                        <span class="icon">📝</span>
                    </c:when>
                    <c:when test="${cat == '健康' || cat == '医疗' || cat == '保健'}">
                        <span class="icon">💊</span>
                    </c:when>
                    <c:when test="${cat == '工具' || cat == '五金' || cat == '维修'}">
                        <span class="icon">🔧</span>
                    </c:when>
                    <c:otherwise>
                        <span class="icon">📦</span>
                    </c:otherwise>
                </c:choose>
                <span>${cat}</span>
            </a>
        </c:forEach>
    </div>

    <c:choose>
        <c:when test="${not empty productList && productList.size() > 0}">
            <div class="product-grid">
                <c:forEach var="product" items="${productList}">
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
                <c:choose>
                    <c:when test="${not empty param.search}">
                        <p style="font-size: 1.2em; color: #bbb;">没有找到包含 "<span style="color: #c792ea;">${param.search}</span>" 的商品</p>
                        <p style="font-size: 1em; color: #888; margin-top: 15px;">试试其他关键词或浏览所有商品</p>
                    </c:when>
                    <c:otherwise>
                        <p style="font-size: 1.2em; color: #bbb;">此分类下暂无商品，或所有商品已售罄。看看其他分类吧！</p>
                    </c:otherwise>
                </c:choose>
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
            }, 3000);
            setTimeout(function() {
                if (cartSuccessMessage.parentNode) {
                    cartSuccessMessage.parentNode.removeChild(cartSuccessMessage);
                }
            }, 3500);
        }

        const searchInput = document.querySelector('.search-input');
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('search') && searchInput) {
            searchInput.focus();
        }

        if (searchInput) {
            searchInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.target.closest('form').submit();
                }
            });
        }
    });

    function submitCartPost(event) {
        event.preventDefault();
        document.getElementById("cartPostForm").submit();
    }
</script>
</body>
</html>