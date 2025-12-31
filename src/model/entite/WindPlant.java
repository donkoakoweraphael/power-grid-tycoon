package model.entite;

/**
 * Wind power plant.
 * Ecological (zero pollution), moderate costs, but power output varies.
 * Higher storage ratio to compensate for variability.
 */
public class WindPlant extends PowerPlant {

    // ========== Class Variables (Static) ==========

    // Construction
    protected static final double baseConstructionCost = 1200.0;
    protected static final int baseConstructionTime = 1;
    protected static final int defaultMaxLevel = 4;

    // Stats at level 1
    protected static final double basePowerOutput = 35.0;
    protected static final double baseStorageCapacity = 105.0; // 3x ratio for wind variability
    protected static final double baseDailyCost = 28.0;
    protected static final double basePollutionRate = 0.0; // Eco-friendly

    // Growth rates per level
    protected static final double powerOutputGrowthRate = 1.48; // +48% per level
    protected static final double storageGrowthRate = 1.42; // +42% per level
    protected static final double dailyCostGrowthRate = 1.18; // +18% per level
    protected static final double pollutionReductionRate = 1.0; // No pollution, no change

    // Upgrade
    protected static final double upgradeCostBase = 1500.0;
    protected static final double upgradeCostMultiplier = 1.4;
    protected static final int upgradeTimeBase = 2;

    // ========== Constructor ==========

    /**
     * Creates a new Wind Plant.
     * 
     * @param id Unique identifier for this plant
     */
    public WindPlant(String id) {
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
