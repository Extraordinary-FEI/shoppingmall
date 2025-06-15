package com.wanggong232.wangyifei.shopping02.dao;

import com.wanggong232.wangyifei.shopping02.model.Category;
import com.wanggong232.wangyifei.shopping02.model.Product;
import com.wanggong232.wangyifei.shopping02.model.SubCategory;
import com.wanggong232.wangyifei.shopping02.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDao {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id, category ASC, name ASC"; // Added sorting for consistency
        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Proper logging should be implemented
        }
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ? ORDER BY name ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Set<String> getDistinctCategories() {
        Set<String> categories = new HashSet<>();
        String sql = "SELECT DISTINCT category FROM products ORDER BY category ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Product product = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    product = mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock_quantity, category, category_id, sub_category_id, image_url, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setBigDecimal(3, product.getPrice());
            preparedStatement.setInt(4, product.getStockQuantity());
            preparedStatement.setString(5, product.getCategory());
            preparedStatement.setInt(6, product.getCategoryId());
            preparedStatement.setInt(7, product.getSubCategoryId());
            preparedStatement.setString(8, product.getImageUrl());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取所有小类别
    public List<SubCategory> getAllSubCategories() {
        List<SubCategory> subCategories = new ArrayList<>();
        String sql = "SELECT * FROM sub_categories";
        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                SubCategory subCategory = new SubCategory();
                subCategory.setSubCategoryId(rs.getInt("sub_category_id"));
                subCategory.setCategoryId(rs.getInt("category_id"));
                subCategory.setSubCategoryName(rs.getString("sub_category_name"));
                subCategories.add(subCategory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subCategories;
    }
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                category.setUpdatedAt(rs.getTimestamp("updated_at"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    public List<SubCategory> getSubCategoriesByCategoryId(int categoryId) {
        List<SubCategory> subCategories = new ArrayList<>();
        String sql = "SELECT * FROM sub_categories WHERE category_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(categoryId);
                    String categoryName = getCategoryNameById(categoryId);

                    SubCategory subCategory = new SubCategory();
                    subCategory.setSubCategoryId(rs.getInt("sub_category_id"));
                    subCategory.setCategory(categoryName);
                    subCategory.setSubCategoryName(rs.getString("sub_category_name"));
                    subCategory.setCreatedAt(rs.getTimestamp("created_at"));
                    subCategory.setUpdatedAt(rs.getTimestamp("updated_at"));
                    subCategories.add(subCategory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subCategories;
    }
    private String getCategoryNameById(int categoryId) {
        String sql = "SELECT category_name FROM categories WHERE category_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("category_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ?, category = ?, category_id = ?, sub_category_id = ?, image_url = ?, updated_at = NOW() WHERE product_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setBigDecimal(3, product.getPrice());
            preparedStatement.setInt(4, product.getStockQuantity());
            preparedStatement.setString(5, product.getCategory());
            preparedStatement.setInt(6, product.getCategoryId());
            preparedStatement.setInt(7, product.getSubCategoryId());
            preparedStatement.setString(8, product.getImageUrl());
            preparedStatement.setInt(9, product.getProductId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 修改后的搜索方法 - 在所有商品中搜索（只搜索商品名称和描述）
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE (name LIKE ? OR description LIKE ?) ORDER BY category ASC, name ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, likeTerm);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // 新增方法 - 在特定分类中搜索
    public List<Product> searchProductsByCategory(String searchTerm, String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE (name LIKE ? OR description LIKE ?) AND category = ? ORDER BY name ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, likeTerm);
            preparedStatement.setString(3, category);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // 保留原有的全字段搜索方法（如果其他地方需要用到）
    public List<Product> searchProductsAllFields(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ? OR category LIKE ? ORDER BY category ASC, name ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, likeTerm);
            preparedStatement.setString(3, likeTerm);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean updateStockQuantity(int productId, int quantityChange) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ?, updated_at = NOW() WHERE product_id = ? AND stock_quantity + ? >= 0";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, quantityChange);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantityChange);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM products"; // Assumes all products in the table are considered 'on sale'
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // private SubCategory mapRowToSubCategory(ResultSet rs) throws SQLException {
    //     SubCategory subCategory = new SubCategory();
    //     subCategory.setSubCategoryId(rs.getInt("sub_category_id"));
    //     // 假设数据库中有 category_id 字段存储分类 ID
    //     subCategory.setCategoryId(rs.getInt("category_id"));
    //     subCategory.setSubCategoryName(rs.getString("sub_category_name"));
    //     // 假设数据库中有 category_name 字段存储分类名称
    //     subCategory.setCategory(rs.getString("category_name"));
    //     subCategory.setCreatedAt(rs.getTimestamp("created_at"));
    //     subCategory.setUpdatedAt(rs.getTimestamp("updated_at"));
    //     return subCategory;
    // }
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        // 修正 setCategory 方法的调用
        product.setCategory(rs.getString("category"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }
}