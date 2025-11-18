package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() {
        String sql = "SELECT course_id, code, title, credits, prerequisites FROM courses";
        List<Course> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("prerequisites")
                );
                list.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}