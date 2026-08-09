CREATE DATABASE IF NOT EXISTS local_tourist_planner;
USE local_tourist_planner;

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS places (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(40) NOT NULL,
    description TEXT NOT NULL,
    distance_km DOUBLE NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    travel_tips TEXT NOT NULL,
    address VARCHAR(200) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS day_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    planner_code VARCHAR(40) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS day_plan_places (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_plan_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    visit_order INT NOT NULL,
    CONSTRAINT uk_day_plan_place UNIQUE (day_plan_id, place_id),
    CONSTRAINT fk_day_plan_places_plan FOREIGN KEY (day_plan_id) REFERENCES day_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_day_plan_places_place FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE
);
