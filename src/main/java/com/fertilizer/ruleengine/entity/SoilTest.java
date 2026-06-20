package com.fertilizer.ruleengine.entity;

import java.time.LocalDateTime;

public class SoilTest {
    private Long id;
    private Long userId;
    private String cropType;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private double phValue;
    private String soilHealthStatus;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(double nitrogen) {
        this.nitrogen = nitrogen;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getPhValue() {
        return phValue;
    }

    public void setPhValue(double phValue) {
        this.phValue = phValue;
    }

    public String getSoilHealthStatus() {
        return soilHealthStatus;
    }

    public void setSoilHealthStatus(String soilHealthStatus) {
        this.soilHealthStatus = soilHealthStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
