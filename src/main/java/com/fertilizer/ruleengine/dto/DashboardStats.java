package com.fertilizer.ruleengine.dto;

public class DashboardStats {
    private int tests;
    private int recommendations;
    private int healthy;
    private int medium;
    private int poor;

    public DashboardStats(int tests, int recommendations, int healthy, int medium, int poor) {
        this.tests = tests;
        this.recommendations = recommendations;
        this.healthy = healthy;
        this.medium = medium;
        this.poor = poor;
    }

    public int getTests() {
        return tests;
    }

    public int getRecommendations() {
        return recommendations;
    }

    public int getHealthy() {
        return healthy;
    }

    public int getMedium() {
        return medium;
    }

    public int getPoor() {
        return poor;
    }
}
