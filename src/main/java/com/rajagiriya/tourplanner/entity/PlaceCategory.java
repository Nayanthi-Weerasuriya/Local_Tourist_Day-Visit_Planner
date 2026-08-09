package com.rajagiriya.tourplanner.entity;

import java.util.Arrays;

public enum PlaceCategory {
    RELIGIOUS("Religious"),
    NATURE("Nature"),
    HERITAGE("Heritage"),
    CULTURAL("Cultural"),
    LEISURE("Leisure");

    private final String displayName;

    PlaceCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PlaceCategory fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }

        return Arrays.stream(values())
                .filter(category -> category.name().equalsIgnoreCase(value)
                        || category.displayName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported category: " + value));
    }
}
