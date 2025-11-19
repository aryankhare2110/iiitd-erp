package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.SectionComponent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionComponentDAO {

    public List<SectionComponent> getComponentsBySection(int sectionID) {
        String sql = "SELECT component_id, section_id, type_id, day, start_time, end_time, weight, description FROM section_components WHERE section_id = ? ORDER BY component_id";
        List<SectionComponent> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectionID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SectionComponent(
                            rs.getInt("component_id"),
                            rs.getInt("section_id"),
                            rs.getInt("type_id"),
                            rs.getString("day"),
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getDouble("weight"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertComponent(SectionComponent sc) {
        String sql = "INSERT INTO section_components (section_id, type_id, day, start_time, end_time, weight, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sc.getSectionID());
            ps.setInt(2, sc.getTypeID());
            ps.setString(3, sc.getDay());
            ps.setString(4, sc.getStartTime());
            ps.setString(5, sc.getEndTime());
            ps.setDouble(6, sc.getWeight());
            ps.setString(7, sc.getDescription());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateComponent(SectionComponent sc) {
        String sql = "UPDATE section_components SET type_id = ?, day = ?, start_time = ?, end_time = ?, weight = ?, description = ? WHERE component_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sc.getTypeID());
            ps.setString(2, sc.getDay());
            ps.setString(3, sc.getStartTime());
            ps.setString(4, sc.getEndTime());
            ps.setDouble(5, sc.getWeight());
            ps.setString(6, sc.getDescription());
            ps.setInt(7, sc.getComponentID());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteComponent(int componentID) {
        String sql = "DELETE FROM section_components WHERE component_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, componentID);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}