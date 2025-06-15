package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.ProductDao;
import com.wanggong232.wangyifei.shopping02.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDao productDao;
    private static final Logger LOGGER = Logger.getLogger(ProductServlet.class.getName());

    public void init() {
        productDao = new ProductDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("category");
        String searchQuery = request.getParameter("search"); // 获取搜索参数
        List<Product> productList;
        Set<String> distinctCategories;

        try {
            distinctCategories = productDao.getDistinctCategories();
            request.setAttribute("distinctCategories", distinctCategories);

            // 处理搜索和分类的组合逻辑
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                // 有搜索关键词
                if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
                    // 在特定分类中搜索
                    productList = productDao.searchProductsByCategory(searchQuery.trim(), category);
                } else {
                    // 在所有商品中搜索
                    productList = productDao.searchProducts(searchQuery.trim());
                }
            } else {
                // 没有搜索关键词，按原有逻辑处理
                if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
                    productList = productDao.getProductsByCategory(category);
                } else {
                    productList = productDao.getAllProducts();
                }
            }

            request.setAttribute("productList", productList);

        } catch (Exception e) {
            LOGGER.severe("Error fetching products or categories: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "无法加载商品信息，请稍后再试。");
        }

        request.getRequestDispatcher("/user/products.jsp").forward(request, response);
    }
}