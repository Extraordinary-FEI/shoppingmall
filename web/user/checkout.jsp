<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Redirect to login if user is not logged in or cart is empty --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="${pageContext.request.contextPath}/user/login.jsp?redirect=checkout"/>
</c:if>
<c:if test="${empty sessionScope.cart || empty sessionScope.cart.items}">
    <c:redirect url="${pageContext.request.contextPath}/user/cart.jsp?error=emptycart"/>
</c:if>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>确认订单与支付 - 购物商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cool_style.css">
    <style>
        /* Specific styles for checkout page if needed */
        .order-summary-card, .shipping-details-card, .payment-options-card {
            background-color: #2c2c2c; /* Darker card background */
            padding: 25px;
            border-radius: 8px;
            margin-bottom: 30px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
        }
        .order-summary-card h2, .shipping-details-card h2, .payment-options-card h2 {
            color: #c792ea; /* Purple for headings */
            border-bottom: 1px solid #444;
            padding-bottom: 10px;
            margin-top: 0;
            margin-bottom: 20px;
            font-size: 1.6em;
        }
        .summary-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            font-size: 1.1em;
            color: #e0e0e0;
        }
        .summary-item span:last-child {
            font-weight: bold;
            color: #f0f0f0;
        }
        .summary-item.total {
            font-weight: bold;
            font-size: 1.4em;
            color: #c792ea; /* Purple for total */
            border-top: 1px solid #444;
            padding-top: 15px;
            margin-top: 15px;
        }
        .product-thumbnail {
            width: 50px;
            height: 50px;
            object-fit: cover;
            border-radius: 4px;
            margin-right: 15px;
            vertical-align: middle;
            border: 1px solid #555;
        }
        .payment-option {
            padding: 15px;
            border: 1px solid #444;
            border-radius: 6px;
            margin-bottom: 15px;
            background-color: #333;
        }
        .payment-option h3 {
            margin-top: 0;
            color: #c792ea;
        }
        .payment-option img.qr-code {
            display: block;
            max-width: 200px; /* Adjust as needed */
            height: auto;
            margin: 10px auto;
            border: 1px solid #555;
            border-radius: 4px;
            background-color: #fff; /* Placeholder background for QR */
        }
        .payment-option .button {
            display: block;
            width: calc(100% - 30px); /* Full width button inside payment option */
            margin: 15px auto 0;
            text-align: center;
        }
    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/products">商品首页</a></li>
            <li class="cart-info"><a href="${pageContext.request.contextPath}/cart">购物车 (<span id="cartItemCount">${empty sessionScope.cart.items ? 0 : sessionScope.cart.totalItems}</span>)</a></li>
            <c:if test="${not empty sessionScope.currentUser}">
                    <span>欢迎，<a href="${pageContext.request.contextPath}/user_profile"><%= session.getAttribute("username") %></a>！</span>
                    <a href="login?action=logout">退出登录</a>
            </c:if>
        </ul>
    </nav>

    <div class="container fade-in">
        <h1>确认订单与支付</h1>

        <c:if test="${not empty orderError}">
            <p class="error-message">${orderError}</p>
        </c:if>
        <c:if test="${not empty paymentMessage}">
            <p class="success-message" id="paymentSuccessMessage">${paymentMessage}</p>
        </c:if>

        <div class="order-summary-card">
            <h2>订单摘要</h2>
            <c:forEach var="entry" items="${sessionScope.cart.items}">
                <c:set var="item" value="${entry.value}"/>
                <c:set var="product" value="${item.product}"/>
                <div class="summary-item">
                    <span>
                        <img src="${pageContext.request.contextPath}/${not empty product.imageUrl ? product.imageUrl : 'images/default_product.png'}" alt="${product.name}" class="product-thumbnail">
                        ${product.name} (x${item.quantity})
                    </span>
                    <span><fmt:formatNumber value="${item.subtotal}" type="currency"/></span>
                </div>
            </c:forEach>
            <div class="summary-item total">
                <span>总计</span>
                <span><fmt:formatNumber value="${sessionScope.cart.totalAmount}" type="currency"/></span>
            </div>
        </div>

        <%-- Check if order has been placed and is pending payment --%>
        <c:if test="${empty sessionScope.latestOrder || sessionScope.latestOrder.status == 'PAID'}">
            <div class="shipping-details-card">
                <h2>收货信息</h2>
                <form action="${pageContext.request.contextPath}/order" method="post" id="placeOrderForm">
                    <input type="hidden" name="action" value="placeOrder">
                    
                    <div class="form-group">
                        <label for="fullName">收货人姓名:</label>
                        <input type="text" id="fullName" name="fullName" value="${sessionScope.currentUser.fullName}" required>
                    </div>

                    <div class="form-group">
                        <label for="shippingAddress">详细收货地址:</label>
                        <textarea id="shippingAddress" name="shippingAddress" required>${sessionScope.currentUser.address}</textarea>
                    </div>

                    <div class="form-group">
                        <label for="phone">联系电话:</label>
                        <input type="text" id="phone" name="phone" value="${sessionScope.currentUser.phone}" required>
                    </div>
                    
                    <button type="submit" style="width:100%; padding: 15px; font-size: 1.2em;">提交订单并选择支付方式</button>
                </form>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.latestOrder && sessionScope.latestOrder.status == 'PENDING'}">
            <div class="payment-options-card">
                <h2>选择支付方式</h2>
                <p>您的订单 (ID: ${sessionScope.latestOrder.orderId}) 已成功提交，请选择以下方式完成支付。</p>
                
                <!-- Alipay -->
                <div class="payment-option">
                    <h3>支付宝</h3>
                    <img src="${pageContext.request.contextPath}/images/payment_qr/alipay_qr.jpg" alt="支付宝收款码" class="qr-code" onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                    <form action="${pageContext.request.contextPath}/order" method="post">
                        <input type="hidden" name="action" value="confirmPayment">
                        <input type="hidden" name="orderId" value="${sessionScope.latestOrder.orderId}">
                        <input type="hidden" name="paymentMethod" value="ALIPAY">
                        <button type="submit" class="button">我已使用支付宝支付</button>
                    </form>
                </div>

                <!-- WeChat Pay -->
                <div class="payment-option">
                    <h3>微信支付</h3>
                    <img src="${pageContext.request.contextPath}/images/payment_qr/wechat_qr.png" alt="微信支付收款码" class="qr-code" onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                     <p style="text-align:center; color:#aaa; display:none;">微信支付二维码未上传或路径错误。请管理员在 webapp/images/payment_qr/ 目录下放置 wechat_qr.png</p>
                    <form action="${pageContext.request.contextPath}/order" method="post">
                        <input type="hidden" name="action" value="confirmPayment">
                        <input type="hidden" name="orderId" value="${sessionScope.latestOrder.orderId}">
                        <input type="hidden" name="paymentMethod" value="WECHAT_PAY">
                        <button type="submit" class="button">我已使用微信支付</button>
                    </form>
                </div>

                <!-- UnionPay -->
                <div class="payment-option">
                    <h3>银联支付/银行卡</h3>
                    <!-- 
                        QR Code Image Instructions:
                        Place your UnionPay/Bank QR code image at: webapp/images/payment_qr/unionpay_qr.png
                        Suggested size: 200x200 pixels 
                    -->
                    <img src="${pageContext.request.contextPath}/images/payment_qr/unionpay_qr.png" alt="银联/银行卡收款码" class="qr-code" onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                     <p style="text-align:center; color:#aaa; display:none;">银联二维码未上传或路径错误。请管理员在 webapp/images/payment_qr/ 目录下放置 unionpay_qr.png</p>
                    <form action="${pageContext.request.contextPath}/order" method="post">
                        <input type="hidden" name="action" value="confirmPayment">
                        <input type="hidden" name="orderId" value="${sessionScope.latestOrder.orderId}">
                        <input type="hidden" name="paymentMethod" value="UNION_PAY">
                        <button type="submit" class="button">我已使用银联/银行卡支付</button>
                    </form>
                </div>
                 <div class="payment-option">
                    <h3>货到付款 (Cash on Delivery)</h3>
                    <p style="text-align:center; color:#ccc;">如果您希望选择货到付款，您的订单已提交，我们将尽快为您安排发货。</p>
                    <form action="${pageContext.request.contextPath}/order" method="post">
                        <input type="hidden" name="action" value="confirmPayment">
                        <input type="hidden" name="orderId" value="${sessionScope.latestOrder.orderId}">
                        <input type="hidden" name="paymentMethod" value="COD">
                        <button type="submit" class="button">选择货到付款</button>
                    </form>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.latestOrder && sessionScope.latestOrder.status == 'PAID'}">
             <div class="payment-options-card">
                <h2>支付成功</h2>
                <p>您的订单 (ID: ${sessionScope.latestOrder.orderId}) 已成功支付！感谢您的购买。</p>
                <p><a href="${pageContext.request.contextPath}/order?action=history" class="button">查看我的订单</a></p>
            </div>
        </c:if>

    </div>

    <footer>
        <p>&copy; <c:set var="currentYear"><jsp:useBean id="date" class="java.util.Date" /><fmt:formatDate value="${date}" pattern="yyyy" /></c:set>${currentYear} 炫酷商城. 版权所有.</p>
    </footer>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const paymentSuccessMessage = document.getElementById('paymentSuccessMessage');
            if (paymentSuccessMessage) {
                setTimeout(function() {
                    paymentSuccessMessage.style.opacity = '0';
                    setTimeout(function() {
                        if (paymentSuccessMessage.parentNode) {
                            paymentSuccessMessage.parentNode.removeChild(paymentSuccessMessage);
                        }
                    }, 500); // Remove after fade out
                }, 3000); // Start fade out after 3 seconds
            }
        });
    </script>
</body>
</html>

