package com.wanggong232.wangyifei.shopping02.dao;

import com.wanggong232.wangyifei.shopping02.model.User;
import com.wanggong232.wangyifei.shopping02.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // Crucial: Ensure these column names EXACTLY match your database table 'users'
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_AVATAR_PATH = "avatar_path";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    private static final String COLUMN_BALANCE = "balance";

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";
        User user = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + username);
            e.printStackTrace(); // Log error for debugging
        }
        return user;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = ?";
        User user = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + userId);
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_USER_ID + " ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users:");
            e.printStackTrace();
        }
        return users;
    }

    public boolean addUser(User user) {
        // Ensure role is set, default to USER if null
        String role = (user.getRole() == null || user.getRole().trim().isEmpty()) ? "USER" : user.getRole();

        String sql = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_USERNAME + ", " + COLUMN_PASSWORD + ", " + COLUMN_EMAIL + ", "
                + COLUMN_FULL_NAME + ", " + COLUMN_ADDRESS + ", " + COLUMN_PHONE + ", " + COLUMN_AVATAR_PATH + ", " + COLUMN_ROLE
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword()); // Store password as is; consider hashing
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getFullName());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getPhone());
            preparedStatement.setString(7, user.getAvatarPath());
            preparedStatement.setString(8, role);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + user.getUsername());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_USERNAME + " = ?, " + COLUMN_EMAIL + " = ?, "
                + COLUMN_FULL_NAME + " = ?, " + COLUMN_ADDRESS + " = ?, " + COLUMN_PHONE + " = ?, "
                + COLUMN_AVATAR_PATH + " = ?, " + COLUMN_ROLE + " = ?, " + COLUMN_UPDATED_AT + " = CURRENT_TIMESTAMP WHERE " + COLUMN_USER_ID + " = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getFullName());
            preparedStatement.setString(4, user.getAddress());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getAvatarPath());
            preparedStatement.setString(7, user.getRole());
            preparedStatement.setInt(8, user.getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + user.getUserId());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserPassword(int userId, String hashedPassword) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_PASSWORD + " = ?, " + COLUMN_UPDATED_AT + " = CURRENT_TIMESTAMP WHERE " + COLUMN_USER_ID + " = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password for user: " + userId);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + userId);
            e.printStackTrace();
            return false;
        }
    }

    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " LIKE ? OR "
                + COLUMN_FULL_NAME + " LIKE ? OR " + COLUMN_EMAIL + " LIKE ? ORDER BY " + COLUMN_USER_ID + " ASC";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, likeTerm);
            preparedStatement.setString(3, likeTerm);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching users with term: " + searchTerm);
            e.printStackTrace();
        }
        return users;
    }

    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean updateUserBalance(int userId, java.math.BigDecimal newBalance) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_BALANCE + " = ?, " + COLUMN_UPDATED_AT + " = CURRENT_TIMESTAMP WHERE " + COLUMN_USER_ID + " = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, newBalance);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating balance for user: " + userId);
            e.printStackTrace();
            return false;
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt(COLUMN_USER_ID));
        user.setUsername(rs.getString(COLUMN_USERNAME));
        user.setPassword(rs.getString(COLUMN_PASSWORD));
        user.setEmail(rs.getString(COLUMN_EMAIL));
        user.setFullName(rs.getString(COLUMN_FULL_NAME));
        user.setAddress(rs.getString(COLUMN_ADDRESS));
        user.setPhone(rs.getString(COLUMN_PHONE));
        user.setRole(rs.getString(COLUMN_ROLE));
        user.setAvatarPath(rs.getString(COLUMN_AVATAR_PATH));
        user.setCreatedAt(rs.getTimestamp(COLUMN_CREATED_AT));
        user.setUpdatedAt(rs.getTimestamp(COLUMN_UPDATED_AT));
        user.setBalance(rs.getBigDecimal(COLUMN_BALANCE));
        return user;
    }
}

