package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

public class SubCategory implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int subCategoryId;
    private int categoryId;
    private String subCategoryName;
    private String category; // 添加 category 属性
    private Timestamp createdAt; // 添加 createdAt 属性
    private Timestamp updatedAt; // 添加 updatedAt 属性

    // 无参构造函数
    public SubCategory() {}

    // Getters
    public int getSubCategoryId() {
        return subCategoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getCategory() {
        return category;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 重写 toString 方法，方便调试和打印对象信息
    @Override
    public String toString() {
        return "SubCategory{" +
                "subCategoryId=" + subCategoryId +
                ", categoryId=" + categoryId +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}