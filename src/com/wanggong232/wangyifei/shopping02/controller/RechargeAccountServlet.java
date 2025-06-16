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
import java.math.BigDecimal;

@WebServlet("/rechargeAccount")
public class RechargeAccountServlet extends HttpServlet {
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

        String rechargeAmountStr = request.getParameter("rechargeAmount");

        try {
            BigDecimal rechargeAmount = new BigDecimal(rechargeAmountStr);

            // 验证充值金额
            if (rechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("rechargeErrorMessage", "充值金额必须大于0！");
                // 根据用户角色跳转到不同的页面
                if ("ADMIN".equals(currentUser.getRole())) {
                    request.getRequestDispatcher("/admin/profile.jsp#rechargeAccount").forward(request, response);
                } else {
                    request.getRequestDispatcher("/user/user_profile.jsp#rechargeAccount").forward(request, response);
                }
                return;
            }

            if (rechargeAmount.compareTo(new BigDecimal("10000")) > 0) {
                request.setAttribute("rechargeErrorMessage", "单次充值金额不能超过10000元！");
                // 根据用户角色跳转到不同的页面
                if ("ADMIN".equals(currentUser.getRole())) {
                    request.getRequestDispatcher("/admin/profile.jsp#rechargeAccount").forward(request, response);
                } else {
                    request.getRequestDispatcher("/user/user_profile.jsp#rechargeAccount").forward(request, response);
                }
                return;
            }

            // 获取当前余额
            BigDecimal currentBalance = currentUser.getBalance();
            if (currentBalance == null) {
                currentBalance = BigDecimal.ZERO;
            }

            // 计算新余额
            BigDecimal newBalance = currentBalance.add(rechargeAmount);

            // 更新用户余额
            if (userDao.updateUserBalance(currentUser.getUserId(), newBalance)) {
                currentUser.setBalance(newBalance);
                session.setAttribute("currentUser", currentUser);
                request.setAttribute("rechargeSuccessMessage", "充值成功！充值金额：" + rechargeAmount + "元，当前余额：" + newBalance + "元");
            } else {
                request.setAttribute("rechargeErrorMessage", "充值失败，请稍后重试！");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("rechargeErrorMessage", "请输入有效的充值金额！");
        } catch (Exception e) {
            request.setAttribute("rechargeErrorMessage", "充值过程中发生错误，请稍后重试！");
        }

        // 根据用户角色跳转到不同的页面
        if ("ADMIN".equals(currentUser.getRole())) {
            request.getRequestDispatcher("/admin/profile.jsp#rechargeAccount").forward(request, response);
        } else {
            request.getRequestDispatcher("/user/user_profile.jsp#rechargeAccount").forward(request, response);
        }
    }
}

