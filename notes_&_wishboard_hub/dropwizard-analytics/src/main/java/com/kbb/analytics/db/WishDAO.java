package com.kbb.analytics.db;

import com.kbb.shared.entity.Wish;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishDAO {

    private final String url;
    private final String user;
    private final String password;

    public WishDAO(String url, String user, String password) {
        this.url      = url;
        this.user     = user;
        this.password = password;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS wishes (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                     "name VARCHAR(200) NOT NULL," +
                     "description TEXT," +
                     "achieved BOOLEAN NOT NULL DEFAULT FALSE," +
                     "user_id BIGINT NOT NULL" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public List<Wish> findAll() throws SQLException {
        List<Wish> list = new ArrayList<>();
        try (Connection c = connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM wishes")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Wish> findByUser(Long userId) throws SQLException {
        List<Wish> list = new ArrayList<>();
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM wishes WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Wish insert(Wish w) throws SQLException {
        String sql = "INSERT INTO wishes (name, description, achieved, user_id) VALUES (?,?,?,?)";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, w.getName());
            ps.setString(2, w.getDescription());
            ps.setBoolean(3, Boolean.TRUE.equals(w.getAchieved()));
            ps.setLong(4, w.getUserId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) w.setId(keys.getLong(1));
            }
        }
        return w;
    }

    public void markAchieved(Long id) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("UPDATE wishes SET achieved = TRUE WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM wishes WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public long countTotal() throws SQLException {
        try (Connection c = connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM wishes")) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    public long countAchieved() throws SQLException {
        try (Connection c = connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM wishes WHERE achieved = TRUE")) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    private Wish map(ResultSet rs) throws SQLException {
        Wish w = new Wish();
        w.setId(rs.getLong("id"));
        w.setName(rs.getString("name"));
        w.setDescription(rs.getString("description"));
        w.setAchieved(rs.getBoolean("achieved"));
        w.setUserId(rs.getLong("user_id"));
        return w;
    }
}
