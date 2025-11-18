package edu.univ.erp.domain;

import edu.univ.erp.data.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Courses {
    public List<Object[]> viewall() {
        String sql = "SELECT code, title, credits, prerequisites FROM courses";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("code");
                row[1] = rs.getString("title");
                row[2] = rs.getInt("credits");
                row[3] = rs.getString("prerequisites");
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
