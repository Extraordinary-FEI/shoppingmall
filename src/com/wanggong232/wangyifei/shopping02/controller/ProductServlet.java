package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.CategoryDao;
import com.wanggong232.wangyifei.shopping02.dao.ProductDao;
import com.wanggong232.wangyifei.shopping02.model.Category;
import com.wanggong232.wangyifei.shopping02.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private static final Logger LOGGER = Logger.getLogger(ProductServlet.class.getName());

    @Override
    public void init() {
        productDao = new ProductDao();
        categoryDao = new CategoryDao();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取请求参数
        String categoryIdStr = request.getParameter("categoryId");
        String subCategoryIdStr = request.getParameter("subCategoryId");
        String category = request.getParameter("category"); // 保持向后兼容
        String searchQuery = request.getParameter("search");

        List<Product> productList;
        List<Category> allCategories;

        try {
            // 获取所有分类用于导航显示
            allCategories = categoryDao.getAllCategories();
            request.setAttribute("allCategories", allCategories);

            // 根据不同参数组合查询商品
            productList = getProductsByParameters(categoryIdStr, subCategoryIdStr, category, searchQuery);

            request.setAttribute("productList", productList);

            // 设置当前选中的分类信息（用于前端显示）
            setCurrentCategoryInfo(request, categoryIdStr, subCategoryIdStr, category);

        } catch (Exception e) {
            LOGGER.severe("Error fetching products or categories: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "无法加载商品信息，请稍后再试。");
        }

        request.getRequestDispatcher("/user/products.jsp").forward(request, response);
    }

    /**
     * 根据不同参数组合查询商品
     */
    private List<Product> getProductsByParameters(String categoryIdStr, String subCategoryIdStr,
                                                  String category, String searchQuery) {

        // 优先级：搜索 > 子分类ID > 大分类ID > 分类名称 > 全部商品

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // 有搜索关键词
            if (subCategoryIdStr != null && !subCategoryIdStr.trim().isEmpty()) {
                try {
                    int subCategoryId = Integer.parseInt(subCategoryIdStr);
                    return productDao.searchProductsBySubCategory(searchQuery.trim(), subCategoryId);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Invalid subCategoryId format: " + subCategoryIdStr);
                }
            } else if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(categoryIdStr);
                    return productDao.searchProductsByCategoryId(searchQuery.trim(), categoryId);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Invalid categoryId format: " + categoryIdStr);
                }
            } else if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
                return productDao.searchProductsByCategory(searchQuery.trim(), category);
            } else {
                return productDao.searchProducts(searchQuery.trim());
            }
        }

        // 无搜索关键词，按分类查询
        if (subCategoryIdStr != null && !subCategoryIdStr.trim().isEmpty()) {
            try {
                int subCategoryId = Integer.parseInt(subCategoryIdStr);
                return productDao.getProductsBySubCategoryId(subCategoryId);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid subCategoryId format: " + subCategoryIdStr);
            }
        }

        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                int categoryId = Integer.parseInt(categoryIdStr);
                return productDao.getProductsByCategoryId(categoryId);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid categoryId format: " + categoryIdStr);
            }
        }

        if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
            return productDao.getProductsByCategory(category);
        }

        // 默认返回所有商品
        return productDao.getAllProducts();
    }

    /**
     * 设置当前分类信息到request中
     */
    private void setCurrentCategoryInfo(HttpServletRequest request, String categoryIdStr,
                                        String subCategoryIdStr, String category) {
        try {
            if (subCategoryIdStr != null && !subCategoryIdStr.trim().isEmpty()) {
                int subCategoryId = Integer.parseInt(subCategoryIdStr);
                request.setAttribute("currentSubCategoryId", subCategoryId);

                // 通过子分类获取对应的大分类
                var subCategory = productDao.getSubCategoryById(subCategoryId);
                if (subCategory != null) {
                    request.setAttribute("currentCategoryId", subCategory.getCategoryId());
                }
            } else if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                int categoryId = Integer.parseInt(categoryIdStr);
                request.setAttribute("currentCategoryId", categoryId);
            } else if (category != null && !category.isEmpty()) {
                request.setAttribute("currentCategoryName", category);
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Error parsing category parameters: " + e.getMessage());
        }
    }
}