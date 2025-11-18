package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    public boolean checkCapacity(int section_id) {
        int capacity = SectionDAO.getCapacity(section_id);
        String sql = "SELECT COUNT(*) AS enrolled FROM enrollments WHERE section_id = ?";
        try (Connection c = DBConnection.getErpConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, section_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int enrolled = rs.getInt("enrolled");
                return enrolled < capacity; // true if seats available
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // safe default: no capacity or error
    }
    public boolean enroll(int student_id, int section_id) {
        if (!checkCapacity(section_id)) {
            return false; // no seats left
        }
        String sql = "INSERT INTO enrollments(student_id, section_id) VALUES (?, ?)";
        try (Connection c = DBConnection.getErpConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, student_id);
            ps.setInt(2, section_id);
            int rows = ps.executeUpdate();
            return rows > 0; // success if at least one row inserted
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // error inserting
    }
    public boolean dropEnrollment(int student_id, int section_id) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection c = DBConnection.getErpConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, student_id);
            ps.setInt(2, section_id);
            int rows = ps.executeUpdate();
            return rows > 0;   // true only if a row was actually deleted

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // deletion failed
    }
    public static List<Integer> enrolled(int student_id) {
        String sql = "SELECT section_id FROM enrollments WHERE student_id = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection c = DBConnection.getErpConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, student_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("section_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;  // empty if no enrollments
    }
}
