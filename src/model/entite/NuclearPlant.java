package model.entite;

/**
 * Nuclear power plant.
 * Massive power production, low operating costs, minimal pollution.
 * Very expensive construction and long build time.
 */
public class NuclearPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    protected static final double baseConstructionCost = 8000.0;
    protected static final int baseConstructionTime = 5;
    protected static final int defaultMaxLevel = 3; // Fewer levels due to complexity

    // Stats at level 1
    protected static final double basePowerOutput = 400.0;
    protected static final double baseStorageCapacity = 800.0; // 2x ratio for safety reserves
    protected static final double baseDailyCost = 140.0;
    protected static final double basePollutionRate = 5.0; // Low (radioactive waste)

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.35; // +35% per level
    protected static final double storageGrowthRate = 1.40; // +40% per level
    protected static final double dailyCostGrowthRate = 1.25; // +25% per level
    protected static final double pollutionReductionRate = 0.90; // -10% per level (better waste management)

    // Upgrade
    protected static final double upgradeCostBase = 10000.0;
    protected static final double upgradeCostMultiplier = 2.0;
    protected static final int upgradeTimeBase = 6;

    // ========== Constructor ==========

    /**
     * Creates a new Nuclear Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public NuclearPlant(String id) {
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
