package model.entite;

/**
 * Hydroelectric power plant.
 * Excellent storage capacity (natural reservoir), stable and ecological.
 * High construction cost but very low operating costs.
 */
public class HydroPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final double BASE_CONSTRUCTION_COST = 5000.0;
    public static final int BASE_CONSTRUCTION_TIME = 4;
    public static final int DEFAULT_MAX_LEVEL = 4;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 80.0;
    public static final double BASE_STORAGE_CAPACITY = 1200.0; // 15x ratio - natural reservoir!
    public static final double BASE_DAILY_COST = 45.0;
    public static final double BASE_POLLUTION_RATE = 0.0; // Eco-friendly

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.38; // +38% per level
    public static final double STORAGE_GROWTH_RATE = 1.50; // +50% per level (expand reservoir)
    public static final double DAILY_COST_GROWTH_RATE = 1.15; // +15% per level
    public static final double POLLUTION_REDUCTION_RATE = 1.0; // No pollution, no change

    // Upgrade
    public static final double UPGRADE_COST_BASE = 6000.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.6;
    public static final int UPGRADE_TIME_BASE = 5;

    // ========== Constructor ==========

    /**
     * Creates a new Hydro Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public HydroPlant(String id) {
        super(id, BASE_POWER_OUTPUT, BASE_STORAGE_CAPACITY, BASE_DAILY_COST,
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }
}
