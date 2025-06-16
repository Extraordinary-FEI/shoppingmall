package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.UserDao;
import com.wanggong232.wangyifei.shopping02.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/updateProfile")
public class UpdateProfileServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDao = new UserDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        String username = request.getParameter("username");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // 更新用户基本信息
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setFullName(fullName);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        if (userDao.updateUser(currentUser)) {
            // 处理密码更新
            if (oldPassword != null && !oldPassword.isEmpty() && newPassword != null && !newPassword.isEmpty() && confirmPassword != null && !confirmPassword.isEmpty()) {
                if (!oldPassword.equals(currentUser.getPassword())) {
                    request.setAttribute("errorMessage", "旧密码输入错误！");
                    request.getRequestDispatcher("/user_profile.jsp").forward(request, response);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("errorMessage", "新密码和确认新密码不一致！");
                    request.getRequestDispatcher("/user_profile.jsp").forward(request, response);
                    return;
                }
                if (userDao.updateUserPassword(currentUser.getUserId(), newPassword)) {
                    currentUser.setPassword(newPassword);
                    session.setAttribute("currentUser", currentUser);
                    request.setAttribute("successMessage", "个人信息和密码更新成功！");
                } else {
                    request.setAttribute("errorMessage", "密码更新失败，请稍后重试！");
                }
            } else {
                request.setAttribute("successMessage", "个人信息更新成功！");
            }
        } else {
            request.setAttribute("errorMessage", "个人信息更新失败，请稍后重试！");
        }

        request.getRequestDispatcher("/user_profile.jsp").forward(request, response);
    }
}