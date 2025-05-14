package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int orderId;
    private int userId; // Retained for cases where User object might not be fully populated immediately
    private User user; // To hold full user details
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String fullName; // For recipient name
    private String phone;    // For recipient phone
    private String paymentMethod;
    private List<OrderItem> items;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Order() {}

    // Getters
    public int getOrderId() { return orderId; }
    public int getUserId() { 
        // Fallback to user object if available
        if (this.user != null) {
            return this.user.getUserId();
        }
        return userId; 
    }
    public User getUser() { return user; } // This is the method in question
    public Timestamp getOrderDate() { return orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getShippingAddress() { return shippingAddress; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getPaymentMethod() { return paymentMethod; }
    public List<OrderItem> getItems() { return items; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Setters
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setUserId(int userId) { 
        this.userId = userId;
        if (this.user != null && this.user.getUserId() == 0) {
            this.user.setUserId(userId);
        }
    }
    public void setUser(User user) { 
        this.user = user; 
        if (user != null) {
            this.userId = user.getUserId(); // Sync userId field
        }
    }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}

