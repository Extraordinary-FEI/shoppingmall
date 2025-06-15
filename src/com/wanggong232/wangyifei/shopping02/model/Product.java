package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int productId;
    private String name;
    private String description;
    private BigDecimal price; // BigDecimal for price
    private int stockQuantity;
    private String imageUrl;
    private String category; // Changed back to String
    private int categoryId; // 添加类别 ID
    private int subCategoryId; // 添加小类别 ID
    private String subCategoryName; // 添加小类别名称
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 构造函数、Getter 和 Setter 方法
    public Product() {}

    // Getters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public int getCategoryId() { return categoryId; }
    public int getSubCategoryId() { return subCategoryId; }
    public String getSubCategoryName() { return subCategoryName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setSubCategoryId(int subCategoryId) { this.subCategoryId = subCategoryId; }
    public void setSubCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}