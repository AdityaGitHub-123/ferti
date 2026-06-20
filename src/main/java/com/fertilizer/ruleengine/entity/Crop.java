package com.fertilizer.ruleengine.entity;

public class Crop {
    private Long id;
    private String name;
    private String category;
    private String imageUrl;
    private String description;
    private Double targetNitrogen;
    private Double targetPhosphorus;
    private Double targetPotassium;
    private Double minPh;
    private Double maxPh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTargetNitrogen() {
        return targetNitrogen;
    }

    public void setTargetNitrogen(Double targetNitrogen) {
        this.targetNitrogen = targetNitrogen;
    }

    public Double getTargetPhosphorus() {
        return targetPhosphorus;
    }

    public void setTargetPhosphorus(Double targetPhosphorus) {
        this.targetPhosphorus = targetPhosphorus;
    }

    public Double getTargetPotassium() {
        return targetPotassium;
    }

    public void setTargetPotassium(Double targetPotassium) {
        this.targetPotassium = targetPotassium;
    }

    public Double getMinPh() {
        return minPh;
    }

    public void setMinPh(Double minPh) {
        this.minPh = minPh;
    }

    public Double getMaxPh() {
        return maxPh;
    }

    public void setMaxPh(Double maxPh) {
        this.maxPh = maxPh;
    }
}
