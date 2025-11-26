package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradesDAO {

    public Grade getGradeByEnrollment(int enrollmentId) {
        String sql = "SELECT grade_id, enrollment_id, total_score, grade_label FROM grades WHERE enrollment_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("enrollment_id"),
                            rs.getDouble("total_score"),
                            rs.getString("grade_label")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertGrade(int enrollmentId, double totalScore, String gradeLabel) {
        String sql = "INSERT INTO grades (enrollment_id, total_score, grade_label) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, totalScore);
            ps.setString(3, gradeLabel);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            // If grade already exists → UPDATE instead
            if (e.getMessage().contains("duplicate")) {
                return updateGrade(enrollmentId, totalScore, gradeLabel);
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGrade(int enrollmentId, double totalScore, String gradeLabel) {
        String sql = "UPDATE grades SET total_score = ?, grade_label = ? WHERE enrollment_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, totalScore);
            ps.setString(2, gradeLabel);
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double computeTotalScore(int enrollmentId) {
        String sql = "SELECT sc.weight AS weight, cs.score AS score FROM section_components sc JOIN component_scores cs ON sc.component_id = cs.component_id WHERE cs.enrollment_id = ? ";
        double total = 0;
        double weightSum = 0;
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double weight = rs.getDouble("weight");    // e.g. 30 (30%)
                    double score = rs.getDouble("score");      // e.g. 78
                    total += score * (weight / 100.0);
                    weightSum += weight;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        if (weightSum == 0) return null;
        return total;
    }

    public boolean insertOrUpdateGrade(int enrollmentId, double totalScore, String gradeLabel) {
        String sql = "INSERT INTO grades (enrollment_id, total_score, grade_label) VALUES (?, ?, ?) " +
                "ON CONFLICT (enrollment_id) DO UPDATE SET total_score = ?, grade_label = ?, computed_at = CURRENT_TIMESTAMP";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, totalScore);
            ps.setString(3, gradeLabel);
            ps. setDouble(4, totalScore);
            ps.setString(5, gradeLabel);
            return ps.executeUpdate() >= 1;
        } catch (SQLException e) {
            e. printStackTrace();
            return false;
        }
    }


}