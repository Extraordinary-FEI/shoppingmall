package com.wanggong232.wangyifei.shopping02.dao;

import com.wanggong232.wangyifei.shopping02.model.Category;
import com.wanggong232.wangyifei.shopping02.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    public Category getCategoryByName(String categoryName) {
        String sql = "SELECT * FROM categories WHERE category_name = ?";
        Category category = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, categoryName);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    category = mapRowToCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        // 若 Category 类还有其他属性，可在此处继续设置
        return category;
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
}
