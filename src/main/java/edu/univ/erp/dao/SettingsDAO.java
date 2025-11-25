package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import java.sql.*;
import java.time.LocalDate;

public class SettingsDAO {

    public boolean isMaintenanceMode() {
        String sql = "SELECT value FROM settings WHERE key = 'maintenance_mode'";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("value").equalsIgnoreCase("ON");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setMaintenanceMode(boolean on) {
        String sql = "INSERT INTO settings (key, value) VALUES ('maintenance_mode', ?) ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value";

        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, on ? "ON" : "OFF");
            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getValue(String key) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("value");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean setValue(String key, String value) {
        String sql = "INSERT INTO settings (key, value) VALUES (?, ?) ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, value);
            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public LocalDate getAddDropDeadline() {
        String val = getValue("add_drop_deadline");
        if (val == null) return null;
        return LocalDate.parse(val);
    }

    public boolean setAddDropDeadline(LocalDate date) {
        return setValue("add_drop_deadline", date.toString());
    }
}