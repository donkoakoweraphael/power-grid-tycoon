package model.entite;

/**
 * Natural gas power plant.
 * Fast construction, stable and modular production, less polluting than coal.
 */
public class NaturalGasPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final double BASE_CONSTRUCTION_COST = 1500.0;
    public static final int BASE_CONSTRUCTION_TIME = 2;
    public static final int DEFAULT_MAX_LEVEL = 5;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 100.0;
    public static final double BASE_STORAGE_CAPACITY = 150.0;
    public static final double BASE_DAILY_COST = 85.0;
    public static final double BASE_POLLUTION_RATE = 50.0;

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.42; // +42% per level
    public static final double STORAGE_GROWTH_RATE = 1.38; // +38% per level
    public static final double DAILY_COST_GROWTH_RATE = 1.20; // +20% per level
    public static final double POLLUTION_REDUCTION_RATE = 0.96; // -4% per level (better than coal)

    // Upgrade
    public static final double UPGRADE_COST_BASE = 2000.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.5;
    public static final int UPGRADE_TIME_BASE = 3;

    // ========== Constructor ==========

    /**
     * Creates a new Natural Gas Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public NaturalGasPlant(String id) {
        super(id, BASE_POWER_OUTPUT, BASE_STORAGE_CAPACITY, BASE_DAILY_COST,
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }
}
