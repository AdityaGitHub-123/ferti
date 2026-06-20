package com.fertilizer.ruleengine.repository;

import com.fertilizer.ruleengine.entity.Recommendation;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecommendationRepository {
    private final DataSource dataSource;

    public RecommendationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Recommendation save(Recommendation recommendation) {
        String sql = """
                INSERT INTO recommendations
                (soil_test_id, user_id, crop_type, fertilizer_name, nutrient_deficiency, recommendation_details, fertilizer_image, crop_image, level_color)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, recommendation.getSoilTestId());
            ps.setLong(2, recommendation.getUserId());
            ps.setString(3, recommendation.getCropType());
            ps.setString(4, recommendation.getFertilizerName());
            ps.setString(5, recommendation.getNutrientDeficiency());
            ps.setString(6, recommendation.getRecommendationDetails());
            ps.setString(7, recommendation.getFertilizerImage());
            ps.setString(8, recommendation.getCropImage());
            ps.setString(9, recommendation.getLevelColor());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    recommendation.setId(keys.getLong(1));
                }
            }
            return recommendation;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to save recommendation", ex);
        }
    }

    public List<Recommendation> findByUser(Long userId) {
        String sql = "SELECT * FROM recommendations WHERE user_id = ? ORDER BY created_at DESC";
        List<Recommendation> recommendations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recommendations.add(mapRecommendation(rs));
                }
            }
            return recommendations;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load recommendations", ex);
        }
    }

    public List<Recommendation> findRecentByUser(Long userId, int limit) {
        String sql = "SELECT * FROM recommendations WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Recommendation> recommendations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recommendations.add(mapRecommendation(rs));
                }
            }
            return recommendations;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load recent recommendations", ex);
        }
    }

    public int countByUser(Long userId) {
        String sql = "SELECT COUNT(*) FROM recommendations WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to count recommendations", ex);
        }
    }

    private Recommendation mapRecommendation(ResultSet rs) throws Exception {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(rs.getLong("id"));
        recommendation.setSoilTestId(rs.getLong("soil_test_id"));
        recommendation.setUserId(rs.getLong("user_id"));
        recommendation.setCropType(rs.getString("crop_type"));
        recommendation.setFertilizerName(rs.getString("fertilizer_name"));
        recommendation.setNutrientDeficiency(rs.getString("nutrient_deficiency"));
        recommendation.setRecommendationDetails(rs.getString("recommendation_details"));
        recommendation.setFertilizerImage(rs.getString("fertilizer_image"));
        recommendation.setCropImage(rs.getString("crop_image"));
        recommendation.setLevelColor(rs.getString("level_color"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            recommendation.setCreatedAt(createdAt.toLocalDateTime());
        }
        return recommendation;
    }
}
