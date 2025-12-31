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
    protected static final double baseConstructionCost = 3000.0;
    protected static final int baseConstructionTime = 2;
    protected static final int defaultMaxLevel = 5;

    // Stats at level 1
    protected static final double basePowerOutput = 0.0; // No production, only storage
    protected static final double baseStorageCapacity = 2500.0; // Massive storage capacity
    protected static final double baseDailyCost = 45.0;
    protected static final double basePollutionRate = 0.0; // Eco-friendly

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.0; // No growth (always 0)
    protected static final double storageGrowthRate = 1.60; // +60% per level
    protected static final double dailyCostGrowthRate = 1.20; // +20% per level
    protected static final double pollutionReductionRate = 1.0; // No pollution, no change

    // Upgrade
    protected static final double upgradeCostBase = 4000.0;
    protected static final double upgradeCostMultiplier = 1.5;
    protected static final int upgradeTimeBase = 3;

    // ========== Constructor ==========

    /**
     * Creates a new Battery Storage facility.
     * 
     * @param id Unique identifier for this storage
     */
    public BatteryStorage(String id) {
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
