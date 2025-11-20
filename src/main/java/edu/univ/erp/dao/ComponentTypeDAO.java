package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}