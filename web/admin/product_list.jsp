<%@ page contentType="text/html;charset=UTF-8"  %>
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
    <title>商品管理 - 炫酷商城后台</title>
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
        .admin-table img.product-thumbnail {
            width: 60px;
            height: 60px;
            border-radius: 4px;
            object-fit: cover;
            margin-right: 10px;
            border: 1px solid #555;
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
         .btn-admin-action {
             margin-right: 5px; 
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>炫酷商城后台</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">&#128200; 仪表盘</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/users">&#128101; 用户管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/products" class="active">&#128722; 商品管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/orders">&#128230; 订单管理</a></li>
            <li><a href="${pageContext.request.contextPath}/login?action=logout">&#128682; 退出登录</a></li>
        </ul>
    </div>

    <div class="main-content fade-in">
        <header class="admin-header">
            <h1>商品管理</h1>
            <div class="user-info">
                <span>管理员: ${sessionScope.currentUser.username} | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
            </div>
        </header>

        <div class="content-area">
            <div class="table-actions">
                <a href="${pageContext.request.contextPath}/admin/product_form.jsp?formAction=add" class="button">添加新商品</a>
                <form action="${pageContext.request.contextPath}/admin/products" method="get" class="search-form">
                    <input type="hidden" name="action" value="search">
                    <input type="text" name="searchTerm" placeholder="搜索商品..." value="${param.searchTerm}">
                    <button type="submit" class="button">搜索</button>
                </form>
            </div>
            
            <c:if test="${not empty message}">
                <div class="alert-cool success">${message}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert-cool danger">${errorMessage}</div>
            </c:if>

            <form id="bulkDeleteForm" action="${pageContext.request.contextPath}/admin/products" method="post">
                <input type="hidden" name="action" value="bulkDelete">
                <div style="overflow-x: auto;">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th><input type="checkbox" id="selectAllProducts" style="accent-color: #c792ea;"></th>
                                <th>ID</th>
                                <th>图片</th>
                                <th>名称</th>
                                <th>分类</th>
                                <th>价格</th>
                                <th>库存</th>
                                <th>创建日期</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="product" items="${productList}">
                                <tr>
                                    <td><input type="checkbox" name="productIds" value="${product.productId}" style="accent-color: #c792ea;"></td>
                                    <td>${product.productId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty product.imageUrl}">
                                                <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="商品图片" class="product-thumbnail">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/images/default_product.png" alt="默认图片" class="product-thumbnail">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${product.name}"/></td>
                                    <td><c:out value="${product.category}"/></td>
                                    <td><fmt:formatNumber value="${product.price}" type="currency"/></td>
                                    <td>${product.stockQuantity}</td>
                                    <td><fmt:formatDate value="${product.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/products?action=edit&productId=${product.productId}" class="button btn-admin-action btn-admin-edit">编辑</a>
                                        <a href="${pageContext.request.contextPath}/admin/products?action=delete&productId=${product.productId}" 
                                           class="button btn-admin-action btn-admin-delete"
                                           onclick="return confirm('确定要删除商品 \'${product.name}\' 吗?');">删除</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty productList}">
                                <tr>
                                    <td colspan="9" style="text-align:center; padding: 20px;">未找到商品数据。</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                <c:if test="${not empty productList}">
                    <button type="submit" class="button" style="background-color: #e74c3c; margin-top: 20px;" onclick="return confirm('确定要批量删除选中的商品吗?');">批量删除选中</button>
                </c:if>
            </form>
        </div>
    </div>
    <script>
        document.getElementById('selectAllProducts').onclick = function() {
            var checkboxes = document.getElementsByName('productIds');
            for (var checkbox of checkboxes) {
                checkbox.checked = this.checked;
            }
        }
    </script>
</body>
</html>

