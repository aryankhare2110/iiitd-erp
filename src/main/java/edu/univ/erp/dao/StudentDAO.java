package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student getStudentByUserID(int userID) {
        String sql = "SELECT student_id, user_id, degree_level, branch, year, term, roll_no, full_name FROM students WHERE user_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("student_id"),
                            rs.getInt("user_id"),
                            rs.getString("degree_level"),
                            rs.getString("branch"),
                            rs.getInt("year"),
                            rs.getString("term"),
                            rs.getString("roll_no"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT student_id, user_id, degree_level, branch, year, term, roll_no, full_name FROM students WHERE student_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("student_id"),
                            rs.getInt("user_id"),
                            rs.getString("degree_level"),
                            rs.getString("branch"),
                            rs.getInt("year"),
                            rs.getString("term"),
                            rs.getString("roll_no"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getAllStudents() {
        String sql = "SELECT student_id, user_id, degree_level, branch, year, term, roll_no, full_name FROM students ORDER BY roll_no";
        List<Student> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getString("degree_level"),
                        rs.getString("branch"),
                        rs.getInt("year"),
                        rs.getString("term"),
                        rs.getString("roll_no"),
                        rs.getString("full_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertStudent(Student s) {
        String sql = "INSERT INTO students (user_id, degree_level, branch, year, term, roll_no, full_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getDegreeLevel());
            ps.setString(3, s.getBranch());
            ps.setInt(4, s.getYear());
            ps.setString(5, s.getTerm());
            ps.setString(6, s.getRollNo());
            ps.setString(7, s.getFullName());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getMessage().contains("unique")) {
                System.out.println("Duplicate roll number or user_id exists!");
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET degree_level = ?, branch = ?, year = ?, term = ?, roll_no = ?, full_name = ? WHERE student_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getDegreeLevel());
            ps.setString(2, s.getBranch());
            ps.setInt(3, s.getYear());
            ps.setString(4, s.getTerm());
            ps.setString(5, s.getRollNo());
            ps.setString(6, s.getFullName());
            ps.setInt(7, s.getStudentId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

