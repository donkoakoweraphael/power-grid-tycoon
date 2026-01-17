package model.entity.plant;

import model.entity.PowerPlant;

/**
 * Solar power plant.
 * Low operating costs, ecological (zero pollution), but lower power output.
 * High storage ratio to buffer for night/day cycles.
 */
public class SolarPlant extends PowerPlant {
    private static final long serialVersionUID = 1L;

    // ========== Class Variables (Static) ==========

    // Construction
    public static final String DISPLAY_NAME = "SOLAR POWER FARM";
    public static final String DESCRIPTION = "Eco-friendly, variable output.";
    public static final double BASE_CONSTRUCTION_COST = 800.0;
    public static final int BASE_CONSTRUCTION_TIME = 1;
    public static final int DEFAULT_MAX_LEVEL = 4;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 120.0; // Boosted x15 (was 8.0)
    public static final double BASE_STORAGE_CAPACITY = 480.0; // Boosted x15 (was 32.0)
    public static final double BASE_DAILY_COST = 200.0; // Keep cost same (cheaper relative to output)
    public static final double BASE_POLLUTION_RATE = 0.0; // Eco-friendly

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.50; // +50% per level
    public static final double STORAGE_GROWTH_RATE = 1.45; // +45% per level
    public static final double DAILY_COST_GROWTH_RATE = 1.15; // +15% per level
    public static final double POLLUTION_REDUCTION_RATE = 1.0; // No pollution, no change

    // Upgrade
    public static final double UPGRADE_COST_BASE = 1000.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.4;
    public static final int UPGRADE_TIME_BASE = 2;

    // ========== Constructor ==========

    /**
     * Creates a new Solar Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public SolarPlant(String id) {
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

    // ========== Upgrade Constants Implementation ==========
    @Override
    public double getPowerOutputGrowthRate() {
        return POWER_OUTPUT_GROWTH_RATE;
    }

    @Override
    public double getStorageGrowthRate() {
        return STORAGE_GROWTH_RATE;
    }

    @Override
    public double getDailyCostGrowthRate() {
        return DAILY_COST_GROWTH_RATE;
    }

    @Override
    public double getPollutionReductionRate() {
        return POLLUTION_REDUCTION_RATE;
    }

    @Override
    public double getUpgradeCostMultiplier() {
        return UPGRADE_COST_MULTIPLIER;
    }

    @Override
    public int getUpgradeTimeBase() {
        return UPGRADE_TIME_BASE;
    }

    @Override
    public double getUpgradeCostBase() {
        return UPGRADE_COST_BASE;
    }
}
