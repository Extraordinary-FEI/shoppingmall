<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    <title>${formAction == 'add' ? '添加新用户' : '编辑用户'} - 炫酷商城后台</title>
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
            color: #e0e0e0; /* Light text for form title */
            font-size: 1.6em;
            margin-bottom: 25px;
            text-align: center;
        }
        .form-group label {
            color: #b0b0b0; /* Lighter label color */
        }
        .btn-cancel {
            display: inline-block;
            margin-top: 15px;
            color: #c792ea; /* Purple for cancel link */
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
        #avatarPreviewContainer {
            margin-top: 10px;
            text-align: center; /* Center the preview */
        }
        #avatarPreview {
            max-width: 100px; 
            max-height: 100px; 
            border-radius: 50%; 
            border: 2px solid #555;
            object-fit: cover; /* Ensures the image covers the area nicely */
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>炫酷商城后台</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">&#128200; 仪表盘</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/users" class="active">&#128101; 用户管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/products">&#128722; 商品管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/orders">&#128230; 订单管理</a></li>
            <li><a href="${pageContext.request.contextPath}/login?action=logout">&#128682; 退出登录</a></li>
        </ul>
    </div>

    <div class="main-content fade-in">
        <header class="admin-header">
            <h1>${formAction == 'add' ? '添加新用户' : '编辑用户'}</h1>
            <div class="user-info">
                <span>管理员: ${sessionScope.currentUser.username} | <a href="${pageContext.request.contextPath}/login?action=logout">退出</a></span>
            </div>
        </header>

        <div class="content-area">
            <div class="form-container">
                <h2>${formAction == 'add' ? '创建新用户账户' : '更新用户信息'}</h2>
                <c:if test="${not empty errorMessage}">
                    <div class="alert-cool danger">${errorMessage}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/admin/users" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="${formAction}">
                    <c:if test="${formAction == 'edit'}">
                        <input type="hidden" name="userId" value="${user.userId}">
                        <input type="hidden" name="existingAvatarPath" value="${user.avatarPath}">
                    </c:if>

                    <div class="form-group">
                        <label for="username">用户名:</label>
                        <input type="text" id="username" name="username" value="<c:out value='${user.username}'/>" required
                               ${formAction == 'edit' && sessionScope.currentUser.userId == user.userId ? 'readonly' : ''} >
                        <c:if test="${formAction == 'edit' && sessionScope.currentUser.userId == user.userId}">
                            <small style="color: #aaa;">当前登录管理员的用户名不可修改。</small>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="email">邮箱:</label>
                        <input type="email" id="email" name="email" value="<c:out value='${user.email}'/>" required>
                    </div>

                    <div class="form-group">
                        <label for="password">密码:</label>
                        <input type="password" id="password" name="password" 
                               placeholder="${formAction == 'edit' ? '留空则不修改密码' : ''}" 
                               ${formAction == 'add' ? 'required' : ''}>
                         <c:if test="${formAction == 'edit'}">
                            <small style="color: #aaa;">如需修改密码，请输入新密码。否则请留空。</small>
                        </c:if>
                    </div>

                    <div class="form-group">
                        <label for="fullName">全名:</label>
                        <input type="text" id="fullName" name="fullName" value="<c:out value='${user.fullName}'/>">
                    </div>

                    <div class="form-group">
                        <label for="address">地址:</label>
                        <textarea id="address" name="address"><c:out value='${user.address}'/></textarea>
                    </div>

                    <div class="form-group">
                        <label for="phone">电话:</label>
                        <input type="text" id="phone" name="phone" value="<c:out value='${user.phone}'/>">
                    </div>
                    
                    <div class="form-group">
                        <label for="role">角色:</label>
                        <select id="role" name="role" required 
                                ${formAction == 'edit' && sessionScope.currentUser.userId == user.userId && user.role == 'ADMIN' ? 'disabled' : ''} >
                            <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>用户 (USER)</option>
                            <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>管理员 (ADMIN)</option>
                        </select>
                        <c:if test="${formAction == 'edit' && sessionScope.currentUser.userId == user.userId && user.role == 'ADMIN'}">
                            <input type="hidden" name="role" value="ADMIN"> <!-- Ensure role is submitted even if disabled -->
                            <small style="color: #aaa;">当前登录的管理员不能修改自己的角色。</small>
                        </c:if>
                    </div>


<%--                    <div class="form-group">
                        <label for="avatar">头像 (可选):</label>
                        <input type="file" id="avatar" name="avatar" accept="image/*" onchange="previewAvatar(event)" style="background-color: #333; color:#f0f0f0; padding: 10px; border-radius:5px; border: 1px solid #555;">

                        <div id="avatarPreviewContainer">
                            <c:choose>
                                <c:when test="${not empty user.avatarPath}">
                                    <img id="avatarPreview" src="${pageContext.request.contextPath}/${user.avatarPath}" alt="用户头像">
                                </c:when>
                                <c:otherwise>
                                    <!-- 默认头像占位符（等待 API 返回） -->
                                    <img id="avatarPreview" src="" alt="头像预览" style="display:none;">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <small style="color: #aaa;">上传新头像将会替换旧头像 (如果存在)。</small>
                    </div>--%>
                    <%--                    <div class="form-group">--%>
                         <label for="avatar">头像 (可选):</label>
                         <input type="file" id="avatar" name="avatar" accept="image/*" onchange="previewAvatar(event)" style="background-color: #333; color:#f0f0f0; padding: 10px; border-radius:5px; border: 1px solid #555;">
                         <div id="avatarPreviewContainer">
                            <c:choose>
                                <c:when test="${not empty user.avatarPath}">
                                    <img id="avatarPreview" src="${pageContext.request.contextPath}/${user.avatarPath}" alt="用户头像">
                                </c:when>
                                <c:otherwise>
                                    <img id="avatarPreview" src="#" alt="头像预览" style="display:none;">
                                </c:otherwise>
                            </c:choose>
                         </div>
                         <small style="color: #aaa;">上传新头像将会替换旧头像 (如果存在)。</small>
                    </div>

                    <button type="submit" class="button" style="width: 100%; padding: 12px;">${formAction == 'add' ? '创建用户' : '保存更改'}</button>
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn-cancel" style="display: block; text-align: center; width: calc(100% - 30px); margin-top:15px;">取消</a>
                </form>
            </div>
        </div>
    </div>

    <!-- 导入 jQuery -->
<%--    <script src="https://cdn.jsdelivr.net/npm/jquery/dist/jquery.min.js"></script>

    <script>
        function previewAvatar(event) {
            var reader = new FileReader();
            reader.onload = function () {
                var output = document.getElementById('avatarPreview');
                output.src = reader.result;
                output.style.display = 'block'; // 显示上传的图片
            };
            if (event.target.files[0]) {
                reader.readAsDataURL(event.target.files[0]);
            } else {
                // 用户取消上传或未选择文件时清空预览
                var output = document.getElementById('avatarPreview');
                output.src = "";
                output.style.display = 'none';
            }
        }

        // 如果用户未上传头像，则调用 API 获取默认头像
        <c:if test="${empty user.avatarPath}">
        $(document).ready(function () {
            $.ajax({
                url: 'https://v2.xxapi.cn/api/head',
                type: 'GET',
                beforeSend: function () {
                    $('#avatarPreview').attr('src', '').hide(); // 清空并隐藏预览
                },
                success: function (res) {
                    if (res.code === 200) {
                        $('#avatarPreview').attr('src', res.data).show(); // 显示 API 返回的默认头像
                    } else {
                        console.error("获取默认头像失败:", res);
                        $('#avatarPreview').hide(); // 隐藏预览
                    }
                },
                error: function (xhr, status, error) {
                    console.error("请求失败:", error);
                    $('#avatarPreview').hide(); // 隐藏预览
                }
            });
        });
        </c:if>
    </script>--%>

    <script>
        function previewAvatar(event) {
            var reader = new FileReader();
            reader.onload = function(){
                var output = document.getElementById('avatarPreview');
                output.src = reader.result;
                output.style.display = 'block'; // Show the preview image
            };
            if (event.target.files[0]) {
                reader.readAsDataURL(event.target.files[0]);
            } else {
                // If no file is selected (e.g., user cancels selection),
                // you might want to hide the preview or revert to the original image.
                // For simplicity, we'll just ensure it's not trying to load a non-existent file.
                var output = document.getElementById('avatarPreview');
                // If there was an existing avatar, you might want to revert to it.
                // This example doesn't handle reverting to original if selection is cancelled after a new preview.
                // output.src = '#'; // or original image path if available
                // output.style.display = 'none';
            }
        }
    </script>
</body>
</html>

