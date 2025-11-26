package edu.univ.erp. dao;

import edu.univ.erp.data.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java. sql.SQLException;
import java. util.ArrayList;
import java. util.List;

public class ComponentTypeDAO {

    public String getComponentTypeName(int id) {
        String sql = "SELECT name FROM component_types WHERE type_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public List<String> getAllComponentTypeNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM component_types ORDER BY name";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps. executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public int getComponentTypeIdByName(String typeName) {
        String sql = "SELECT type_id FROM component_types WHERE name = ?";
        try (Connection c = DBConnection. getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, typeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("type_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}