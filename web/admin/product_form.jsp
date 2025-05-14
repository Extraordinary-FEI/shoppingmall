<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty sessionScope.currentUser || sessionScope.currentUser.role ne 'ADMIN'}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?error=AdminAccessRequired"/>
</c:if>

<%-- Set the formAction from request parameter if not already set --%>
<c:if test="${empty formAction}">
    <c:set var="formAction" value="${param.formAction}" />
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${formAction == 'add' ? '添加新商品' : '编辑商品'} - 炫酷商城后台</title>
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
        .form-container {
            max-width: 700px;
            margin: 0 auto;
        }
        .form-container h2 {
            margin-top: 0;
            color: #e0e0e0;
            font-size: 1.6em;
            margin-bottom: 25px;
            text-align: center;
        }
        .form-group label {
            color: #b0b0b0;
        }
        .current-image {
            max-width: 150px;
            max-height: 150px;
            border-radius: 8px;
            margin-top: 10px;
            display: block;
            border: 1px solid #555;
        }
        .btn-cancel {
            display: inline-block;
            margin-top: 15px;
            color: #c792ea;
            text-decoration: none;
            padding: 10px 15px;
            border: 1px solid #c792ea;
            border-radius: 5px;
            transition: background-color 0.3s ease, color 0.3s ease;
        }
        .btn-cancel:hover {
            background-color: #c792ea;
            color: #1e1e1e;
        }
        .alert-cool.danger {
            background-color: rgba(231, 76, 60, 0.2);
            border-color: #e74c3c;
            color: #f0f0f0;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
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
            <h1>${formAction == 'add' ? '添加新商品' : '编辑商品'}</h1>
            <div class="user-info">
                <span>管理员: ${sessionScope.currentUser.username} | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
            </div>
        </header>

        <div class="content-area">
            <div class="form-container">
                <h2>${formAction == 'add' ? '填写商品信息' : '更新商品信息'}</h2>

                <c:if test="${not empty errorMessage}">
                    <div class="alert-cool danger">${errorMessage}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="${formAction}">
                    <c:if test="${formAction == 'edit'}">
                        <input type="hidden" name="productId" value="${product.productId}">
                        <input type="hidden" name="existingImageUrl" value="${product.imageUrl}">
                    </c:if>

                    <div class="form-group">
                        <label for="name">商品名称:</label>
                        <input type="text" id="name" name="name" value="<c:out value='${product.name}'/>" required>
                    </div>

                    <div class="form-group">
                        <label for="description">商品描述:</label>
                        <textarea id="description" name="description" required><c:out value='${product.description}'/></textarea>
                    </div>

                    <div class="form-group">
                        <label for="category">商品分类:</label>
                        <input type="text" id="category" name="category" value="<c:out value='${product.category}'/>" required>
                    </div>

                    <div class="form-group">
                        <label for="price">价格 (元):</label>
                        <input type="number" id="price" name="price" value="${product.price}" step="0.01" min="0.01" required>
                    </div>

                    <div class="form-group">
                        <label for="stockQuantity">库存数量:</label>
                        <input type="number" id="stockQuantity" name="stockQuantity" value="${product.stockQuantity}" min="0" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="imageFile">商品图片 ${formAction == 'edit' ? '(不选择则保留原图)' : ''}:</label>
                        <input type="file" id="imageFile" name="imageFile" accept="image/jpeg, image/png, image/gif, image/webp" style="background-color: #333; color:#f0f0f0; padding: 10px; border-radius:5px; border: 1px solid #555;">
                        <small style="color: #aaa;">支持 JPG, PNG, GIF, WebP 格式。最大 10MB。</small>
                        <c:if test="${formAction == 'edit' && not empty product.imageUrl}">
                            <p style="margin-top:10px; color: #aaa;">当前图片: <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="商品图片" class="current-image"></p>
                        </c:if>
                    </div>

                    <button type="submit" class="button" style="width: 100%; padding: 12px;">${formAction == 'add' ? '添加商品' : '保存更改'}</button>
                    <a href="${pageContext.request.contextPath}/admin/products" class="btn-cancel" style="display: block; text-align: center; width: calc(100% - 30px); margin-top:15px;">取消</a>
                </form>
            </div>
        </div>
    </div>
</body>
</html>

