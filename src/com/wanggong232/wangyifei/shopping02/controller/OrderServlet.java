package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.CartDao;
import com.wanggong232.wangyifei.shopping02.exception.InsufficientStockException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.wanggong232.wangyifei.shopping02.dao.OrderDao;
import com.wanggong232.wangyifei.shopping02.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDao orderDao;

    private static final Logger LOGGER = Logger.getLogger(OrderServlet.class.getName());


    public void init() {
        orderDao = new OrderDao();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=请先登录后再操作");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String action = request.getParameter("action");

        if ("placeOrder".equals(action)) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null || cart.getItems().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/user/cart.jsp?error=购物车为空，无法下单");
                return;
            }

            try {
                String fullName = request.getParameter("fullName");
                String shippingAddress = request.getParameter("shippingAddress");
                // String phone = request.getParameter("phone"); // Phone is not in orders table as per DAO and error

                if (fullName == null || fullName.trim().isEmpty() ||
                        shippingAddress == null || shippingAddress.trim().isEmpty()) {
                        // Removed phone check: phone == null || phone.trim().isEmpty()) {
                    request.setAttribute("orderError", "收货人姓名和详细收货地址均为必填项。");
                    request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
                    return;
                }

                Order order = new Order();
                order.setUserId(currentUser.getUserId());
                order.setOrderDate(new Timestamp(System.currentTimeMillis()));
                order.setTotalAmount(cart.getTotalAmount());
                order.setStatus("PENDING");
                order.setShippingAddress(shippingAddress);
                // order.setPhone(phone); // Do not set phone if it's not in the orders table
                order.setPaymentMethod("PENDING_PAYMENT"); // Default before actual payment selection

                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem cartItem : cart.getItems().values()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProduct().getProductId());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
                    orderItems.add(orderItem);
                }
                order.setItems(orderItems);

                boolean orderPlaced = orderDao.addOrder(order);

                if (orderPlaced) {
                    session.setAttribute("latestOrder", order); // Store the newly created order
                    // Redirect to checkout.jsp which should now show payment options for the latestOrder
                    response.sendRedirect(request.getContextPath() + "/user/checkout.jsp"); 
                } else {
                    request.setAttribute("orderError", "订单处理失败，未知错误。请稍后再试。");
                    request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
                }
            } catch (InsufficientStockException e) {
                e.printStackTrace();
                request.setAttribute("orderError", "订单处理失败: " + e.getMessage());
                request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("orderError", "订单处理失败: 数据库操作错误，请稍后再试。错误详情: " + e.getMessage());
                request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("orderError", "处理您的订单时发生意外错误: " + e.getMessage());
                request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
            }
        } else if ("confirmPayment".equals(action)) {
            try {
                String orderIdStr = request.getParameter("orderId");
                String paymentMethod = request.getParameter("paymentMethod");

                if (orderIdStr == null || orderIdStr.trim().isEmpty() || paymentMethod == null || paymentMethod.trim().isEmpty()) {
                    request.setAttribute("orderError", "订单ID或支付方式缺失。");
                    request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
                    return;
                }
                int orderId = Integer.parseInt(orderIdStr);

                // Fetch the order from DB to ensure it's the correct one and in PENDING state
                Order orderToUpdate = orderDao.getOrderById(orderId);

                if (orderToUpdate == null || orderToUpdate.getUserId() != currentUser.getUserId()) {
                    request.setAttribute("orderError", "无效的订单ID或订单不属于您。");
                    response.sendRedirect(request.getContextPath() + "/user/order_history.jsp?error=订单查找失败");
                    return;
                }

                if (!"PENDING".equals(orderToUpdate.getStatus())) {
                     session.setAttribute("latestOrder", orderToUpdate); // Update session with current order state
                    if("PAID".equals(orderToUpdate.getStatus())){
                        session.setAttribute("paymentMessage", "订单 (ID: " + orderId + ") 此前已成功支付！");
                    } else {
                        session.setAttribute("paymentMessage", "订单 (ID: " + orderId + ") 状态为 "+orderToUpdate.getStatus()+", 无需重复操作。");
                    }
                    response.sendRedirect(request.getContextPath() + "/user/checkout.jsp");
                    return;
                }

                boolean paymentConfirmed = orderDao.updateOrderStatusAndPayment(orderId, "PAID", paymentMethod);

                if (paymentConfirmed) {
                    CartDao cartDao = new CartDao();
                    cartDao.clearCart(currentUser.getUserId()); 
                    session.removeAttribute("cart"); 

                    orderToUpdate.setStatus("PAID");
                    orderToUpdate.setPaymentMethod(paymentMethod);
                    session.setAttribute("latestOrder", orderToUpdate);
                    session.setAttribute("paymentMessage", "订单 (ID: " + orderId + ") 已成功支付！感谢您的购买。");
                    response.sendRedirect(request.getContextPath() + "/user/checkout.jsp");
                } else {
                    request.setAttribute("orderError", "支付确认失败，数据库更新错误，请联系客服或稍后再试。");
                    request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("orderError", "无效的订单ID格式。");
                request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("orderError", "确认支付时发生意外错误: " + e.getMessage());
                request.getRequestDispatcher("/user/checkout.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=请登录后查看订单");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String action = request.getParameter("action");

        if ("history".equals(action)) {
            List<Order> orderHistory = orderDao.getOrdersByUserId(currentUser.getUserId());
            request.setAttribute("orderHistory", orderHistory);
            request.getRequestDispatcher("/user/order_history.jsp").forward(request, response);
        } else if (request.getParameter("orderId") != null && "view".equals(action)) { // Added action=view for clarity
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                Order order = orderDao.getOrderById(orderId);
                // Ensure order_detail.jsp exists or is correctly named/pathed
                if (order != null && (order.getUserId() == currentUser.getUserId() || "ADMIN".equals(currentUser.getRole()))) {
                    request.setAttribute("orderDetails", order);
                    request.getRequestDispatcher("/user/order_detail.jsp").forward(request, response); 
                } else {
                    response.sendRedirect(request.getContextPath() + "/user/order_history.jsp?error=无权访问此订单或订单不存在");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/user/order_history.jsp?error=无效的订单ID");
            }
        } else {
            // Default GET request could redirect to order history or products page
            response.sendRedirect(request.getContextPath() + "/user/order_history.jsp");
        }
    }
}

