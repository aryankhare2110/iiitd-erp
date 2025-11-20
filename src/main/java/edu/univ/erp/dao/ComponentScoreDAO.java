package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.ComponentScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComponentScoreDAO {

    public List<ComponentScore> getScoresByEnrollment(int enrollmentId) {
        String sql = "SELECT score_id, enrollment_id, component_id, score FROM component_scores WHERE enrollment_id = ?";
        List<ComponentScore> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ComponentScore(
                            rs.getInt("score_id"),
                            rs.getInt("enrollment_id"),
                            rs.getInt("component_id"),
                            rs.getDouble("score")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ComponentScore getScore(int enrollmentId, int componentId) {
        String sql = "SELECT score_id, enrollment_id, component_id, score FROM component_scores WHERE enrollment_id = ? AND component_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setInt(2, componentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ComponentScore(
                            rs.getInt("score_id"),
                            rs.getInt("enrollment_id"),
                            rs.getInt("component_id"),
                            rs.getDouble("score")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertScore(ComponentScore cs) {
        String sql = "INSERT INTO component_scores (enrollment_id, component_id, score) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cs.getEnrollmentId());
            ps.setInt(2, cs.getComponentId());
            ps.setDouble(3, cs.getScore());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate")) {
                System.err.println("Score already exists for this component!");
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateScore(ComponentScore cs) {
        String sql = "UPDATE component_scores SET score = ? WHERE score_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, cs.getScore());
            ps.setInt(2, cs.getScoreId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteScore(int scoreId) {
        String sql = "DELETE FROM component_scores WHERE score_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, scoreId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}