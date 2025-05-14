package com.wanggong232.wangyifei.shopping02.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Map<Integer, CartItem> items;
    private int userId;

    public Cart() {
        this.items = new HashMap<>();
    }

    public Cart(int userId) {
        this.items = new HashMap<>();
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Map<Integer, CartItem> getItems() {
        return items;
    }

    public void setItems(Map<Integer, CartItem> items) {
        this.items = items;
    }

    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) return;
        CartItem item = items.get(product.getProductId());
        if (item == null) {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            items.put(product.getProductId(), item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void addItem(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null) return;
        CartItem existingItem = items.get(cartItem.getProduct().getProductId());
        if (existingItem == null) {
            items.put(cartItem.getProduct().getProductId(), cartItem);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
        }
    }

    public void updateItemQuantity(int productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            if (quantity > 0) {
                item.setQuantity(quantity);
            } else {
                items.remove(productId);
            }
        }
    }

    public void removeItem(int productId) {
        items.remove(productId);
    }

    public void clearCart() {
        items.clear();
    }

    public int getTotalItems() {
        return items.size();
    }

    public int getTotalQuantity() {
        int totalQuantity = 0;
        for (CartItem item : items.values()) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            if (item.getPriceAtPurchase() != null) {
                BigDecimal itemPrice = item.getPriceAtPurchase();
                BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());
                total = total.add(itemPrice.multiply(itemQuantity));
            }
        }
        return total;
    }
}
