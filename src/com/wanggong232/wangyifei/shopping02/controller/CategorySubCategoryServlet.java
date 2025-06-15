package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.CategoryDao;
import com.wanggong232.wangyifei.shopping02.dao.SubCategoryDao;
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

@WebServlet("/api/subcategories")
public class CategorySubCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CategoryDao categoryDao;
    private SubCategoryDao subCategoryDao;
    private static final Logger LOGGER = Logger.getLogger(CategorySubCategoryServlet.class.getName());

    public void init() {
        categoryDao = new CategoryDao();
        subCategoryDao = new SubCategoryDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String categoryName = request.getParameter("category");

        if (categoryName == null || categoryName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"分类名称不能为空\"}");
            return;
        }

        try {
            // 根据分类名称获取分类信息
            Category category = categoryDao.getCategoryByName(categoryName);

            if (category == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"未找到该分类\"}");
                return;
            }

            // 获取该分类下的所有子分类
            List<SubCategory> subCategories = subCategoryDao.getSubCategoriesByCategoryId(category.getCategoryId());

            // 构建 JSON 响应
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"categoryId\":").append(category.getCategoryId()).append(",");
            jsonBuilder.append("\"categoryName\":\"").append(escapeJson(category.getCategoryName())).append("\",");
            jsonBuilder.append("\"subCategories\":[");

            for (int i = 0; i < subCategories.size(); i++) {
                SubCategory subCategory = subCategories.get(i);
                if (i > 0) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("{");
                jsonBuilder.append("\"subCategoryId\":").append(subCategory.getSubCategoryId()).append(",");
                jsonBuilder.append("\"subCategoryName\":\"").append(escapeJson(subCategory.getSubCategoryName())).append("\"");
                jsonBuilder.append("}");
            }

            jsonBuilder.append("]}");

            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            out.flush();

        } catch (Exception e) {
            LOGGER.severe("获取子分类时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"服务器内部错误\"}");
        }
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

