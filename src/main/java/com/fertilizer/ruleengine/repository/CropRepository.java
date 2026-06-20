package com.fertilizer.ruleengine.repository;

import com.fertilizer.ruleengine.entity.Crop;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CropRepository {
    private final DataSource dataSource;

    public CropRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Crop> findAll() {
        String sql = "SELECT * FROM crops ORDER BY name";
        List<Crop> crops = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                crops.add(mapCrop(rs));
            }
            return crops;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load crops", ex);
        }
    }

    public Optional<Crop> findByName(String name) {
        String sql = "SELECT * FROM crops WHERE LOWER(name) = LOWER(?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCrop(rs));
                }
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load crop", ex);
        }
    }

    private Crop mapCrop(ResultSet rs) throws Exception {
        Crop crop = new Crop();
        crop.setId(rs.getLong("id"));
        crop.setName(rs.getString("name"));
        crop.setCategory(getString(rs, "category", "General"));
        crop.setImageUrl(rs.getString("image_url"));
        crop.setDescription(rs.getString("description"));
        crop.setTargetNitrogen(getDouble(rs, "target_nitrogen", 50.0));
        crop.setTargetPhosphorus(getDouble(rs, "target_phosphorus", 30.0));
        crop.setTargetPotassium(getDouble(rs, "target_potassium", 30.0));
        crop.setMinPh(getDouble(rs, "min_ph", 6.0));
        crop.setMaxPh(getDouble(rs, "max_ph", 7.5));
        return crop;
    }

    private String getString(ResultSet rs, String column, String fallback) throws Exception {
        if (!hasColumn(rs, column)) {
            return fallback;
        }
        String value = rs.getString(column);
        return value == null ? fallback : value;
    }

    private Double getDouble(ResultSet rs, String column, Double fallback) throws Exception {
        if (!hasColumn(rs, column)) {
            return fallback;
        }
        double value = rs.getDouble(column);
        return rs.wasNull() ? fallback : value;
    }

    private boolean hasColumn(ResultSet rs, String column) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (column.equalsIgnoreCase(metaData.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }
}
