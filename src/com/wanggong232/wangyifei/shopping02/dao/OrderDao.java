package com.wanggong232.wangyifei.shopping02.dao;

import com.wanggong232.wangyifei.shopping02.exception.InsufficientStockException;
import com.wanggong232.wangyifei.shopping02.model.Order;
import com.wanggong232.wangyifei.shopping02.model.OrderItem;
import com.wanggong232.wangyifei.shopping02.model.Product;
import com.wanggong232.wangyifei.shopping02.model.User;
import com.wanggong232.wangyifei.shopping02.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private UserDao userDao;

    public OrderDao() {
        userDao = new UserDao();
    }

    public boolean addOrder(Order order) throws InsufficientStockException, SQLException {
        // Updated SQL to include recipient_name and phone
        String insertOrderSQL = "INSERT INTO orders (user_id, order_date, total_amount, status, shipping_address, payment_method, recipient_name, phone, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        String insertOrderItemSQL = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase, created_at, updated_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        String checkStockSQL = "SELECT stock_quantity FROM products WHERE product_id = ? FOR UPDATE";
        String updateStockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";

        Connection connection = null;
        PreparedStatement psOrder = null;
        PreparedStatement psOrderItem = null;
        PreparedStatement psCheckStock = null;
        PreparedStatement psUpdateStock = null;
        ResultSet generatedKeys = null;
        boolean orderPlaced = false;

        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false);

            for (OrderItem item : order.getItems()) {
                psCheckStock = connection.prepareStatement(checkStockSQL);
                psCheckStock.setInt(1, item.getProductId());
                ResultSet rsStock = psCheckStock.executeQuery();
                if (rsStock.next()) {
                    int currentStock = rsStock.getInt("stock_quantity");
                    if (currentStock < item.getQuantity()) {
                        throw new InsufficientStockException("商品ID: " + item.getProductId() + " 库存不足。请求数量: " + item.getQuantity() + ", 当前库存: " + currentStock);
                    }
                } else {
                    throw new SQLException("商品ID: " + item.getProductId() + " 未找到。");
                }
                if (rsStock != null) rsStock.close();
                if (psCheckStock != null) psCheckStock.close();
            }

            psOrder = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getUserId());
            psOrder.setTimestamp(2, order.getOrderDate() != null ? order.getOrderDate() : new Timestamp(System.currentTimeMillis()));
            psOrder.setBigDecimal(3, order.getTotalAmount());
            psOrder.setString(4, order.getStatus() != null ? order.getStatus() : "PENDING");
            psOrder.setString(5, order.getShippingAddress());
            psOrder.setString(6, order.getPaymentMethod() != null ? order.getPaymentMethod() : "PENDING_PAYMENT");
            psOrder.setString(7, order.getFullName()); // recipient_name
            psOrder.setString(8, order.getPhone());     // phone

            int affectedRows = psOrder.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("创建订单失败，没有行受到影响。");
            }

            generatedKeys = psOrder.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setOrderId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("创建订单失败，没有获取到订单ID。");
            }

            psOrderItem = connection.prepareStatement(insertOrderItemSQL);
            psUpdateStock = connection.prepareStatement(updateStockSQL);

            for (OrderItem item : order.getItems()) {
                psOrderItem.setInt(1, order.getOrderId());
                psOrderItem.setInt(2, item.getProductId());
                psOrderItem.setInt(3, item.getQuantity());
                psOrderItem.setBigDecimal(4, item.getPriceAtPurchase());
                psOrderItem.addBatch();

                psUpdateStock.setInt(1, item.getQuantity());
                psUpdateStock.setInt(2, item.getProductId());
                psUpdateStock.setInt(3, item.getQuantity());
                int stockUpdateAffectedRows = psUpdateStock.executeUpdate();
                if (stockUpdateAffectedRows == 0) {
                    throw new InsufficientStockException("更新商品ID: " + item.getProductId() + " 库存失败或并发修改导致库存不足。");
                }
            }
            psOrderItem.executeBatch();

            connection.commit();
            orderPlaced = true;

        } catch (SQLException | InsufficientStockException e) {
            if (connection != null) {
                try {
                    System.err.println("事务因以下原因回滚: " + e.getMessage());
                    connection.rollback();
                } catch (SQLException excep) {
                    System.err.println("回滚事务时出错: " + excep.getMessage());
                }
            }
            throw e;
        } finally {
            closeResources(connection, psOrder, psOrderItem, psCheckStock, psUpdateStock, generatedKeys);
        }
        return orderPlaced;
    }

    public boolean updateOrderStatusAndPayment(int orderId, String newStatus, String paymentMethod) {
        String sql = "UPDATE orders SET status = ?, payment_method = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, paymentMethod);
            preparedStatement.setInt(3, orderId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username as user_username FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs, connection, true));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private Order mapRowToOrder(ResultSet rs, Connection conn, boolean loadItems) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));

        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        if (hasColumn(rs, "user_username")) {
            user.setUsername(rs.getString("user_username"));
        } else {
            User fullUser = userDao.getUserById(rs.getInt("user_id"));
            if (fullUser != null) {
                user.setUsername(fullUser.getUsername());
            }
        }
        order.setUser(user);

        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Retrieve recipient_name and phone if columns exist
        if (hasColumn(rs, "recipient_name")) {
            order.setFullName(rs.getString("recipient_name"));
        }
        if (hasColumn(rs, "phone")) {
            order.setPhone(rs.getString("phone"));
        }

        if (loadItems && conn != null && !conn.isClosed()) {
            order.setItems(getOrderItemsByOrderId(order.getOrderId(), conn));
        }
        return order;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnLabel(x))) {
                return true;
            }
        }
        return false;
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId, Connection connection) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name as product_name, p.image_url as product_image_url " +
                "FROM order_items oi JOIN products p ON oi.product_id = p.product_id WHERE oi.order_id = ?";
        boolean newConnectionCreated = false;
        Connection connToUse = connection;

        if (connToUse == null || connToUse.isClosed()) {
            connToUse = DBConnectionUtil.getConnection();
            newConnectionCreated = true;
        }

        try (PreparedStatement preparedStatement = connToUse.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
                    // Assuming order_items also has created_at and updated_at
                    if (hasColumn(rs, "created_at")) {
                        item.setCreatedAt(rs.getTimestamp("created_at"));
                    }
                    if (hasColumn(rs, "updated_at")) {
                        item.setUpdatedAt(rs.getTimestamp("updated_at"));
                    }

                    Product product = new Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setName(rs.getString("product_name"));
                    product.setImageUrl(rs.getString("product_image_url"));
                    item.setProduct(product);
                    items.add(item);
                }
            }
        } finally {
            if (newConnectionCreated && connToUse != null) {
                try {
                    connToUse.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return items;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username as user_username FROM orders o JOIN users u ON o.user_id = u.user_id ORDER BY o.order_date DESC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs, connection, true));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order getOrderById(int orderId) {
        Order order = null;
        String sql = "SELECT o.*, u.username as user_username FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.order_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    order = mapRowToOrder(rs, connection, true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public int countPendingOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "PENDING");
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void closeResources(Connection conn, PreparedStatement ps1, PreparedStatement ps2, PreparedStatement ps3, PreparedStatement ps4, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps1 != null) ps1.close();
            if (ps2 != null) ps2.close();
            if (ps3 != null) ps3.close();
            if (ps4 != null) ps4.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

