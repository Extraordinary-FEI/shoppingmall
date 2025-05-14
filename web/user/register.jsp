<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户注册 - 炫酷商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        .avatar-preview {
            width: 100px;
            height: 100px;
            border: 1px solid #ddd;
            border-radius: 50%;
            margin-top: 10px;
            object-fit: cover;
        }
    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user/products.jsp">首页</a></li>
            <li><a href="${pageContext.request.contextPath}/user/cart.jsp">购物车</a></li>
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <li><a href="${pageContext.request.contextPath}/user/profile.jsp">个人中心</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">退出登录</a></li>
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
            <h2>用户注册</h2>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">${errorMessage}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="username">用户名</label>
                    <input type="text" id="username" name="username" value="<c:out value='${user.username}'/>" required>
                </div>

                <div class="form-group">
                    <label for="password">密码</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <div class="form-group">
                    <label for="email">邮箱</label>
                    <input type="email" id="email" name="email" value="<c:out value='${user.email}'/>" required>
                </div>

                <div class="form-group">
                    <label for="fullName">姓名</label>
                    <input type="text" id="fullName" name="fullName" value="<c:out value='${user.fullName}'/>" required>
                </div>

                <div class="form-group">
                    <label for="address">地址</label>
                    <input type="text" id="address" name="address" value="<c:out value='${user.address}'/>">
                </div>

                <div class="form-group">
                    <label for="phone">电话</label>
                    <input type="text" id="phone" name="phone" value="<c:out value='${user.phone}'/>">
                </div>

                <div class="form-group">
                    <label for="avatar">选择头像 (可选)</label>
                    <input type="file" id="avatar" name="avatar" accept="image/*" onchange="previewAvatar(event)">
                    <img id="avatarPreview" src="#" alt="头像预览" class="avatar-preview" style="display:none;"/>
                </div>

                <button type="submit">立即注册</button>
            </form>

            <p style="text-align: center; margin-top: 20px;">已经有账号了？<a href="${pageContext.request.contextPath}/user/login.jsp">点击登录</a></p>
        </div>
    </div>
    <footer>
        <p>&copy; 2025 炫酷商城. 版权所有.</p>
    </footer>
    <script>
        function previewAvatar(event) {
            var reader = new FileReader();
            reader.onload = function(){
                var output = document.getElementById('avatarPreview');
                output.src = reader.result;
                output.style.display = 'block';
            };
            if (event.target.files[0]) {
                reader.readAsDataURL(event.target.files[0]);
            } else {
                var output = document.getElementById('avatarPreview');
                output.src = '#';
                output.style.display = 'none';
            }
        }
    </script>
</body>
</html>

