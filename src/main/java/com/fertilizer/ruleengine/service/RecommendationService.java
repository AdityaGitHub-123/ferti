package com.fertilizer.ruleengine.service;

import com.fertilizer.ruleengine.dto.DashboardStats;
import com.fertilizer.ruleengine.dto.RecommendationResponse;
import com.fertilizer.ruleengine.dto.SoilRequest;
import com.fertilizer.ruleengine.entity.Crop;
import com.fertilizer.ruleengine.entity.Recommendation;
import com.fertilizer.ruleengine.entity.SoilTest;
import com.fertilizer.ruleengine.entity.User;
import com.fertilizer.ruleengine.repository.CropRepository;
import com.fertilizer.ruleengine.repository.RecommendationRepository;
import com.fertilizer.ruleengine.repository.SoilTestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class RecommendationService {
    private static final double LOW_NITROGEN = 50.0;
    private static final double LOW_PHOSPHORUS = 30.0;
    private static final double LOW_POTASSIUM = 30.0;

    private final SoilTestRepository soilTestRepository;
    private final RecommendationRepository recommendationRepository;
    private final CropRepository cropRepository;

    public RecommendationService(SoilTestRepository soilTestRepository,
                                 RecommendationRepository recommendationRepository,
                                 CropRepository cropRepository) {
        this.soilTestRepository = soilTestRepository;
        this.recommendationRepository = recommendationRepository;
        this.cropRepository = cropRepository;
    }

    public RecommendationResponse generate(User user, SoilRequest request) {
        String cropType = normalizeCrop(request.getCropType());
        Optional<Crop> cropProfile = cropRepository.findByName(cropType);
        Crop crop = cropProfile.orElse(null);
        if (crop != null) {
            cropType = crop.getName();
        }
        String soilHealth = calculateHealth(request.getNitrogen(), request.getPhosphorus(), request.getPotassium(), request.getPhValue(), crop);

        SoilTest soilTest = new SoilTest();
        soilTest.setUserId(user.getId());
        soilTest.setCropType(cropType);
        soilTest.setNitrogen(request.getNitrogen());
        soilTest.setPhosphorus(request.getPhosphorus());
        soilTest.setPotassium(request.getPotassium());
        soilTest.setPhValue(request.getPhValue());
        soilTest.setSoilHealthStatus(soilHealth);
        SoilTest savedTest = soilTestRepository.save(soilTest);

        List<Recommendation> recommendations = buildRecommendations(savedTest, user.getId(), cropType, request, crop);
        List<Recommendation> savedRecommendations = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            savedRecommendations.add(recommendationRepository.save(recommendation));
        }

        return new RecommendationResponse(
                savedTest.getId(),
                cropType,
                request.getNitrogen(),
                request.getPhosphorus(),
                request.getPotassium(),
                request.getPhValue(),
                soilHealth,
                "Recommendation generated successfully",
                savedRecommendations
        );
    }

    public List<Recommendation> getHistory(User user) {
        return recommendationRepository.findByUser(user.getId());
    }

    public List<Recommendation> getRecent(User user) {
        return recommendationRepository.findRecentByUser(user.getId(), 5);
    }

    public DashboardStats getStats(User user) {
        int tests = soilTestRepository.countByUser(user.getId());
        int recommendations = recommendationRepository.countByUser(user.getId());
        Map<String, Integer> health = soilTestRepository.countByHealthStatus(user.getId());
        return new DashboardStats(
                tests,
                recommendations,
                health.getOrDefault("Healthy", 0),
                health.getOrDefault("Medium", 0),
                health.getOrDefault("Poor", 0)
        );
    }

    public List<Crop> getCrops() {
        return cropRepository.findAll();
    }

    private List<Recommendation> buildRecommendations(SoilTest soilTest, Long userId, String cropType, SoilRequest request, Crop crop) {
        List<Recommendation> recommendations = new ArrayList<>();
        String cropImage = cropImage(crop);
        double nitrogenTarget = target(crop == null ? null : crop.getTargetNitrogen(), LOW_NITROGEN);
        double phosphorusTarget = target(crop == null ? null : crop.getTargetPhosphorus(), LOW_PHOSPHORUS);
        double potassiumTarget = target(crop == null ? null : crop.getTargetPotassium(), LOW_POTASSIUM);
        double minPh = target(crop == null ? null : crop.getMinPh(), 6.0);
        double maxPh = target(crop == null ? null : crop.getMaxPh(), 7.5);

        if (request.getNitrogen() < nitrogenTarget) {
            recommendations.add(createRecommendation(soilTest, userId, cropType, "Urea", "Nitrogen deficiency",
                    cropType + " needs stronger nitrogen support. Apply Urea in split doses around active growth and irrigate lightly after application.",
                    "/images/fertilizers/urea.svg", cropImage, colorForLevel(request.getNitrogen(), nitrogenTarget)));
        }
        if (request.getPhosphorus() < phosphorusTarget) {
            recommendations.add(createRecommendation(soilTest, userId, cropType, "DAP", "Phosphorus deficiency",
                    "Use DAP as a basal application for " + cropType + " to improve early root growth, flowering, and crop establishment.",
                    "/images/fertilizers/dap.svg", cropImage, colorForLevel(request.getPhosphorus(), phosphorusTarget)));
        }
        if (request.getPotassium() < potassiumTarget) {
            recommendations.add(createRecommendation(soilTest, userId, cropType, "MOP", "Potassium deficiency",
                    "Apply MOP for " + cropType + " to improve stem strength, stress resistance, produce quality, and filling development.",
                    "/images/fertilizers/mop.svg", cropImage, colorForLevel(request.getPotassium(), potassiumTarget)));
        }
        if (request.getPhValue() < minPh) {
            recommendations.add(createRecommendation(soilTest, userId, cropType, "Organic Compost", "Acidic soil reaction",
                    cropType + " performs best around pH " + minPh + "-" + maxPh + ". Add compost and use lime only after local agronomist confirmation.",
                    "/images/fertilizers/compost.svg", cropImage, phColor(minPh - request.getPhValue())));
        }
        if (request.getPhValue() > maxPh) {
            recommendations.add(createRecommendation(soilTest, userId, cropType, "Organic Compost", "Alkaline soil reaction",
                    cropType + " performs best around pH " + minPh + "-" + maxPh + ". Add compost and avoid overuse of alkaline amendments.",
                    "/images/fertilizers/compost.svg", cropImage, phColor(request.getPhValue() - maxPh)));
        }
        if (recommendations.isEmpty()) {
            recommendations.add(createRecommendation(soilTest, userId, cropType, "Organic Compost", "No major deficiency",
                    cropType + " readings are within the expected range. Maintain soil health with compost, crop rotation, mulching, and periodic soil testing.",
                    "/images/fertilizers/compost.svg", cropImage, "green"));
        }
        return recommendations;
    }

    private Recommendation createRecommendation(SoilTest soilTest, Long userId, String cropType, String fertilizer,
                                                String deficiency, String details, String fertilizerImage,
                                                String cropImage, String levelColor) {
        Recommendation recommendation = new Recommendation();
        recommendation.setSoilTestId(soilTest.getId());
        recommendation.setUserId(userId);
        recommendation.setCropType(cropType);
        recommendation.setFertilizerName(fertilizer);
        recommendation.setNutrientDeficiency(deficiency);
        recommendation.setRecommendationDetails(details);
        recommendation.setFertilizerImage(fertilizerImage);
        recommendation.setCropImage(cropImage);
        recommendation.setLevelColor(levelColor);
        return recommendation;
    }

    private String calculateHealth(double n, double p, double k, double ph, Crop crop) {
        double nitrogenTarget = target(crop == null ? null : crop.getTargetNitrogen(), LOW_NITROGEN);
        double phosphorusTarget = target(crop == null ? null : crop.getTargetPhosphorus(), LOW_PHOSPHORUS);
        double potassiumTarget = target(crop == null ? null : crop.getTargetPotassium(), LOW_POTASSIUM);
        double minPh = target(crop == null ? null : crop.getMinPh(), 6.0);
        double maxPh = target(crop == null ? null : crop.getMaxPh(), 7.5);

        int poorSignals = 0;
        if (n < nitrogenTarget * 0.7) poorSignals++;
        if (p < phosphorusTarget * 0.7) poorSignals++;
        if (k < potassiumTarget * 0.7) poorSignals++;
        if (ph < minPh - 0.6 || ph > maxPh + 0.6) poorSignals++;

        int mediumSignals = 0;
        if (n < nitrogenTarget) mediumSignals++;
        if (p < phosphorusTarget) mediumSignals++;
        if (k < potassiumTarget) mediumSignals++;
        if (ph < minPh || ph > maxPh) mediumSignals++;

        if (poorSignals >= 2 || mediumSignals >= 3) {
            return "Poor";
        }
        if (mediumSignals > 0) {
            return "Medium";
        }
        return "Healthy";
    }

    private double target(Double cropValue, double fallback) {
        return cropValue == null ? fallback : cropValue;
    }

    private String colorForLevel(double value, double threshold) {
        if (value < threshold * 0.7) {
            return "red";
        }
        return "yellow";
    }

    private String phColor(double distance) {
        return distance > 0.6 ? "red" : "yellow";
    }

    private String cropImage(Crop crop) {
        return crop == null ? "/images/crops/generic.svg" : crop.getImageUrl();
    }

    private String normalizeCrop(String cropType) {
        String cleaned = cropType.trim().toLowerCase(Locale.ROOT);
        return cleaned.substring(0, 1).toUpperCase(Locale.ROOT) + cleaned.substring(1);
    }
}
