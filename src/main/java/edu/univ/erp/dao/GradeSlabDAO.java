package edu.univ.erp.dao;

import edu.univ.erp. data.DBConnection;
import edu.univ.erp.domain.GradeSlab;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeSlabDAO {

    public List<GradeSlab> getSlabsByCourse(int courseId) {
        List<GradeSlab> list = new ArrayList<>();
        String sql = "SELECT * FROM grade_slabs WHERE course_id = ? ORDER BY min_score DESC";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new GradeSlab(
                            rs.getInt("slab_id"),
                            rs.getInt("course_id"),
                            rs.getString("grade_label"),
                            rs.getDouble("min_score"),
                            rs.getDouble("max_score")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertSlab(GradeSlab slab) {
        String sql = "INSERT INTO grade_slabs (course_id, grade_label, min_score, max_score) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slab.getCourseId());
            ps.setString(2, slab.getGradeLabel());
            ps.setDouble(3, slab.getMinScore());
            ps.setDouble(4, slab.getMaxScore());
            return ps. executeUpdate() == 1;
        } catch (SQLException e) {
            e. printStackTrace();
            return false;
        }
    }

    public boolean updateSlab(GradeSlab slab) {
        String sql = "UPDATE grade_slabs SET grade_label = ?, min_score = ?, max_score = ?  WHERE slab_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c. prepareStatement(sql)) {
            ps.setString(1, slab.getGradeLabel());
            ps.setDouble(2, slab.getMinScore());
            ps.setDouble(3, slab.getMaxScore());
            ps.setInt(4, slab.getSlabId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSlab(int slabId) {
        String sql = "DELETE FROM grade_slabs WHERE slab_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slabId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getGradeForScore(int courseId, double score) {
        String sql = "SELECT grade_label FROM grade_slabs WHERE course_id = ?  AND ?  >= min_score AND ? <= max_score";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setDouble(2, score);
            ps.setDouble(3, score);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("grade_label");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "F";
    }

    public boolean deleteSlabsByCourse(int courseId) {
        String sql = "DELETE FROM grade_slabs WHERE course_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}