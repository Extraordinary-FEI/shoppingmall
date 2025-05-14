package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;
    private int productId;
    private BigDecimal priceAtPurchase; // Changed to BigDecimal

    public CartItem() {}

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        if (product != null) {
            this.productId = product.getProductId();
            this.priceAtPurchase = product.getPrice(); // Assuming product.getPrice() now returns BigDecimal
        }
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.setProductId(product.getProductId());
            this.setPriceAtPurchase(product.getPrice()); // Assuming product.getPrice() now returns BigDecimal
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    } // Changed to BigDecimal

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    } // Changed to BigDecimal

    public BigDecimal getSubtotal() {
        if (priceAtPurchase != null) {
            return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}

