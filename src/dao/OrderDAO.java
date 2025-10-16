package dao;

import config.DBConnection;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // 🔹 Add new order with its items
    public int addOrder(Order order) throws SQLException {
        int orderId = -1;

        String insertOrderSQL = "INSERT INTO orders(table_number, status, total) VALUES (?, ?, ?)";
        String insertItemSQL = "INSERT INTO order_items(order_id, menu_item_id, menu_name, quantity, price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {

            psOrder.setString(1, order.getTableNumber());
            psOrder.setString(2, "Pending");
            psOrder.setDouble(3, order.getTotal());
            psOrder.executeUpdate();

            // 🔹 Get generated order ID
            try (ResultSet rs = psOrder.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }

            // 🔹 Insert order items
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSQL)) {
                for (OrderItem item : order.getItems()) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getMenuItemId());
                    psItem.setString(3, item.getMenuName());
                    psItem.setInt(4, item.getQuantity());
                    psItem.setDouble(5, item.getPrice());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding order: " + e.getMessage());
            throw e;
        }
        return orderId;
    }

    // 🔹 Get orders by their status (Pending, Ready, Paid)
    public List<Order> getOrdersByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrder(rs, conn);
                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching orders by status: " + e.getMessage());
            throw e;
        }
        return orders;
    }

    // 🔹 Get specific order with its items
    public Order getOrderById(int id) throws SQLException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = extractOrder(rs, conn);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching order by ID: " + e.getMessage());
            throw e;
        }
        return order;
    }

    // 🔹 Helper method to extract order and its items
    private Order extractOrder(ResultSet rs, Connection conn) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setTableNumber(rs.getString("table_number"));
        order.setStatus(rs.getString("status"));
        order.setTotal(rs.getDouble("total"));

        // Fetch order items
        List<OrderItem> items = new ArrayList<>();
        String itemSQL = "SELECT * FROM order_items WHERE order_id = ?";

        try (PreparedStatement psItems = conn.prepareStatement(itemSQL)) {
            psItems.setInt(1, order.getId());
            try (ResultSet rsItems = psItems.executeQuery()) {
                while (rsItems.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rsItems.getInt("id"));
                    item.setMenuItemId(rsItems.getInt("menu_item_id"));
                    item.setMenuName(rsItems.getString("menu_name"));
                    item.setQuantity(rsItems.getInt("quantity"));
                    item.setPrice(rsItems.getDouble("price"));
                    items.add(item);
                }
            }
        }

        order.setItems(items);
        return order;
    }

    // 🔹 Mark order as Ready
    public void markAsReady(int orderId) throws SQLException {
        updateOrderStatus(orderId, "Ready");
    }

    // 🔹 Mark order as Paid
    public void markAsPaid(int orderId) throws SQLException {
        updateOrderStatus(orderId, "Paid");
    }

    // 🔹 Reusable status update method
    private void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error updating order status: " + e.getMessage());
            throw e;
        }
    }
}
