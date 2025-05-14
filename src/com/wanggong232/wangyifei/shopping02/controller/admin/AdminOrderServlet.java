package com.wanggong232.wangyifei.shopping02.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.wanggong232.wangyifei.shopping02.dao.OrderDao;
import com.wanggong232.wangyifei.shopping02.dao.ProductDao; 
import com.wanggong232.wangyifei.shopping02.dao.UserDao; // Added for fetching user details for an order
import com.wanggong232.wangyifei.shopping02.model.Order;
import com.wanggong232.wangyifei.shopping02.model.OrderItem;
import com.wanggong232.wangyifei.shopping02.model.Product;
import com.wanggong232.wangyifei.shopping02.model.User;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDao orderDao;
    private ProductDao productDao; 
    private UserDao userDao; // Added UserDao

    public void init() {
        orderDao = new OrderDao();
        productDao = new ProductDao();
        userDao = new UserDao(); // Initialize UserDao
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=AdminAccessRequired");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; 
        }

        switch (action) {
            case "view":
                viewOrderDetails(request, response);
                break;
            case "search":
                searchOrders(request, response);
                break;
            case "list":
            default:
                listOrders(request, response);
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=AdminAccessRequired");
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            updateOrderStatus(request, response);
        } else {
            listOrders(request, response); 
        }
    }

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String newStatus = request.getParameter("status");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "订单状态不能为空。");
                response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
                return;
            }
            
            Order existingOrder = orderDao.getOrderById(orderId);
            if (existingOrder == null) {
                request.getSession().setAttribute("errorMessage", "未找到订单ID: " + orderId);
                response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
                return;
            }

            // Use the existing payment method from the fetched order, as the form only submits status.
            boolean success = orderDao.updateOrderStatusAndPayment(orderId, newStatus, existingOrder.getPaymentMethod());

            if (success) {
                request.getSession().setAttribute("message", "订单 " + orderId + " 状态已成功更新为 " + newStatus + "。");
            } else {
                request.getSession().setAttribute("errorMessage", "更新订单 " + orderId + " 状态失败。数据库操作可能未成功。");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的订单ID格式。");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "更新订单状态时发生意外错误: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin/orders?action=list");
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Order> orderList = orderDao.getAllOrders(); 
        enrichOrders(orderList); // Enrich with user and product details
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/admin/order_list.jsp").forward(request, response);
    }

    private void searchOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        List<Order> orderList;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            orderList = orderDao.getAllOrders().stream()
                    .filter(order -> 
                        String.valueOf(order.getOrderId()).contains(searchTerm) ||
                        (order.getUser() != null && String.valueOf(order.getUser().getUserId()).contains(searchTerm)) ||
                        (order.getUser() != null && order.getUser().getUsername() != null && order.getUser().getUsername().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (order.getStatus() != null && order.getStatus().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (order.getShippingAddress() != null && order.getShippingAddress().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (order.getPaymentMethod() != null && order.getPaymentMethod().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
            request.setAttribute("message", "搜索结果: " + searchTerm);
        } else {
            orderList = orderDao.getAllOrders();
        }
        enrichOrders(orderList); // Enrich with user and product details
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/admin/order_list.jsp").forward(request, response);
    }

    private void viewOrderDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            Order order = orderDao.getOrderById(orderId);
            if (order != null) {
                // User details should already be populated by OrderDao.getOrderById if implemented correctly
                // If not, fetch user explicitly:
                if (order.getUser() == null && order.getUserId() > 0) { // Check if user object is missing but userId is present
                    User orderUser = userDao.getUserById(order.getUserId());
                    order.setUser(orderUser);
                }
                enrichOrderItems(order); // Enrich items with product details
                request.setAttribute("orderDetails", order);
                request.getRequestDispatcher("/admin/order_detail.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "未找到ID为 " + orderId + " 的订单");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的订单ID格式");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "查看订单详情时发生错误: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    // Helper to enrich a list of orders
    private void enrichOrders(List<Order> orders) {
        if (orders == null) return;
        for (Order order : orders) {
            enrichOrder(order);
        }
    }
    
    // Helper to enrich a single order
    private void enrichOrder(Order order) {
        if (order == null) return;
        // Ensure user object is populated
        if (order.getUser() == null && order.getUserId() > 0) {
            User orderUser = userDao.getUserById(order.getUserId());
            order.setUser(orderUser);
        }
        // Ensure order items and their products are populated
        enrichOrderItems(order);
    }

    // Helper to enrich order items with product details for a single order
    private void enrichOrderItems(Order order) {
        if (order == null || order.getItems() == null) return;
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getName() == null) { 
                Product product = productDao.getProductById(item.getProductId());
                if (product != null) {
                    item.setProduct(product); 
                }
            }
        }
    }
}

