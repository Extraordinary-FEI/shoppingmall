<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Redirect to login if not admin --%>
<c:if test="${empty sessionScope.currentUser || sessionScope.currentUser.role ne 'ADMIN'}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?error=AdminAccessRequired"/>
</c:if>

<%-- Ensure AdminDashboardServlet is called to populate these requestScope attributes --%>
<c:if test="${empty requestScope.totalUsers || empty requestScope.totalProducts || empty requestScope.pendingOrders}">
    <jsp:forward page="/admin/dashboard" />
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员后台 - 炫酷商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        body {
            display: flex;
            min-height: 100vh;
            background-color: #121212; /* Even darker background for admin area */
        }
        .sidebar {
            width: 260px;
            background-color: #1e1e1e; /* Dark sidebar */
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
            color: #c792ea; /* Purple for sidebar title */
            border-bottom: 1px solid #444;
            padding-bottom: 15px;
        }
        .sidebar ul {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }
        .sidebar ul li a {
            color: #b0b0b0; /* Lighter grey for links */
            text-decoration: none;
            padding: 15px 20px;
            display: block;
            border-radius: 6px;
            margin-bottom: 10px;
            transition: background-color 0.3s ease, color 0.3s ease, transform 0.2s ease;
        }
        .sidebar ul li a:hover,
        .sidebar ul li a.active {
            background-color: #c792ea; /* Purple background on hover/active */
            color: #1e1e1e; /* Dark text for contrast */
            transform: translateX(5px);
        }
        .main-content {
            margin-left: 260px; /* Same as sidebar width */
            padding: 30px;
            width: calc(100% - 260px);
            background-color: #121212;
        }
        .admin-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            background-color: #1e1e1e; /* Dark header */
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.3);
            margin-bottom: 30px;
        }
        .admin-header h1 {
            margin: 0;
            font-size: 2em;
            color: #c792ea; /* Purple for main title */
        }
        .admin-header .user-info a {
            color: #c792ea;
            text-decoration: none;
            font-weight: bold;
        }
        .admin-header .user-info a:hover {
            text-decoration: underline;
        }
        .content-area {
            background-color: #1e1e1e; /* Dark content cards */
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
        }
        .content-area h2 {
            margin-top: 0;
            color: #e0e0e0;
            font-size: 1.6em;
            margin-bottom: 20px;
        }
        .stats-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: linear-gradient(145deg, #8e44ad, #5e3370); /* Purple gradient */
            color: white;
            padding: 25px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            transition: transform 0.3s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .stat-card h3 {
            margin-top: 0;
            font-size: 3em;
            margin-bottom: 5px;
        }
        .stat-card p {
            margin-bottom: 0;
            font-size: 1.2em;
        }
        .alert-cool.danger {
            background-color: rgba(231, 76, 60, 0.2);
            border-color: #e74c3c;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            color: #f0f0f0;
            border: 1px solid;
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>炫酷商城后台</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="active">&#128200; 仪表盘</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/users">&#128101; 用户管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/products">&#128722; 商品管理</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/orders">&#128230; 订单管理</a></li>
            <li><a href="${pageContext.request.contextPath}/login?action=logout">&#128682; 退出登录</a></li>
        </ul>
    </div>

    <div class="main-content fade-in">
        <header class="admin-header">
            <h1>仪表盘</h1>
            <div class="user-info">
                <span>管理员: <a href="${pageContext.request.contextPath}/user_profile"><%= session.getAttribute("username") %></a></span>
                <a href="logout?action=logout">退出登录</a>
            </div>
        </header>

        <div class="content-area">
            <h2>欢迎回来, ${sessionScope.currentUser.fullName != null ? sessionScope.currentUser.fullName : sessionScope.currentUser.username}!</h2>
            <p>这里是您的商城管理仪表盘。您可以在这里管理用户、商品和订单。</p>
            
            <c:if test="${not empty requestScope.errorMessage}">
                <div class="alert-cool danger">${requestScope.errorMessage}</div>
            </c:if>

            <div class="stats-cards">
                <div class="stat-card">
                    <h3><c:out value="${requestScope.totalUsers != null ? requestScope.totalUsers : 0}"/></h3> 
                    <p>注册用户</p>
                </div>
                <div class="stat-card">
                    <h3><c:out value="${requestScope.totalProducts != null ? requestScope.totalProducts : 0}"/></h3> 
                    <p>在售商品</p>
                </div>
                <div class="stat-card">
                    <h3><c:out value="${requestScope.pendingOrders != null ? requestScope.pendingOrders : 0}"/></h3> 
                    <p>待处理订单</p>
                </div>
            </div>
            
            <p>请从左侧导航栏选择一个模块开始管理。</p>
        </div>
    </div>
</body>
</html>

