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

@WebServlet("/changeAdminPassword")
public class ChangeAdminPasswordServlet extends HttpServlet {
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

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");

        // 验证当前密码
        if (!currentPassword.equals(currentUser.getPassword())) {
            request.setAttribute("passwordErrorMessage", "当前密码输入错误！");
            request.getRequestDispatcher("/admin/profile.jsp").forward(request, response);
            return;
        }

        // 验证新密码和确认密码是否一致
        if (!newPassword.equals(confirmNewPassword)) {
            request.setAttribute("passwordErrorMessage", "新密码和确认密码不一致！");
            request.getRequestDispatcher("/admin/profile.jsp").forward(request, response);
            return;
        }

        // 验证新密码长度
        if (newPassword.length() < 6) {
            request.setAttribute("passwordErrorMessage", "新密码长度至少为6位！");
            request.getRequestDispatcher("/admin/profile.jsp").forward(request, response);
            return;
        }

        // 更新密码
        if (userDao.updateUserPassword(currentUser.getUserId(), newPassword)) {
            currentUser.setPassword(newPassword);
            session.setAttribute("currentUser", currentUser);
            request.setAttribute("passwordSuccessMessage", "管理员密码修改成功！");
        } else {
            request.setAttribute("passwordErrorMessage", "密码修改失败，请稍后重试！");
        }

        request.getRequestDispatcher("/admin/profile.jsp").forward(request, response);
    }
}

