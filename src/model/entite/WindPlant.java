package model.entite;

/**
 * Wind power plant.
 * Ecological (zero pollution), moderate costs, but power output varies.
 * Higher storage ratio to compensate for variability.
 */
public class WindPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final double BASE_CONSTRUCTION_COST = 1200.0;
    public static final int BASE_CONSTRUCTION_TIME = 1;
    public static final int DEFAULT_MAX_LEVEL = 4;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 35.0;
    public static final double BASE_STORAGE_CAPACITY = 105.0; // 3x ratio for wind variability
    public static final double BASE_DAILY_COST = 28.0;
    public static final double BASE_POLLUTION_RATE = 0.0; // Eco-friendly

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.48; // +48% per level
    public static final double STORAGE_GROWTH_RATE = 1.42; // +42% per level
    public static final double DAILY_COST_GROWTH_RATE = 1.18; // +18% per level
    public static final double POLLUTION_REDUCTION_RATE = 1.0; // No pollution, no change

    // Upgrade
    public static final double UPGRADE_COST_BASE = 1500.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.4;
    public static final int UPGRADE_TIME_BASE = 2;

    // ========== Constructor ==========

    /**
     * Creates a new Wind Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public WindPlant(String id) {
        super(id, BASE_POWER_OUTPUT, BASE_STORAGE_CAPACITY, BASE_DAILY_COST,
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }
}
