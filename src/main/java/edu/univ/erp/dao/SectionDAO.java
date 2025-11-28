package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public Section getSectionById(int sectionId) {
        String sql = "SELECT section_id, course_id, instructor_id, term, year, room, capacity FROM sections WHERE section_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("term"),
                            rs.getInt("year"),
                            rs.getString("room"),
                            rs.getInt("capacity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Section> getSectionsByCourse(int courseId) {
        String sql = "SELECT section_id, course_id, instructor_id, term, year, room, capacity FROM sections WHERE course_id = ? ORDER BY term, year";
        List<Section> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("term"),
                            rs.getInt("year"),
                            rs.getString("room"),
                            rs.getInt("capacity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Section> getSectionsByInstructor(int instructorId) {
        String sql = "SELECT section_id, course_id, instructor_id, term, year, room, capacity FROM sections WHERE instructor_id = ? ORDER BY year DESC, term";
        List<Section> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Section(
                            rs.getInt("section_id"),
                            rs.getInt("course_id"),
                            rs.getInt("instructor_id"),
                            rs.getString("term"),
                            rs.getInt("year"),
                            rs.getString("room"),
                            rs.getInt("capacity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Section> getAllSections() {
        String sql = "SELECT section_id, course_id, instructor_id, term, year, room, capacity FROM sections ORDER BY year DESC, term";
        List<Section> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("term"),
                        rs.getInt("year"),
                        rs.getString("room"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getCapacity(int sectionId) {
        String sql = "SELECT capacity FROM sections WHERE section_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("capacity");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean insertSection(Section s) {
        String sql = "INSERT INTO sections (course_id, instructor_id, term, year, room, capacity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseID());
            ps.setInt(2, s.getInstructorID());
            ps.setString(3, s.getTerm());
            ps.setInt(4, s.getYear());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getCapacity());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSection(Section s) {
        String sql = "UPDATE sections SET course_id = ?, instructor_id = ?, term = ?, year = ?, room = ?, capacity = ? WHERE section_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseID());
            ps.setInt(2, s.getInstructorID());
            ps.setString(3, s.getTerm());
            ps.setInt(4, s.getYear());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getCapacity());
            ps.setInt(7, s.getSectionID());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}