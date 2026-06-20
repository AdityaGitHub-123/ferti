package com.fertilizer.ruleengine.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SoilRequest {
    @NotNull(message = "Nitrogen value is required")
    @DecimalMin(value = "0.0", message = "Nitrogen cannot be negative")
    private Double nitrogen;

    @NotNull(message = "Phosphorus value is required")
    @DecimalMin(value = "0.0", message = "Phosphorus cannot be negative")
    private Double phosphorus;

    @NotNull(message = "Potassium value is required")
    @DecimalMin(value = "0.0", message = "Potassium cannot be negative")
    private Double potassium;

    @NotNull(message = "pH value is required")
    @DecimalMin(value = "0.0", message = "pH must be between 0 and 14")
    @DecimalMax(value = "14.0", message = "pH must be between 0 and 14")
    private Double phValue;

    @NotBlank(message = "Crop type is required")
    private String cropType;

    public Double getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(Double nitrogen) {
        this.nitrogen = nitrogen;
    }

    public Double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(Double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = potassium;
    }

    public Double getPhValue() {
        return phValue;
    }

    public void setPhValue(Double phValue) {
        this.phValue = phValue;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }
}
