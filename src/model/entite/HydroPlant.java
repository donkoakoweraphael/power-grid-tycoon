package model.entite;

/**
 * Hydroelectric power plant.
 * Excellent storage capacity (natural reservoir), stable and ecological.
 * High construction cost but very low operating costs.
 */
public class HydroPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    protected static final double baseConstructionCost = 5000.0;
    protected static final int baseConstructionTime = 4;
    protected static final int defaultMaxLevel = 4;

    // Stats at level 1
    protected static final double basePowerOutput = 80.0;
    protected static final double baseStorageCapacity = 1200.0; // 15x ratio - natural reservoir!
    protected static final double baseDailyCost = 45.0;
    protected static final double basePollutionRate = 0.0; // Eco-friendly

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.38; // +38% per level
    protected static final double storageGrowthRate = 1.50; // +50% per level (expand reservoir)
    protected static final double dailyCostGrowthRate = 1.15; // +15% per level
    protected static final double pollutionReductionRate = 1.0; // No pollution, no change

    // Upgrade
    protected static final double upgradeCostBase = 6000.0;
    protected static final double upgradeCostMultiplier = 1.6;
    protected static final int upgradeTimeBase = 5;

    // ========== Constructor ==========

    /**
     * Creates a new Hydro Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public HydroPlant(String id) {
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
