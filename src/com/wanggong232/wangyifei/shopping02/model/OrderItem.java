package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal; // Import BigDecimal
import java.sql.Timestamp; // Import Timestamp

public class OrderItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int orderItemId;
    private int orderId;
    private int productId;
    private Product product; // To hold product details like name, image
    private int quantity;
    private BigDecimal priceAtPurchase; // Changed to BigDecimal
    private Timestamp createdAt; // Added createdAt
    private Timestamp updatedAt; // Added updatedAt

    // Default constructor
    public OrderItem() {}

    // Getters
    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public Timestamp getCreatedAt() { // Added getter for createdAt
        return createdAt;
    }

    public Timestamp getUpdatedAt() { // Added getter for updatedAt
        return updatedAt;
    }

    // Setters
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public void setCreatedAt(Timestamp createdAt) { // Added setter for createdAt
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) { // Added setter for updatedAt
        this.updatedAt = updatedAt;
    }

    // Calculated subtotal
    public BigDecimal getSubtotal() {
        if (this.priceAtPurchase != null) {
            return this.priceAtPurchase.multiply(BigDecimal.valueOf(this.quantity));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", product_name=" + (product != null ? product.getName() : "N/A") +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

