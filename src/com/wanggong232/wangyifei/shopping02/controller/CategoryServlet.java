package com.wanggong232.wangyifei.shopping02.controller;


import com.google.gson.Gson;
import com.wanggong232.wangyifei.shopping02.dao.CategoryDao;
import com.wanggong232.wangyifei.shopping02.dao.ProductDao;
import com.wanggong232.wangyifei.shopping02.model.Category;
import com.wanggong232.wangyifei.shopping02.model.SubCategory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());

    @Override
    public void init() {
        categoryDao = new CategoryDao();
        productDao = new ProductDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("getSubCategories".equals(action)) {
            handleGetSubCategories(request, response);
        } else if ("getAllCategories".equals(action)) {
            handleGetAllCategories(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    /**
     * 获取指定大类下的所有子类（AJAX请求）
     */
    private void handleGetSubCategories(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String categoryIdStr = request.getParameter("categoryId");
            if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                out.print("[]");
                return;
            }

            int categoryId = Integer.parseInt(categoryIdStr);
            List<SubCategory> subCategories = productDao.getSubCategoriesByCategoryId(categoryId);

            Gson gson = new Gson();
            String json = gson.toJson(subCategories);
            out.print(json);

        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid category ID format: " + request.getParameter("categoryId"));
            out.print("[]");
        } catch (Exception e) {
            LOGGER.severe("Error fetching sub categories: " + e.getMessage());
            e.printStackTrace();
            out.print("[]");
        }
    }

    /**
     * 获取所有大类（AJAX请求）
     */
    private void handleGetAllCategories(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Category> categories = categoryDao.getAllCategories();
            Gson gson = new Gson();
            String json = gson.toJson(categories);
            out.print(json);

        } catch (Exception e) {
            LOGGER.severe("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
            out.print("[]");
        }
    }
}
