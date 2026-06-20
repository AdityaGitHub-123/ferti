package com.fertilizer.ruleengine.repository;

import com.fertilizer.ruleengine.entity.SoilTest;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SoilTestRepository {
    private final DataSource dataSource;

    public SoilTestRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SoilTest save(SoilTest soilTest) {
        String sql = "INSERT INTO soil_test (user_id, crop_type, nitrogen, phosphorus, potassium, ph_value, soil_health_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, soilTest.getUserId());
            ps.setString(2, soilTest.getCropType());
            ps.setDouble(3, soilTest.getNitrogen());
            ps.setDouble(4, soilTest.getPhosphorus());
            ps.setDouble(5, soilTest.getPotassium());
            ps.setDouble(6, soilTest.getPhValue());
            ps.setString(7, soilTest.getSoilHealthStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    soilTest.setId(keys.getLong(1));
                }
            }
            return soilTest;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to save soil test", ex);
        }
    }

    public Map<String, Integer> countByHealthStatus(Long userId) {
        String sql = "SELECT soil_health_status, COUNT(*) total FROM soil_test WHERE user_id = ? GROUP BY soil_health_status";
        Map<String, Integer> stats = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("soil_health_status"), rs.getInt("total"));
                }
            }
            return stats;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load soil stats", ex);
        }
    }

    public int countByUser(Long userId) {
        String sql = "SELECT COUNT(*) FROM soil_test WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to count soil tests", ex);
        }
    }

    public SoilTest mapSoilTest(ResultSet rs) throws Exception {
        SoilTest soilTest = new SoilTest();
        soilTest.setId(rs.getLong("id"));
        soilTest.setUserId(rs.getLong("user_id"));
        soilTest.setCropType(rs.getString("crop_type"));
        soilTest.setNitrogen(rs.getDouble("nitrogen"));
        soilTest.setPhosphorus(rs.getDouble("phosphorus"));
        soilTest.setPotassium(rs.getDouble("potassium"));
        soilTest.setPhValue(rs.getDouble("ph_value"));
        soilTest.setSoilHealthStatus(rs.getString("soil_health_status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            soilTest.setCreatedAt(createdAt.toLocalDateTime());
        }
        return soilTest;
    }
}
