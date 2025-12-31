package model.entite;

/**
 * Natural gas power plant.
 * Fast construction, stable and modular production, less polluting than coal.
 */
public class NaturalGasPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    protected static final double baseConstructionCost = 1500.0;
    protected static final int baseConstructionTime = 2;
    protected static final int defaultMaxLevel = 5;

    // Stats at level 1
    protected static final double basePowerOutput = 100.0;
    protected static final double baseStorageCapacity = 150.0;
    protected static final double baseDailyCost = 85.0;
    protected static final double basePollutionRate = 50.0;

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.42; // +42% per level
    protected static final double storageGrowthRate = 1.38; // +38% per level
    protected static final double dailyCostGrowthRate = 1.20; // +20% per level
    protected static final double pollutionReductionRate = 0.96; // -4% per level (better than coal)

    // Upgrade
    protected static final double upgradeCostBase = 2000.0;
    protected static final double upgradeCostMultiplier = 1.5;
    protected static final int upgradeTimeBase = 3;

    // ========== Constructor ==========

    /**
     * Creates a new Natural Gas Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public NaturalGasPlant(String id) {
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
