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
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "ORDER BY p.category_id ASC, p.name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }


    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE c.category_name = ? " +
                "ORDER BY p.name ASC";

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
        String sql = "SELECT DISTINCT c.category_name FROM categories c " +
                "INNER JOIN products p ON c.category_id = p.category_id " +
                "ORDER BY c.category_name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE p.product_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock_quantity, category, category_id, sub_category_id, image_url, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

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
    // 获取所有大类

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
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
    // 新增大类
    public int addCategory(String categoryName) {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, categoryName);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    // 新增子类
    public int addSubCategory(int categoryId, String subCategoryName) {
        String sql = "INSERT INTO sub_categories (category_id, sub_category_name) VALUES (?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, categoryId);
            preparedStatement.setString(2, subCategoryName);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 获取某个大类下的所有子类
    public List<SubCategory> getSubCategories(int categoryId) {
        List<SubCategory> subCategories = new ArrayList<>();
        String sql = "SELECT * FROM sub_categories WHERE category_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SubCategory subCategory = new SubCategory();
                    subCategory.setSubCategoryId(rs.getInt("sub_category_id"));
                    subCategory.setCategoryId(rs.getInt("category_id"));
                    subCategory.setSubCategoryName(rs.getString("sub_category_name"));
                    subCategories.add(subCategory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subCategories;
    }

    public List<SubCategory> getSubCategoriesByCategoryId(int categoryId) {
        List<SubCategory> subCategories = new ArrayList<>();
        String sql = "SELECT sc.*, c.category_name " +
                "FROM sub_categories sc " +
                "LEFT JOIN categories c ON sc.category_id = c.category_id " +
                "WHERE sc.category_id = ? " +
                "ORDER BY sc.sub_category_name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SubCategory subCategory = new SubCategory();
                    subCategory.setSubCategoryId(rs.getInt("sub_category_id"));
                    subCategory.setCategoryId(rs.getInt("category_id"));
                    subCategory.setSubCategoryName(rs.getString("sub_category_name"));
                    subCategory.setCategory(rs.getString("category_name"));
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

    public SubCategory getSubCategoryById(int subCategoryId) {
        String sql = "SELECT sc.*, c.category_name " +
                "FROM sub_categories sc " +
                "LEFT JOIN categories c ON sc.category_id = c.category_id " +
                "WHERE sc.sub_category_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, subCategoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    SubCategory subCategory = new SubCategory();
                    subCategory.setSubCategoryId(rs.getInt("sub_category_id"));
                    subCategory.setCategoryId(rs.getInt("category_id"));
                    subCategory.setSubCategoryName(rs.getString("sub_category_name"));
                    subCategory.setCategory(rs.getString("category_name"));
                    subCategory.setCreatedAt(rs.getTimestamp("created_at"));
                    subCategory.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return subCategory;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ?, " +
                "category = ?, category_id = ?, sub_category_id = ?, image_url = ?, updated_at = NOW() " +
                "WHERE product_id = ?";

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
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE (p.name LIKE ? OR p.description LIKE ?) " +
                "ORDER BY p.category_id ASC, p.name ASC";

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

    public List<Product> searchProductsByCategoryId(String searchTerm, int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE (p.name LIKE ? OR p.description LIKE ?) AND p.category_id = ? " +
                "ORDER BY p.name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, likeTerm);
            preparedStatement.setInt(3, categoryId);
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

    public List<Product> searchProductsBySubCategory(String searchTerm, int subCategoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE (p.name LIKE ? OR p.description LIKE ?) AND p.sub_category_id = ? " +
                "ORDER BY p.name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, likeTerm);
            preparedStatement.setInt(3, subCategoryId);
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
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE (p.name LIKE ? OR p.description LIKE ?) AND c.category_name = ? " +
                "ORDER BY p.name ASC";

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
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ?, updated_at = NOW() " +
                "WHERE product_id = ? AND stock_quantity + ? >= 0";

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
        String sql = "SELECT COUNT(*) FROM products";
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
        product.setCategoryId(rs.getInt("category_id"));
        product.setSubCategoryId(rs.getInt("sub_category_id"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }

    /**
     * 根据大分类ID查询商品
     */
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE p.category_id = ? " +
                "ORDER BY p.name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, categoryId);
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

    public List<Product> getProductsBySubCategoryId(int subCategoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, sc.sub_category_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN sub_categories sc ON p.sub_category_id = sc.sub_category_id " +
                "WHERE p.sub_category_id = ? " +
                "ORDER BY p.name ASC";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, subCategoryId);
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
}