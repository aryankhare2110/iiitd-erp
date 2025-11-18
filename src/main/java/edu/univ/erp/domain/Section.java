package edu.univ.erp.domain;

import edu.univ.erp.data.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Section {
    public static int getCapacity(int section_id) {
        String sql = "SELECT capacity FROM sections WHERE section_id = ?";
        try (Connection c = DBConnection.getErpConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, section_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("capacity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // safe default: no capacity or error
    }
    public List<Map<String, Object>> viewTimeTable(int student_id) {
        List<Integer> sections = Enrollment.enrolled(student_id);  // get all section IDs
        List<Map<String, Object>> timetable = new ArrayList<>();
        String sql = "SELECT s.section_id, c.name, s.day, s.start_time, s.end_time, t.room, FROM component_types c JOIN sections_components s ON c.type_id = s.type_id JOIN sections t ON s.section_id = t.section_id WHERE section_id = ? AND c.name IN ('LECTURE', 'TUTORIAL', 'LAB')";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int section_id : sections) {
                ps.setInt(1, section_id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("section_id", rs.getInt("section_id"));
                    row.put("component_type", rs.getString("name"));
                    row.put("day", rs.getString("day"));
                    row.put("start_time", rs.getString("start_time"));
                    row.put("end_time", rs.getString("end_time"));
                    row.put("room", rs.getString("room"));
                    timetable.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timetable; // empty if not enrolled in any courses
    }

    public List<Map<String, Object>> viewMySections(int instructor_id) {
        String sql = "SELECT c.code, c.title, s.term, s.year, s.room, s.capacity FROM sections s JOIN courses c ON s.course_id = c.course_id WHERE s.instructor_id = ?";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, instructor_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("code", rs.getString("code"));
                row.put("title", rs.getString("title"));
                row.put("term", rs.getString("term"));
                row.put("year", rs.getInt("year"));
                row.put("room", rs.getString("room"));
                row.put("capacity", rs.getInt("capacity"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list; // empty if instructor teaches no sections
    }
}
