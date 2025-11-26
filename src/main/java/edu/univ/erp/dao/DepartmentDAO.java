package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<String> getAllDepartmentNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM departments ORDER BY name";

        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getDepartmentIdByName(String name) {
        String sql = "SELECT department_id FROM departments WHERE name = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("department_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getDepartmentNameById(int id) {
        String sql = "SELECT name FROM departments WHERE department_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}