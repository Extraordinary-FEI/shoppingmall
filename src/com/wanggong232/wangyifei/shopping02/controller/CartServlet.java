package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.model.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.wanggong232.wangyifei.shopping02.dao.ProductDao;
import com.wanggong232.wangyifei.shopping02.dao.CartDao;
import com.wanggong232.wangyifei.shopping02.model.Cart;
import com.wanggong232.wangyifei.shopping02.model.Product;
import com.wanggong232.wangyifei.shopping02.model.User;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDao productDao;
    private CartDao cartDao;

    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());


    public void init() {
        productDao = new ProductDao();
        cartDao = new CartDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("获取购物车信息列表");
        // 从会话中获取购物车对象
        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        // 将购物车对象设置为请求属性，供 JSP 页面使用
        req.setAttribute("cart", cart);

        // 转发到购物车页面
        req.getRequestDispatcher("/user/cart.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        String targetJsp = "/user/cart.jsp";
        String redirectPath = request.getContextPath();

        try {
            switch (action) {
                case "add":
                    handleAddAction(request, cart, currentUser, session);
                    String sourcePage = request.getParameter("sourcePage");
                    if ("productDetail".equals(sourcePage) && request.getParameter("productId") != null) {
                        response.sendRedirect(redirectPath + "/product_detail.jsp?productId=" + request.getParameter("productId"));
                    } else {
                        response.sendRedirect(redirectPath + "/products");
                    }
                    return;
                case "update":
                    handleUpdateAction(request, cart, currentUser, session);
                    session.setAttribute("cartMessage", "购物车已更新。");
                    break;
                case "remove":
                    handleRemoveAction(request, cart, currentUser, session);
                    session.setAttribute("cartMessage", "商品已从购物车移除。");
                    break;
                default:
                    session.setAttribute("cartError", "无效的购物车操作。");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("cartError", "购物车操作失败: " + e.getMessage());
        }

        session.setAttribute("cart", cart);
        response.sendRedirect(redirectPath + targetJsp);
    }

    private void handleAddAction(HttpServletRequest request, Cart cart, User currentUser, HttpSession session) {
        int productId = parseInt(request.getParameter("productId"));
        int quantity = parseInt(request.getParameter("quantity"), 1);
        if (productId <= 0 || quantity <= 0) {
            session.setAttribute("cartError", "商品ID或数量无效。");
            return;
        }

        Product product = productDao.getProductById(productId);
        if (product == null) {
            session.setAttribute("cartError", "商品不存在。");
            return;
        }

        if (product.getStockQuantity() >= quantity) {
            cart.addItem(product, quantity);
            if (currentUser != null) {
                CartItem itemInCart = cart.getItems().get(productId);
                if (itemInCart != null) {
                    cartDao.saveOrUpdateCartItem(currentUser.getUserId(), productId, itemInCart.getQuantity());
                    session.setAttribute("cartMessage", "商品 '" + product.getName() + "' 已成功添加到购物车！");
                }
            } else {
                session.setAttribute("cartMessage", "商品 '" + product.getName() + "' 已添加到临时购物车。登录后将保存。");
            }
        } else {
            session.setAttribute("cartError", "库存不足（剩余：" + product.getStockQuantity() + "）。");
        }
    }

    private void handleUpdateAction(HttpServletRequest request, Cart cart, User currentUser, HttpSession session) {
        int productId = parseInt(request.getParameter("productId"));
        int quantity = parseInt(request.getParameter("quantity"));
        if (productId <= 0 || quantity < 0) {
            session.setAttribute("cartError", "更新商品失败：无效输入。");
            return;
        }

        Product product = productDao.getProductById(productId);
        if (product == null) {
            session.setAttribute("cartError", "尝试更新的商品不存在。");
            return;
        }

        if (quantity > product.getStockQuantity()) {
            session.setAttribute("cartError", "库存不足。最多可购买 " + product.getStockQuantity() + " 件。");
            return;
        }

        cart.updateItemQuantity(productId, quantity);
        if (currentUser != null) {
            cartDao.saveOrUpdateCartItem(currentUser.getUserId(), productId, quantity);
        }
    }

    private void handleRemoveAction(HttpServletRequest request, Cart cart, User currentUser, HttpSession session) {
        int productId = parseInt(request.getParameter("productId"));
        cart.removeItem(productId);
        if (currentUser != null) {
            cartDao.removeCartItem(currentUser.getUserId(), productId);
        }
    }

    private int parseInt(String value) {
        return parseInt(value, -1);
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
