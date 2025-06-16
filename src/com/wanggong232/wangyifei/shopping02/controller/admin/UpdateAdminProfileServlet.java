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

@WebServlet("/updateAdminProfile")
public class UpdateAdminProfileServlet extends HttpServlet {
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

        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // 更新管理员基本信息
        currentUser.setEmail(email);
        currentUser.setFullName(fullName);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        if (userDao.updateUser(currentUser)) {
            session.setAttribute("currentUser", currentUser);
            request.setAttribute("successMessage", "管理员信息更新成功！");
        } else {
            request.setAttribute("errorMessage", "管理员信息更新失败，请稍后重试！");
        }

        request.getRequestDispatcher("/admin/profile.jsp").forward(request, response);
    }
}

