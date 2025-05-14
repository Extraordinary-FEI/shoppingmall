package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.UserDao;
import com.wanggong232.wangyifei.shopping02.dao.CartDao;
import com.wanggong232.wangyifei.shopping02.model.User;
import com.wanggong232.wangyifei.shopping02.model.Cart;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao;
    private CartDao cartDao;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDao = new UserDao();
        this.cartDao = new CartDao();
        LOGGER.info("LoginServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("logout".equals(action)) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                    LOGGER.info("User logged out successfully.");
                }
                response.sendRedirect(request.getContextPath() + "/user/login.jsp?logout=true");
            } else {
                request.getRequestDispatcher("/user/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during GET request in LoginServlet", e);
            request.setAttribute("errorMessage", "发生内部错误，请稍后再试或联系管理员。");
            request.getRequestDispatcher("/user/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                request.setAttribute("errorMessage", "用户名和密码不能为空！");
                request.getRequestDispatcher("/user/login.jsp").forward(request, response);
                return;
            }

            User user = userDao.getUserByUsername(username);

            if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
                HttpSession session = request.getSession(); // Create or get session
                session.setAttribute("currentUser", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userRole", user.getRole());
                LOGGER.log(Level.INFO, "User ''{0}'' logged in successfully. Role: ''{1}''", new Object[]{user.getUsername(), user.getRole()});

                // Cart Persistence Logic
                Cart sessionCart = (Cart) session.getAttribute("cart");
                Cart dbCart = cartDao.getCartByUserId(user.getUserId());

                if (dbCart == null) { // Initialize cart if not found in DB
                    LOGGER.log(Level.INFO, "No cart found in DB for user ID: {0}. Creating new cart.", user.getUserId());
                    dbCart = new Cart(user.getUserId()); 
                    // You might want to save this new cart to DB immediately or handle it based on your CartDao logic
                    // For now, it will exist in session. If CartDao.mergeSessionCartToDB handles new cart creation, this is fine.
                }

                if (sessionCart != null && !sessionCart.getItems().isEmpty()) {
                    LOGGER.log(Level.INFO, "Merging session cart for user ID: {0}", user.getUserId());
                    cartDao.mergeSessionCartToDB(user, sessionCart);
                    dbCart = cartDao.getCartByUserId(user.getUserId()); // Reload cart after merge
                }
                
                session.setAttribute("cart", dbCart);
                LOGGER.log(Level.INFO, "Cart set in session for user ID: {0}", user.getUserId());

                if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
                    LOGGER.info("Redirecting ADMIN user to dashboard.");
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                } else {
                    String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                    if (redirectUrl != null && !redirectUrl.isEmpty()) {
                        session.removeAttribute("redirectAfterLogin");
                        LOGGER.log(Level.INFO, "Redirecting user to: {0}", redirectUrl);
                        response.sendRedirect(redirectUrl);
                    } else {
                        LOGGER.info("Redirecting USER to products page.");
                        response.sendRedirect(request.getContextPath() + "/products");
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Login failed for username: {0}. User not found or password mismatch.", username);
                request.setAttribute("errorMessage", "用户名或密码错误！");
                request.getRequestDispatcher("/user/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during POST request in LoginServlet for username: " + username, e);
            request.setAttribute("errorMessage", "登录过程中发生内部错误，请稍后再试或联系管理员。错误详情: " + e.getMessage());
            request.getRequestDispatcher("/user/login.jsp").forward(request, response);
        }
    }
}

