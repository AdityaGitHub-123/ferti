package com.fertilizer.ruleengine.dto;

import com.fertilizer.ruleengine.entity.Recommendation;

import java.util.List;

public class RecommendationResponse {
    private Long soilTestId;
    private String cropType;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private double phValue;
    private String soilHealthStatus;
    private String message;
    private List<Recommendation> recommendations;

    public RecommendationResponse(Long soilTestId, String cropType, double nitrogen, double phosphorus,
                                  double potassium, double phValue, String soilHealthStatus,
                                  String message, List<Recommendation> recommendations) {
        this.soilTestId = soilTestId;
        this.cropType = cropType;
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.phValue = phValue;
        this.soilHealthStatus = soilHealthStatus;
        this.message = message;
        this.recommendations = recommendations;
    }

    public Long getSoilTestId() {
        return soilTestId;
    }

    public String getCropType() {
        return cropType;
    }

    public double getNitrogen() {
        return nitrogen;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public double getPotassium() {
        return potassium;
    }

    public double getPhValue() {
        return phValue;
    }

    public String getSoilHealthStatus() {
        return soilHealthStatus;
    }

    public String getMessage() {
        return message;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
}
