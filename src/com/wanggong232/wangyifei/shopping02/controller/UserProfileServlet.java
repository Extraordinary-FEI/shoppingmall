package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/user_profile")
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserProfileServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = null;

        try {
            if (session != null) {
                currentUser = (User) session.getAttribute("currentUser");
            }

            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=SessionExpiredOrNotLoggedIn");
                return;
            }

            request.getRequestDispatcher("/user_profile.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "访问个人信息页面时发生错误，请联系管理员。");
            request.getRequestDispatcher("/user/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
