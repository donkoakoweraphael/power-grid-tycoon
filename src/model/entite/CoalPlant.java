package model.entite;

/**
 * Coal-fired power plant.
 * High power output and stable production, but high pollution and operating
 * costs.
 */
public class CoalPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    protected static final double baseConstructionCost = 1000.0;
    protected static final int baseConstructionTime = 2;
    protected static final int defaultMaxLevel = 5;

    // Stats at level 1
    protected static final double basePowerOutput = 120.0;
    protected static final double baseStorageCapacity = 180.0;
    protected static final double baseDailyCost = 110.0;
    protected static final double basePollutionRate = 100.0;

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.45; // +45% per level
    protected static final double storageGrowthRate = 1.40; // +40% per level
    protected static final double dailyCostGrowthRate = 1.22; // +22% per level
    protected static final double pollutionReductionRate = 0.98; // -2% per level (technology improvement)

    // Upgrade
    protected static final double upgradeCostBase = 1500.0;
    protected static final double upgradeCostMultiplier = 1.5;
    protected static final int upgradeTimeBase = 3;

    // ========== Constructor ==========

    /**
     * Creates a new Coal Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public CoalPlant(String id) {
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
