package com.wanggong232.wangyifei.shopping02.dao;

import com.wanggong232.wangyifei.shopping02.model.SubCategory;
import com.wanggong232.wangyifei.shopping02.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class SubCategoryDao {
    public List<SubCategory> getSubCategoriesByCategoryId(int categoryId) {
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
}