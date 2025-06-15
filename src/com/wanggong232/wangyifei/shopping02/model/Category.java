package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int categoryId;
    private String categoryName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Getters and Setters
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}