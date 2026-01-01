package model.entite;

/**
 * Abstract base class representing a power plant in the game.
 * All specific power plant types inherit from this class.
 */
public abstract class PowerPlant extends Building implements PowerPlantOperations {

    // ========== Class Variables (Static) - Default Values ==========

    // Construction defaults
    public static final double BASE_CONSTRUCTION_COST = 1000.0;
    public static final int BASE_CONSTRUCTION_TIME = 2;
    public static final int DEFAULT_MAX_LEVEL = 5;

    // Stats at level 1 - defaults
    public static final double BASE_POWER_OUTPUT = 100.0;
    public static final double BASE_STORAGE_CAPACITY = 150.0;
    public static final double BASE_DAILY_COST = 50.0;
    public static final double BASE_POLLUTION_RATE = 50.0;

    // Growth rates per level - defaults
    public static final double POWER_OUTPUT_GROWTH_RATE = 1.40;
    public static final double STORAGE_GROWTH_RATE = 1.40;
    public static final double DAILY_COST_GROWTH_RATE = 1.20;
    public static final double POLLUTION_REDUCTION_RATE = 0.95;

    // Upgrade defaults
    public static final double UPGRADE_COST_BASE = 1500.0;
    public static final double UPGRADE_COST_MULTIPLIER = 1.5;
    public static final int UPGRADE_TIME_BASE = 3;

    // ========== Instance Variables ==========

    // id, level, maxLevel inherited from GameEntity

    /**
     * Current operational status of the power plant.
     */
    private PlantStatus status;

    /**
     * Current power output in MW per day.
     */
    private double powerOutput;

    /**
     * Storage capacity in MWh.
     */
    private double storageCapacity;

    /**
     * Current amount of energy stored in MWh.
     */
    private double currentEnergyStored;

    /**
     * Daily cost (maintenance + operating) in coins per day.
     */
    private double dailyCost;

    /**
     * Pollution produced per day in Pollution Points (PP).
     */
    private double pollutionRate;

    /**
     * Cost to upgrade to the next level in coins.
     */
    private double upgradeCost;

    /**
     * Time required for upgrade in days.
     */
    private int upgradeTime;

    /**
     * Remaining time for construction or upgrade in days.
     */
    private int remainingTime;

    // ========== Constructor ==========

    /**
     * Constructor for PowerPlant.
     * 
     * @param id                  Unique identifier
     * @param basePowerOutput     Base power output at level 1
     * @param baseStorageCapacity Base storage capacity at level 1
     * @param baseDailyCost       Base daily cost at level 1
     * @param basePollutionRate   Base pollution rate at level 1
     * @param constructionTime    Time required for construction
     * @param maxLevel            Maximum level for this plant
     */
    protected PowerPlant(String id, double basePowerOutput, double baseStorageCapacity,
            double baseDailyCost, double basePollutionRate,
            int constructionTime, int maxLevel) {
        super(id, maxLevel);
        this.status = PlantStatus.UNDER_CONSTRUCTION;
        this.powerOutput = basePowerOutput;
        this.storageCapacity = baseStorageCapacity;
        this.currentEnergyStored = 0.0;
        this.dailyCost = baseDailyCost;
        this.pollutionRate = basePollutionRate;
        this.remainingTime = constructionTime;
        this.upgradeCost = 0.0; // Will be calculated after construction
        this.upgradeTime = 0;
    }

    // ========== Getters ==========

    // id, level, maxLevel getters are inherited from GameEntity

    public PlantStatus getStatus() {
        return status;
    }

    public double getPowerOutput() {
        return powerOutput;
    }

    public double getStorageCapacity() {
        return storageCapacity;
    }

    public double getCurrentEnergyStored() {
        return currentEnergyStored;
    }

    public double getDailyCost() {
        return dailyCost;
    }

    public double getPollutionRate() {
        return pollutionRate;
    }

    public double getUpgradeCost() {
        return upgradeCost;
    }

    public int getUpgradeTime() {
        return upgradeTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    // ========== Lifecycle Management Methods ==========
    // Implementation of PowerPlantOperations interface

    @Override
    public void advanceCycle() {
        // To be implemented
    }

    @Override
    public boolean canUpgrade() {
        // To be implemented
        return false;
    }

    @Override
    public void startUpgrade() {
        // To be implemented
    }

    @Override
    public void completeConstruction() {
        // To be implemented
    }

    @Override
    public void activate() {
        // To be implemented
    }

    @Override
    public void deactivate() {
        // To be implemented
    }

    // ========== Energy Management Methods ==========

    @Override
    public double produceEnergy() {
        // To be implemented
        return 0.0;
    }

    @Override
    public double storeEnergy(double amount) {
        // To be implemented
        return 0.0;
    }

    @Override
    public double releaseEnergy(double requested) {
        // To be implemented
        return 0.0;
    }

    @Override
    public double getAvailableEnergy() {
        // To be implemented
        return 0.0;
    }

    // ========== Financial & Pollution Calculations ==========

    @Override
    public double calculateDailyCost() {
        // To be implemented
        return 0.0;
    }

    @Override
    public double calculateUpgradeCost() {
        // To be implemented
        return 0.0;
    }

    @Override
    public int calculateUpgradeTime() {
        // To be implemented
        return 0;
    }

    @Override
    public double calculatePollution() {
        // To be implemented
        return 0.0;
    }

    // ========== Status & Information Methods ==========

    @Override
    public boolean isOperational() {
        // To be implemented
        return false;
    }

    @Override
    public boolean isUnderConstruction() {
        // To be implemented
        return false;
    }

    @Override
    public boolean isUpgrading() {
        // To be implemented
        return false;
    }

    @Override
    public String getStatusDescription() {
        // To be implemented
        return "";
    }

    @Override
    public double getEfficiencyRatio() {
        // To be implemented
        return 0.0;
    }

    /**
     * Gets a detailed string representation of this power plant.
     * 
     * @return detailed plant information
     */
    @Override
    public String toString() {
        // To be implemented
        return "";
    }
}
