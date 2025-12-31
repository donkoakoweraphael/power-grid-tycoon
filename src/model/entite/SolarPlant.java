package model.entite;

/**
 * Solar power plant.
 * Low operating costs, ecological (zero pollution), but lower power output.
 * High storage ratio to buffer for night/day cycles.
 */
public class SolarPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    protected static final double baseConstructionCost = 800.0;
    protected static final int baseConstructionTime = 1;
    protected static final int defaultMaxLevel = 4;

    // Stats at level 1
    protected static final double basePowerOutput = 40.0;
    protected static final double baseStorageCapacity = 160.0; // 4x ratio for day/night buffering
    protected static final double baseDailyCost = 20.0;
    protected static final double basePollutionRate = 0.0; // Eco-friendly

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.50; // +50% per level
    protected static final double storageGrowthRate = 1.45; // +45% per level
    protected static final double dailyCostGrowthRate = 1.15; // +15% per level
    protected static final double pollutionReductionRate = 1.0; // No pollution, no change

    // Upgrade
    protected static final double upgradeCostBase = 1000.0;
    protected static final double upgradeCostMultiplier = 1.4;
    protected static final int upgradeTimeBase = 2;

    // ========== Constructor ==========

    /**
     * Creates a new Solar Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public SolarPlant(String id) {
        super(id, basePowerOutput, baseStorageCapacity, baseDailyCost,
                basePollutionRate, baseConstructionTime, defaultMaxLevel);
    }

    // ========== Static Getters for Class Variables ==========

    public static double getBaseConstructionCost() {
        return baseConstructionCost;
    }

    public static int getBaseConstructionTime() {
        return baseConstructionTime;
    }

    public static int getDefaultMaxLevel() {
        return defaultMaxLevel;
    }
}
