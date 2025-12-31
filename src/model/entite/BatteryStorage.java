package model.entite;

/**
 * Battery storage facility.
 * Does not produce power (powerOutput = 0), but provides massive energy
 * storage.
 * Strategic for balancing renewable energy production and peak demand.
 */
public class BatteryStorage extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    public static final double BASE_CONSTRUCTION_COST = 3000.0;
    public static final int BASE_CONSTRUCTION_TIME = 2;
    public static final int DEFAULT_MAX_LEVEL = 5;

    // Stats at level 1
    public static final double BASE_POWER_OUTPUT = 0.0; // No production, only storage
    public static final double BASE_STORAGE_CAPACITY = 2500.0; // Massive storage capacity
    public static final double BASE_DAILY_COST = 45.0;
    public static final double BASE_POLLUTION_RATE = 0.0; // Eco-friendly

    // Growth rates per level
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.0; // No growth (always 0)
    public static final double STORAGE_GROWTH_RATE = 1.60; // +60% per level
    public static final double DAILY_COST_GROWTH_RATE = 1.20; // +20% per level
    public static final double POLLUTION_REDUCTION_RATE = 1.0; // No pollution, no change

    // Upgrade
    public static final double UPGRADE_COST_BASE = 4000.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.5;
    public static final int UPGRADE_TIME_BASE = 3;

    // ========== Constructor ==========

    /**
     * Creates a new Battery Storage facility.
     * 
     * @param id Unique identifier for this storage
     */
    public BatteryStorage(String id) {
        super(id, BASE_POWER_OUTPUT, BASE_STORAGE_CAPACITY, BASE_DAILY_COST,
                BASE_POLLUTION_RATE, BASE_CONSTRUCTION_TIME, DEFAULT_MAX_LEVEL);
    }
}
