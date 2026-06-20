CREATE DATABASE IF NOT EXISTS fertilizer_db;
USE fertilizer_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    auth_token VARCHAR(80),
    profile_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS crops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    category VARCHAR(80) NOT NULL DEFAULT 'General',
    image_url VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    target_nitrogen DOUBLE NOT NULL DEFAULT 50,
    target_phosphorus DOUBLE NOT NULL DEFAULT 30,
    target_potassium DOUBLE NOT NULL DEFAULT 30,
    min_ph DOUBLE NOT NULL DEFAULT 6,
    max_ph DOUBLE NOT NULL DEFAULT 7.5
);

ALTER TABLE crops ADD COLUMN category VARCHAR(80) NOT NULL DEFAULT 'General';
ALTER TABLE crops ADD COLUMN target_nitrogen DOUBLE NOT NULL DEFAULT 50;
ALTER TABLE crops ADD COLUMN target_phosphorus DOUBLE NOT NULL DEFAULT 30;
ALTER TABLE crops ADD COLUMN target_potassium DOUBLE NOT NULL DEFAULT 30;
ALTER TABLE crops ADD COLUMN min_ph DOUBLE NOT NULL DEFAULT 6;
ALTER TABLE crops ADD COLUMN max_ph DOUBLE NOT NULL DEFAULT 7.5;

CREATE TABLE IF NOT EXISTS soil_test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    crop_type VARCHAR(80) NOT NULL,
    nitrogen DOUBLE NOT NULL,
    phosphorus DOUBLE NOT NULL,
    potassium DOUBLE NOT NULL,
    ph_value DOUBLE NOT NULL,
    soil_health_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_soil_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    soil_test_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    crop_type VARCHAR(80) NOT NULL,
    fertilizer_name VARCHAR(80) NOT NULL,
    nutrient_deficiency VARCHAR(120) NOT NULL,
    recommendation_details TEXT NOT NULL,
    fertilizer_image VARCHAR(255) NOT NULL,
    crop_image VARCHAR(255) NOT NULL,
    level_color VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rec_soil FOREIGN KEY (soil_test_id) REFERENCES soil_test(id) ON DELETE CASCADE,
    CONSTRAINT fk_rec_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO crops (name, category, image_url, description, target_nitrogen, target_phosphorus, target_potassium, min_ph, max_ph) VALUES
('Rice', 'Cereal', '/images/crops/rice.svg', 'High water requirement cereal crop', 70, 35, 40, 5.5, 7.0),
('Wheat', 'Cereal', '/images/crops/wheat.svg', 'Cool-season cereal crop', 65, 32, 35, 6.0, 7.5),
('Maize', 'Cereal', '/images/crops/maize.svg', 'Nitrogen-responsive cereal crop', 75, 35, 45, 5.8, 7.5),
('Ragi', 'Millet', '/images/crops/ragi.svg', 'Hardy millet suited to dryland farming', 45, 25, 25, 5.5, 7.5),
('Barley', 'Cereal', '/images/crops/barley.svg', 'Cool-season grain and fodder crop', 55, 28, 32, 6.0, 7.5),
('Tomato', 'Vegetable', '/images/crops/tomato.svg', 'Nutrient-sensitive vegetable crop', 60, 40, 55, 6.0, 7.0),
('Potato', 'Vegetable', '/images/crops/potato.svg', 'Tuber crop with high potassium demand', 65, 45, 70, 5.2, 6.5),
('Onion', 'Vegetable', '/images/crops/onion.svg', 'Bulb crop requiring balanced nutrition', 55, 35, 45, 6.0, 7.0),
('Chilli', 'Vegetable', '/images/crops/chilli.svg', 'Spice crop sensitive to nutrient stress', 60, 35, 50, 6.0, 7.0),
('Brinjal', 'Vegetable', '/images/crops/brinjal.svg', 'Fruit vegetable with steady nutrient needs', 60, 35, 50, 5.5, 7.0),
('Cabbage', 'Vegetable', '/images/crops/cabbage.svg', 'Leafy vegetable with strong nitrogen need', 70, 40, 55, 6.0, 7.5),
('Sugarcane', 'Commercial', '/images/crops/sugarcane.svg', 'Long-duration commercial cane crop', 85, 45, 70, 6.0, 8.0),
('Cotton', 'Fiber', '/images/crops/cotton.svg', 'Fiber crop needing balanced NPK', 65, 35, 55, 6.0, 8.0),
('Groundnut', 'Oilseed', '/images/crops/groundnut.svg', 'Legume oilseed crop with phosphorus need', 35, 40, 35, 6.0, 7.5),
('Sunflower', 'Oilseed', '/images/crops/sunflower.svg', 'Oilseed crop with potassium demand', 55, 35, 60, 6.0, 7.5),
('Banana', 'Fruit', '/images/crops/banana.svg', 'Fruit crop with very high potassium need', 80, 45, 90, 6.0, 7.5),
('Mango', 'Fruit', '/images/crops/mango.svg', 'Perennial fruit crop for orchard systems', 50, 30, 55, 5.5, 7.5)
ON DUPLICATE KEY UPDATE
    category = VALUES(category),
    image_url = VALUES(image_url),
    description = VALUES(description),
    target_nitrogen = VALUES(target_nitrogen),
    target_phosphorus = VALUES(target_phosphorus),
    target_potassium = VALUES(target_potassium),
    min_ph = VALUES(min_ph),
    max_ph = VALUES(max_ph);
