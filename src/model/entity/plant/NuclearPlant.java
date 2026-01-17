package model.entity.plant;

import model.entity.PowerPlant;

/**
 * Nuclear power plant.
 * Massive power production, low operating costs, minimal pollution.
 * Very expensive construction and long build time.
 */
public class NuclearPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final String DISPLAY_NAME = "ADVANCED NUCLEAR PLANT";
    public static final String DESCRIPTION = "Massive clean power.";
    public static final double BASE_CONSTRUCTION_COST = 8000.0;
    public static final int BASE_CONSTRUCTION_TIME = 5;
    public static final int DEFAULT_MAX_LEVEL = 3; // Fewer levels due to complexity

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 60.0; // Reduced for game balance
    public static final double BASE_STORAGE_CAPACITY = 120.0; // 2x ratio for safety reserves
    public static final double BASE_DAILY_COST = 1500.0; // Very expensive (safety + waste management)
    public static final double BASE_POLLUTION_RATE = 5.0; // Low (radioactive waste)

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.35; // +35% per level
    public static final double STORAGE_GROWTH_RATE = 1.40; // +40% per level
    public static final double DAILY_COST_GROWTH_RATE = 1.25; // +25% per level
    public static final double POLLUTION_REDUCTION_RATE = 0.90; // -10% per level (better waste management)

    // Upgrade
    public static final double UPGRADE_COST_BASE = 10000.0;
    public static final double UPGRADE_COST_MULTIPLIER = 2.0;
    public static final int UPGRADE_TIME_BASE = 6;

    // ========== Constructor ==========

    /**
     * Creates a new Nuclear Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public NuclearPlant(String id) {
        super(id, BASE_POWER_OUTPUT, BASE_STORAGE_CAPACITY, BASE_DAILY_COST,
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_COST, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }

    @Override
    public String getShopName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getShopDescription() {
        return DESCRIPTION;
    }
}
