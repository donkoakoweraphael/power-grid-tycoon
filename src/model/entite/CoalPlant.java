package model.entite;

/**
 * Coal-fired power plant.
 * High power output and stable production, but high pollution and operating
 * costs.
 */
public class CoalPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final double BASE_CONSTRUCTION_COST = 1000.0;
    public static final int BASE_CONSTRUCTION_TIME = 2;
    public static final int DEFAULT_MAX_LEVEL = 5;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 120.0;
    public static final double BASE_STORAGE_CAPACITY = 180.0;
    public static final double BASE_DAILY_COST = 110.0;
    public static final double BASE_POLLUTION_RATE = 100.0;

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.45; // +45% per level
    public static final double STORAGE_GROWTH_RATE = 1.40; // +40% per level
    public static final double DAILY_COST_GROWTH_RATE = 1.22; // +22% per level
    public static final double POLLUTION_REDUCTION_RATE = 0.98; // -2% per level (technology improvement)

    // Upgrade
    public static final double UPGRADE_COST_BASE = 1500.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.5;
    public static final int UPGRADE_TIME_BASE = 3;

    // ========== Constructor ==========

    /**
     * Creates a new Coal Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public CoalPlant(String id) {
        super(id, BASE_POWER_OUTPUT, BASE_STORAGE_CAPACITY, BASE_DAILY_COST,
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }
}
