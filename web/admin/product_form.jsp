<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.wanggong232.wangyifei.shopping02.dao.ProductDao" %>
<%@ page import="com.wanggong232.wangyifei.shopping02.model.Category" %>
<%@ page import="com.wanggong232.wangyifei.shopping02.model.SubCategory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.currentUser || sessionScope.currentUser.role ne 'ADMIN'}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?error=AdminAccessRequired"/>
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>添加新商品 - 炫酷商城后台</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            background-color: #121212;
            margin: 0;
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
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #b0b0b0;
            font-weight: 500;
        }
        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%;
            padding: 12px;
            background-color: #333;
            border: 1px solid #555;
            border-radius: 5px;
            color: #e0e0e0;
            font-size: 1em;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
            box-sizing: border-box;
        }
        .form-group input:focus,
        .form-group textarea:focus,
        .form-group select:focus {
            outline: none;
            border-color: #c792ea;
            box-shadow: 0 0 0 2px rgba(199, 146, 234, 0.2);
        }
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        .form-group select {
            cursor: pointer;
        }
        .form-group select option {
            background-color: #333;
            color: #e0e0e0;
        }
        .form-row {
            display: flex;
            gap: 20px;
        }
        .form-row .form-group {
            flex: 1;
        }

        /* 图片上传相关样式 */
        .image-upload-section {
            background-color: #2a2a2a;
            border: 2px dashed #555;
            border-radius: 8px;
            padding: 20px;
            margin-top: 10px;
            transition: border-color 0.3s ease;
        }
        .image-upload-section:hover {
            border-color: #c792ea;
        }
        .upload-options {
            display: flex;
            gap: 20px;
            align-items: flex-start;
        }
        .upload-option {
            flex: 1;
        }
        .upload-option h4 {
            color: #c792ea;
            margin: 0 0 10px 0;
            font-size: 1em;
        }
        .file-upload-area {
            border: 1px solid #555;
            border-radius: 5px;
            padding: 15px;
            text-align: center;
            background-color: #333;
            transition: background-color 0.3s ease;
        }
        .file-upload-area:hover {
            background-color: #3a3a3a;
        }
        .file-upload-area input[type="file"] {
            width: 100%;
            padding: 8px;
            background-color: transparent;
            border: none;
            color: #e0e0e0;
        }
        .upload-hint {
            color: #aaa;
            font-size: 0.9em;
            margin-top: 8px;
        }
        .image-preview {
            margin-top: 15px;
            text-align: center;
        }
        .image-preview img {
            max-width: 200px;
            max-height: 200px;
            border-radius: 8px;
            border: 1px solid #555;
        }
        .divider {
            text-align: center;
            color: #666;
            margin: 15px 0;
            position: relative;
        }
        .divider::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 0;
            right: 0;
            height: 1px;
            background-color: #444;
        }
        .divider span {
            background-color: #2a2a2a;
            padding: 0 15px;
        }

        .submit-btn {
            width: 100%;
            padding: 15px;
            background: linear-gradient(135deg, #c792ea, #b47eda);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-top: 10px;
        }
        .submit-btn:hover {
            background: linear-gradient(135deg, #b47eda, #a66bc9);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(199, 146, 234, 0.4);
        }
        .btn-cancel {
            display: block;
            text-align: center;
            margin-top: 15px;
            color: #c792ea;
            text-decoration: none;
            padding: 12px 15px;
            border: 1px solid #c792ea;
            border-radius: 5px;
            transition: background-color 0.3s ease, color 0.3s ease;
        }
        .btn-cancel:hover {
            background-color: #c792ea;
            color: #1e1e1e;
        }
        .alert-cool {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            border-left: 4px solid;
        }
        .alert-cool.success {
            background-color: rgba(46, 204, 113, 0.2);
            border-color: #2ecc71;
            color: #f0f0f0;
        }
        .alert-cool.danger {
            background-color: rgba(231, 76, 60, 0.2);
            border-color: #e74c3c;
            color: #f0f0f0;
        }
        .loading-indicator {
            display: none;
            color: #c792ea;
            font-size: 0.9em;
            margin-top: 5px;
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
            .form-row {
                flex-direction: column;
                gap: 0;
            }
            .upload-options {
                flex-direction: column;
                gap: 15px;
            }
            .admin-header {
                flex-direction: column;
                gap: 10px;
                text-align: center;
            }
        }
    </style>
</head>
<body>
<div class="sidebar">
    <h2>🛍️ 炫酷商城后台</h2>
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">📊 仪表盘</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">👥 用户管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/products" class="active">📦 商品管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/orders">📋 订单管理</a></li>
        <li><a href="${pageContext.request.contextPath}/login?action=logout">🚪 退出登录</a></li>
    </ul>
</div>

<div class="main-content">
    <header class="admin-header">
        <h1>✨ 添加新商品</h1>
        <div class="user-info">
            <span>管理员: ${sessionScope.currentUser.username} | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
        </div>
    </header>

    <div class="content-area">
        <div class="form-container">
            <h2>📝 填写商品信息</h2>

            <c:if test="${not empty errorMessage}">
                <div class="alert-cool danger">${errorMessage}</div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert-cool success">${successMessage}</div>
            </c:if>

            <form action="addProduct.jsp" method="post" enctype="multipart/form-data" id="productForm">
                <div class="form-group">
                    <label for="name">🏷️ 商品名称:</label>
                    <input type="text" id="name" name="name" placeholder="请输入商品名称" required>
                </div>

                <div class="form-group">
                    <label for="description">📝 商品描述:</label>
                    <textarea id="description" name="description" placeholder="请详细描述商品特点和功能" required></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="category">📂 商品大分类:</label>
                        <select id="category" name="categoryId" onchange="loadSubCategories()" required>
                            <option value="">请选择大分类</option>
                            <%
                                try {
                                    ProductDao productDao = new ProductDao();
                                    List<Category> categories = productDao.getAllCategories();
                                    for (Category category : categories) {
                            %>
                            <option value="<%= category.getCategoryId() %>"><%= category.getCategoryName() %></option>
                            <%
                                    }
                                } catch (Exception e) {
                                    out.println("<option value=''>加载分类失败</option>");
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="subCategory">🏷️ 商品小分类:</label>
                        <select id="subCategory" name="subCategoryId" required>
                            <option value="">请先选择大分类</option>
                        </select>
                        <div class="loading-indicator" id="loadingIndicator">正在加载小分类...</div>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="price">💰 商品价格 (元):</label>
                        <input type="number" id="price" name="price" step="0.01" min="0.01" placeholder="0.00" required>
                    </div>

                    <div class="form-group">
                        <label for="stockQuantity">📦 库存数量:</label>
                        <input type="number" id="stockQuantity" name="stockQuantity" min="0" placeholder="0" required>
                    </div>
                </div>

                <div class="form-group">
                    <label>🖼️ 商品图片:</label>
                    <div class="image-upload-section">
                        <div class="upload-options">
                            <div class="upload-option">
                                <h4>📁 本地上传</h4>
                                <div class="file-upload-area">
                                    <input type="file" id="imageFile" name="imageFile" accept="image/*" onchange="previewImage(this)">
                                    <div class="upload-hint">支持 JPG, PNG, GIF, WebP 格式</div>
                                </div>
                            </div>

                            <div class="divider">
                                <span>或</span>
                            </div>

                            <div class="upload-option">
                                <h4>🔗 图片路径</h4>
                                <input type="text" id="imageUrl" name="imageUrl" placeholder="例如: images/products/product1.jpg" onchange="previewImageUrl(this)">
                                <div class="upload-hint">输入相对于网站根目录的图片路径</div>
                            </div>
                        </div>

                        <div class="image-preview" id="imagePreview" style="display: none;">
                            <h4 style="color: #c792ea; margin-bottom: 10px;">📷 图片预览:</h4>
                            <img id="previewImg" src="/placeholder.svg" alt="图片预览">
                        </div>
                    </div>
                </div>

                <button type="submit" class="submit-btn">✅ 添加商品</button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn-cancel">❌ 取消操作</a>
            </form>
        </div>
    </div>
</div>

<script>
    function loadSubCategories() {
        var categoryId = document.getElementById("category").value;
        var subCategorySelect = document.getElementById("subCategory");
        var loadingIndicator = document.getElementById("loadingIndicator");

        // 清空小分类选项
        subCategorySelect.innerHTML = '<option value="">正在加载...</option>';
        subCategorySelect.disabled = true;
        loadingIndicator.style.display = 'block';

        if (!categoryId) {
            subCategorySelect.innerHTML = '<option value="">请先选择大分类</option>';
            subCategorySelect.disabled = false;
            loadingIndicator.style.display = 'none';
            return;
        }

        // 使用 AJAX 获取小分类数据
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "getSubCategories.jsp?categoryId=" + categoryId, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4) {
                loadingIndicator.style.display = 'none';
                subCategorySelect.disabled = false;

                if (xhr.status == 200) {
                    try {
                        var subCategories = JSON.parse(xhr.responseText);
                        subCategorySelect.innerHTML = '<option value="">请选择小分类</option>';

                        for (var i = 0; i < subCategories.length; i++) {
                            var option = document.createElement("option");
                            option.value = subCategories[i].subCategoryId;
                            option.text = subCategories[i].subCategoryName;
                            subCategorySelect.add(option);
                        }

                        if (subCategories.length === 0) {
                            subCategorySelect.innerHTML = '<option value="">该分类下暂无小分类</option>';
                        }
                    } catch (e) {
                        console.error('解析JSON失败:', e);
                        subCategorySelect.innerHTML = '<option value="">加载失败，请重试</option>';
                    }
                } else {
                    subCategorySelect.innerHTML = '<option value="">加载失败，请重试</option>';
                }
            }
        };
        xhr.onerror = function() {
            loadingIndicator.style.display = 'none';
            subCategorySelect.disabled = false;
            subCategorySelect.innerHTML = '<option value="">网络错误，请重试</option>';
        };
        xhr.send();
    }

    // 预览上传的图片
    function previewImage(input) {
        var imageUrl = document.getElementById('imageUrl');
        var preview = document.getElementById('imagePreview');
        var previewImg = document.getElementById('previewImg');

        if (input.files && input.files[0]) {
            // 清空图片路径输入框
            imageUrl.value = '';

            var reader = new FileReader();
            reader.onload = function(e) {
                previewImg.src = e.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(input.files[0]);
        } else {
            preview.style.display = 'none';
        }
    }

    // 预览图片路径
    function previewImageUrl(input) {
        var imageFile = document.getElementById('imageFile');
        var preview = document.getElementById('imagePreview');
        var previewImg = document.getElementById('previewImg');

        if (input.value.trim()) {
            // 清空文件上传
            imageFile.value = '';

            // 构建完整的图片URL
            var fullUrl = '${pageContext.request.contextPath}/' + input.value;
            previewImg.src = fullUrl;
            preview.style.display = 'block';

            // 检查图片是否能正常加载
            previewImg.onerror = function() {
                preview.style.display = 'none';
            };
        } else {
            preview.style.display = 'none';
        }
    }

    // 表单提交前验证
    document.getElementById('productForm').addEventListener('submit', function(e) {
        var categoryId = document.getElementById('category').value;
        var subCategoryId = document.getElementById('subCategory').value;
        var imageFile = document.getElementById('imageFile').files[0];
        var imageUrl = document.getElementById('imageUrl').value.trim();

        if (!categoryId) {
            alert('请选择商品大分类！');
            e.preventDefault();
            return false;
        }

        if (!subCategoryId) {
            alert('请选择商品小分类！');
            e.preventDefault();
            return false;
        }

        if (!imageFile && !imageUrl) {
            alert('请上传商品图片或输入图片路径！');
            e.preventDefault();
            return false;
        }

        // 显示提交中状态
        var submitBtn = document.querySelector('.submit-btn');
        submitBtn.innerHTML = '⏳ 正在添加...';
        submitBtn.disabled = true;
    });

    // 页面加载完成后的初始化
    document.addEventListener('DOMContentLoaded', function() {
        // 为数字输入框添加格式化
        var priceInput = document.getElementById('price');
        priceInput.addEventListener('blur', function() {
            if (this.value) {
                this.value = parseFloat(this.value).toFixed(2);
            }
        });
    });
</script>
</body>
</html>