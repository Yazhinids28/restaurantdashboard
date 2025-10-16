package dao;

import config.DBConnection;
import model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    public List<MenuItem> getAll() throws SQLException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MenuItem m = new MenuItem();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setCategory(rs.getString("category"));
                m.setPrice(rs.getDouble("price"));
                m.setImagePath(rs.getString("image_path"));
                m.setAvailable(rs.getBoolean("available"));
                list.add(m);
            }
        }
        return list;
    }

    public void add(MenuItem m) throws SQLException {
        String sql = "INSERT INTO menu_items(name, category, price, image_path, available) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setString(4, m.getImagePath());
            ps.setBoolean(5, m.isAvailable());
            ps.executeUpdate();
        }
    }

    public void update(MenuItem m) throws SQLException {
        String sql = "UPDATE menu_items SET name=?, category=?, price=?, image_path=?, available=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setString(4, m.getImagePath());
            ps.setBoolean(5, m.isAvailable());
            ps.setInt(6, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ✅ Added method to fetch by name
    public MenuItem getByName(String name) throws SQLException {
        String sql = "SELECT * FROM menu_items WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MenuItem m = new MenuItem();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setCategory(rs.getString("category"));
                m.setPrice(rs.getDouble("price"));
                m.setImagePath(rs.getString("image_path"));
                m.setAvailable(rs.getBoolean("available"));
                return m;
            }
        }
        return null;
    }
}
