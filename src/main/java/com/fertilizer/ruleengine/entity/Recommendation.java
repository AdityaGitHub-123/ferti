package com.fertilizer.ruleengine.entity;

import java.time.LocalDateTime;

public class Recommendation {
    private Long id;
    private Long soilTestId;
    private Long userId;
    private String cropType;
    private String fertilizerName;
    private String nutrientDeficiency;
    private String recommendationDetails;
    private String fertilizerImage;
    private String cropImage;
    private String levelColor;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSoilTestId() {
        return soilTestId;
    }

    public void setSoilTestId(Long soilTestId) {
        this.soilTestId = soilTestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getFertilizerName() {
        return fertilizerName;
    }

    public void setFertilizerName(String fertilizerName) {
        this.fertilizerName = fertilizerName;
    }

    public String getNutrientDeficiency() {
        return nutrientDeficiency;
    }

    public void setNutrientDeficiency(String nutrientDeficiency) {
        this.nutrientDeficiency = nutrientDeficiency;
    }

    public String getRecommendationDetails() {
        return recommendationDetails;
    }

    public void setRecommendationDetails(String recommendationDetails) {
        this.recommendationDetails = recommendationDetails;
    }

    public String getFertilizerImage() {
        return fertilizerImage;
    }

    public void setFertilizerImage(String fertilizerImage) {
        this.fertilizerImage = fertilizerImage;
    }

    public String getCropImage() {
        return cropImage;
    }

    public void setCropImage(String cropImage) {
        this.cropImage = cropImage;
    }

    public String getLevelColor() {
        return levelColor;
    }

    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
