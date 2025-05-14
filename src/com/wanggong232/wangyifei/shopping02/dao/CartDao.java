package com.wanggong232.wangyifei.shopping02.dao;

import com.wanggong232.wangyifei.shopping02.model.Cart;
import com.wanggong232.wangyifei.shopping02.model.CartItem;
import com.wanggong232.wangyifei.shopping02.model.Product;
import com.wanggong232.wangyifei.shopping02.model.User;
import com.wanggong232.wangyifei.shopping02.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CartDao {

    private ProductDao productDao;

    public CartDao() {
        this.productDao = new ProductDao(); // Instantiate ProductDao to fetch product details
    }

    public Cart getCartByUserId(int userId) {
        Cart cart = new Cart();
        String sql = "SELECT uc.product_id, uc.quantity, p.name, p.price, p.image_url, p.stock_quantity, p.category, p.description " +
                     "FROM user_carts uc JOIN products p ON uc.product_id = p.product_id WHERE uc.user_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setImageUrl(rs.getString("image_url"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setCategory(rs.getString("category"));
                    product.setDescription(rs.getString("description"));

                    int quantity = rs.getInt("quantity");
                    CartItem item = new CartItem(product, quantity);
                    cart.addItem(item); // addItem in Cart handles merging quantities if product already exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error or throw custom exception
        }
        return cart;
    }

    public void saveOrUpdateCartItem(int userId, int productId, int quantity) {
        // Check if item exists, then insert or update
        String selectSql = "SELECT quantity FROM user_carts WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO user_carts (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE user_carts SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND product_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection()) {
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                selectStmt.setInt(2, productId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) { // Item exists, update it
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, productId);
                        updateStmt.executeUpdate();
                    }
                } else { // Item does not exist, insert it
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, quantity);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error or throw custom exception
        }
    }

    public void removeCartItem(int userId, int productId) {
        String sql = "DELETE FROM user_carts WHERE user_id = ? AND product_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error or throw custom exception
        }
    }

    public void clearCart(int userId) {
        String sql = "DELETE FROM user_carts WHERE user_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error or throw custom exception
        }
    }

    /**
     * Merges a session cart into the database cart for a logged-in user.
     * Typically called upon login if a session cart exists.
     * The strategy here is to update DB quantities with session quantities, adding new items.
     */
    public void mergeSessionCartToDB(User user, Cart sessionCart) {
        if (user == null || sessionCart == null || sessionCart.getItems().isEmpty()) {
            return;
        }
        int userId = user.getUserId();
        for (CartItem sessionItem : sessionCart.getItems().values()) {
            // For each item in session cart, update or insert into DB cart
            // This could be optimized to fetch existing DB cart once and merge in memory before batch update
            saveOrUpdateCartItem(userId, sessionItem.getProduct().getProductId(), sessionItem.getQuantity());
        }
    }
}

