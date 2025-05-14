package com.wanggong232.wangyifei.shopping02.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.wanggong232.wangyifei.shopping02.dao.UserDao;
import com.wanggong232.wangyifei.shopping02.dao.ProductDao;
import com.wanggong232.wangyifei.shopping02.dao.OrderDao;
import com.wanggong232.wangyifei.shopping02.model.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao;
    private ProductDao productDao;
    private OrderDao orderDao;
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardServlet.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
        userDao = new UserDao();
        productDao = new ProductDao();
        orderDao = new OrderDao();
        LOGGER.info("AdminDashboardServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("AdminDashboardServlet doGet called.");
        HttpSession session = request.getSession(false);
        User currentUser = null;
        String dashboardErrorMessage = null;

        try {
            if (session != null) {
                currentUser = (User) session.getAttribute("currentUser");
            }

            if (currentUser == null) {
                LOGGER.warning("Admin access attempt without login. Redirecting to login page.");
                response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=SessionExpiredOrNotLoggedIn");
                return;
            }
            
            if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                LOGGER.warning("Non-ADMIN user (" + currentUser.getUsername() + ") attempted to access admin dashboard. Redirecting to login page.");
                response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=AdminAccessRequired");
                return;
            }
            
            LOGGER.info("Admin user '" + currentUser.getUsername() + "' accessing dashboard.");

            int totalUsers = 0;
            int totalProducts = 0;
            int pendingOrders = 0;
            StringBuilder errorMessages = new StringBuilder();

            try {
                totalUsers = userDao.getTotalUserCount();
                LOGGER.log(Level.INFO, "Total users fetched: {0}", totalUsers);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error fetching total user count", e);
                errorMessages.append("无法加载用户总数: ").append(e.getMessage()).append("; ");
            }

            try {
                totalProducts = productDao.getTotalProductCount();
                LOGGER.log(Level.INFO, "Total products fetched: {0}", totalProducts);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error fetching total product count", e);
                errorMessages.append("无法加载商品总数: ").append(e.getMessage()).append("; ");
            }

            try {
                pendingOrders = orderDao.countPendingOrders();
                LOGGER.log(Level.INFO, "Pending orders fetched: {0}", pendingOrders);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error fetching pending orders count", e);
                errorMessages.append("无法加载待处理订单数: ").append(e.getMessage()).append("; ");
            }
            
            if (errorMessages.length() > 0) {
                dashboardErrorMessage = errorMessages.toString();
                request.setAttribute("dashboardErrorMessage", dashboardErrorMessage);
                LOGGER.warning("Dashboard data loading errors: " + dashboardErrorMessage);
            }

            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("pendingOrders", pendingOrders);
            
            LOGGER.info("Forwarding to admin dashboard page.");
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Critical error in AdminDashboardServlet doGet", e);
            // Set a generic error message for the user
            request.setAttribute("errorMessage", "访问管理员后台时发生严重内部错误，请联系管理员。错误: " + e.getMessage());
            // Attempt to forward to an error page or login page if dashboard forwarding fails
            try {
                response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=DashboardLoadFailed");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to redirect after critical error in AdminDashboardServlet", ex);
                // If redirect fails, at least log it. The response might be already committed.
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("AdminDashboardServlet doPost called, redirecting to doGet.");
        doGet(request, response);
    }
}

