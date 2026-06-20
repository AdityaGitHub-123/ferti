package com.fertilizer.ruleengine.controller;

import com.fertilizer.ruleengine.dto.DashboardStats;
import com.fertilizer.ruleengine.dto.RecommendationResponse;
import com.fertilizer.ruleengine.dto.SoilRequest;
import com.fertilizer.ruleengine.entity.Crop;
import com.fertilizer.ruleengine.entity.Recommendation;
import com.fertilizer.ruleengine.entity.User;
import com.fertilizer.ruleengine.service.AuthService;
import com.fertilizer.ruleengine.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendationController {
    private final AuthService authService;
    private final RecommendationService recommendationService;

    public RecommendationController(AuthService authService, RecommendationService recommendationService) {
        this.authService = authService;
        this.recommendationService = recommendationService;
    }

    @PostMapping("/soil")
    public ResponseEntity<RecommendationResponse> submitSoilData(@RequestHeader("X-Auth-Token") String token,
                                                                  @Valid @RequestBody SoilRequest request) {
        User user = authService.getUserFromToken(token);
        return ResponseEntity.ok(recommendationService.generate(user, request));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Recommendation>> getRecommendations(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.getUserFromToken(token);
        return ResponseEntity.ok(recommendationService.getHistory(user));
    }

    @GetMapping("/recommendations/recent")
    public ResponseEntity<List<Recommendation>> getRecentRecommendations(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.getUserFromToken(token);
        return ResponseEntity.ok(recommendationService.getRecent(user));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStats> getDashboardStats(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.getUserFromToken(token);
        return ResponseEntity.ok(recommendationService.getStats(user));
    }

    @GetMapping("/crops")
    public ResponseEntity<List<Crop>> getCrops() {
        return ResponseEntity.ok(recommendationService.getCrops());
    }
}
