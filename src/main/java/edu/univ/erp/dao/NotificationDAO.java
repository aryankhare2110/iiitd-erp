package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean insertNotification(String message, String email) {
        String sql = "INSERT INTO notifications (message, sent_by_email) VALUES (?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, message);
            ps.setString(2, email);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Notification> getRecentNotifications(int limit) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT notification_id, message, sent_by_email, sent_at FROM notifications ORDER BY sent_at DESC LIMIT ?";

        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getString("message"),
                            rs.getString("sent_by_email"),
                            rs.getString("sent_at")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT notification_id, message, sent_by_email, sent_at FROM notifications ORDER BY sent_at DESC";

        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Notification(
                        rs.getInt("notification_id"),
                        rs.getString("message"),
                        rs.getString("sent_by_email"),
                        rs.getString("sent_at")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}