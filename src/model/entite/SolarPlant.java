package model.entite;

/**
 * Solar power plant.
 * Low operating costs, ecological (zero pollution), but lower power output.
 * High storage ratio to buffer for night/day cycles.
 */
public class SolarPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final double BASE_CONSTRUCTION_COST = 800.0;
    public static final int BASE_CONSTRUCTION_TIME = 1;
    public static final int DEFAULT_MAX_LEVEL = 4;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 40.0;
    public static final double BASE_STORAGE_CAPACITY = 160.0; // 4x ratio for day/night buffering
    public static final double BASE_DAILY_COST = 20.0;
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
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }
}
