<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员个人中心 - 炫酷商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            flex-direction: column;
        }
        .main-content {
            display: flex;
            flex: 1;
            padding: 20px;
            gap: 20px;
            max-width: 1200px;
            margin: 20px auto;
            background-color: #1e1e1e;
            border-radius: 12px;
            box-shadow: 0 8px 30px rgba(0,0,0,0.5);
        }
        .profile-sidebar {
            width: 250px;
            background-color: #2c2c2c;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.3);
        }
        .profile-sidebar ul {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }
        .profile-sidebar ul li a {
            display: flex;
            align-items: center;
            padding: 15px 20px;
            color: #e0e0e0;
            text-decoration: none;
            border-radius: 8px;
            margin-bottom: 10px;
            transition: background-color 0.3s ease, color 0.3s ease, transform 0.2s ease;
            font-size: 1.1em;
        }
        .profile-sidebar ul li a .icon {
            margin-right: 12px;
            font-size: 1.3em;
        }
        .profile-sidebar ul li a:hover,
        .profile-sidebar ul li a.active {
            background-color: #c792ea;
            color: #1e1e1e;
            transform: translateX(5px);
        }
        .profile-content {
            flex: 1;
            background-color: #2c2c2c;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.3);
        }
        .profile-content h2 {
            color: #c792ea;
            font-size: 2.2em;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #444;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #e0e0e0;
            font-size: 1.05em;
        }
        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="password"],
        .form-group input[type="number"],
        .form-group textarea {
            width: calc(100% - 24px);
            padding: 12px;
            border: 1px solid #555;
            border-radius: 6px;
            background-color: #3a3a3a;
            color: #e0e0e0;
            font-size: 1em;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
        }
        .form-group input[type="text"]:focus,
        .form-group input[type="email"]:focus,
        .form-group input[type="password"]:focus,
        .form-group input[type="number"]:focus,
        .form-group textarea:focus {
            border-color: #c792ea;
            box-shadow: 0 0 0 3px rgba(199, 146, 234, 0.3);
            outline: none;
        }
        .profile-content button {
            background-color: #c792ea;
            color: #1e1e1e;
            padding: 12px 25px;
            border: none;
            border-radius: 8px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .profile-content button:hover {
            background-color: #b47eda;
            transform: translateY(-2px);
        }
        .message-success {
            color: #2ecc71;
            background-color: rgba(46, 204, 113, 0.1);
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            border: 1px solid #2ecc71;
            text-align: center;
        }
        .message-error {
            color: #e74c3c;
            background-color: rgba(231, 76, 60, 0.1);
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            border: 1px solid #e74c3c;
            text-align: center;
        }
        @media (max-width: 768px) {
            .main-content {
                flex-direction: column;
                padding: 10px;
            }
            .profile-sidebar {
                width: 100%;
                padding: 15px;
            }
            .profile-content {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
<nav>
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard">管理后台</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/users">用户管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/products">商品管理</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/orders">订单管理</a></li>
        <c:choose>
            <c:when test="${not empty sessionScope.currentUser}">
                <li><a href="${pageContext.request.contextPath}/admin/profile.jsp" class="active">欢迎, ${sessionScope.currentUser.username}!</a></li>
                <li><a href="${pageContext.request.contextPath}/login?action=logout">退出登录</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${pageContext.request.contextPath}/user/login.jsp">登录</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</nav>

<div class="container fade-in">
    <div class="main-content">
        <div class="profile-sidebar">
            <ul>
                <li><a href="#" id="personalInfoLink" class="active"><span class="icon">👤</span> 个人信息</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/dashboard"><span class="icon">📊</span> 管理后台</a></li>
                <li><a href="#" id="changePasswordLink"><span class="icon">🔒</span> 修改密码</a></li>
                <li><a href="#" id="rechargeAccountLink"><span class="icon">💰</span> 账户充值</a></li>
                <li><a href="${pageContext.request.contextPath}/login?action=logout"><span class="icon">🚪</span> 退出系统</a></li>
            </ul>
        </div>

        <div class="profile-content">
            <div id="personalInfoSection">
                <h2>管理员个人信息</h2>
                <c:if test="${not empty successMessage}">
                    <p class="message-success">${successMessage}</p>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <p class="message-error">${errorMessage}</p>
                </c:if>
                <form action="${pageContext.request.contextPath}/updateProfile" method="post">
                    <div class="form-group">
                        <label for="username">用户名:</label>
                        <input type="text" id="username" name="username" value="${sessionScope.currentUser.username}" readonly>
                    </div>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" value="${sessionScope.currentUser.email}">
                    </div>
                    <div class="form-group">
                        <label for="fullName">真实姓名:</label>
                        <input type="text" id="fullName" name="fullName" value="${sessionScope.currentUser.fullName}">
                    </div>
                    <div class="form-group">
                        <label for="phone">手机号码:</label>
                        <input type="text" id="phone" name="phone" value="${sessionScope.currentUser.phone}">
                    </div>
                    <div class="form-group">
                        <label for="address">联系地址:</label>
                        <textarea id="address" name="address" rows="3">${sessionScope.currentUser.address}</textarea>
                    </div>
                    <button type="submit">保存</button>
                </form>
            </div>

            <div id="changePasswordSection" style="display: none;">
                <h2>修改密码</h2>
                <c:if test="${not empty passwordSuccessMessage}">
                    <p class="message-success">${passwordSuccessMessage}</p>
                </c:if>
                <c:if test="${not empty passwordErrorMessage}">
                    <p class="message-error">${passwordErrorMessage}</p>
                </c:if>
                <form action="${pageContext.request.contextPath}/changePassword" method="post">
                    <div class="form-group">
                        <label for="currentPassword">当前密码:</label>
                        <input type="password" id="currentPassword" name="currentPassword" required>
                    </div>
                    <div class="form-group">
                        <label for="newPassword">新密码:</label>
                        <input type="password" id="newPassword" name="newPassword" required>
                    </div>
                    <div class="form-group">
                        <label for="confirmNewPassword">确认新密码:</label>
                        <input type="password" id="confirmNewPassword" name="confirmNewPassword" required>
                    </div>
                    <button type="submit">修改密码</button>
                </form>
            </div>

            <div id="rechargeAccountSection" style="display: none;">
                <h2>账户充值</h2>
                <c:if test="${not empty rechargeSuccessMessage}">
                    <p class="message-success">${rechargeSuccessMessage}</p>
                </c:if>
                <c:if test="${not empty rechargeErrorMessage}">
                    <p class="message-error">${rechargeErrorMessage}</p>
                </c:if>
                <p>当前账户余额: <span style="color: #c792ea; font-weight: bold;">${sessionScope.currentUser.balance != null ? sessionScope.currentUser.balance : 0.00}</span> 元</p>
                <form action="${pageContext.request.contextPath}/rechargeAccount" method="post">
                    <div class="form-group">
                        <label for="rechargeAmount">充值金额:</label>
                        <input type="number" id="rechargeAmount" name="rechargeAmount" min="1" step="0.01" required>
                    </div>
                    <button type="submit">立即充值</button>
                </form>
            </div>
        </div>
    </div>
</div>

<footer>
    <p>&copy; 2025 炫酷商城. 版权所有.</p>
</footer>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const personalInfoLink = document.getElementById('personalInfoLink');
        const changePasswordLink = document.getElementById('changePasswordLink');
        const rechargeAccountLink = document.getElementById('rechargeAccountLink');

        const personalInfoSection = document.getElementById('personalInfoSection');
        const changePasswordSection = document.getElementById('changePasswordSection');
        const rechargeAccountSection = document.getElementById('rechargeAccountSection');

        function showSection(sectionToShow) {
            personalInfoSection.style.display = 'none';
            changePasswordSection.style.display = 'none';
            rechargeAccountSection.style.display = 'none';

            personalInfoLink.classList.remove('active');
            changePasswordLink.classList.remove('active');
            rechargeAccountLink.classList.remove('active');

            sectionToShow.style.display = 'block';
        }

        personalInfoLink.addEventListener('click', function(e) {
            e.preventDefault();
            showSection(personalInfoSection);
            personalInfoLink.classList.add('active');
        });

        changePasswordLink.addEventListener('click', function(e) {
            e.preventDefault();
            showSection(changePasswordSection);
            changePasswordLink.classList.add('active');
        });

        rechargeAccountLink.addEventListener('click', function(e) {
            e.preventDefault();
            showSection(rechargeAccountSection);
            rechargeAccountLink.classList.add('active');
        });

        // Initial display based on URL hash or default to personal info
        const hash = window.location.hash;
        if (hash === '#changePassword') {
            showSection(changePasswordSection);
            changePasswordLink.classList.add('active');
        } else if (hash === '#rechargeAccount') {
            showSection(rechargeAccountSection);
            rechargeAccountLink.classList.add('active');
        } else {
            showSection(personalInfoSection);
            personalInfoLink.classList.add('active');
        }
    });
</script>
</body>
</html>

