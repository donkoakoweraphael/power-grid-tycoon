package model.entite;

/**
 * Abstract base class representing a power plant in the game.
 * All specific power plant types inherit from this class.
 */
public abstract class PowerPlant {

    // ========== Class Variables (Static) - Default Values ==========

    // Construction defaults
    protected static final double baseConstructionCost = 1000.0;
    protected static final int baseConstructionTime = 2;
    protected static final int defaultMaxLevel = 5;

    // Stats at level 1 - defaults
    protected static final double basePowerOutput = 100.0;
    protected static final double baseStorageCapacity = 150.0;
    protected static final double baseDailyCost = 50.0;
    protected static final double basePollutionRate = 50.0;

    // Growth rates per level - defaults
    protected static final double powerOutputGrowthRate = 1.40;
    protected static final double storageGrowthRate = 1.40;
    protected static final double dailyCostGrowthRate = 1.20;
    protected static final double pollutionReductionRate = 0.95;

    // Upgrade defaults
    protected static final double upgradeCostBase = 1500.0;
    protected static final double upgradeCostMultiplier = 1.5;
    protected static final int upgradeTimeBase = 3;

    // ========== Instance Variables ==========

    /**
     * Unique identifier for this power plant.
     */
    private String id;

    /**
     * Current level of the power plant (starts at 1).
     */
    private int level;

    /**
     * Maximum level this power plant can reach.
     */
    private int maxLevel;

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
        this.id = id;
        this.level = 1;
        this.maxLevel = maxLevel;
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

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

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

    // ========== Setters ==========

    protected void setLevel(int level) {
        this.level = level;
    }

    protected void setStatus(PlantStatus status) {
        this.status = status;
    }

    protected void setPowerOutput(double powerOutput) {
        this.powerOutput = powerOutput;
    }

    protected void setStorageCapacity(double storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    protected void setCurrentEnergyStored(double currentEnergyStored) {
        this.currentEnergyStored = currentEnergyStored;
    }

    protected void setDailyCost(double dailyCost) {
        this.dailyCost = dailyCost;
    }

    protected void setPollutionRate(double pollutionRate) {
        this.pollutionRate = pollutionRate;
    }

    protected void setUpgradeCost(double upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    protected void setUpgradeTime(int upgradeTime) {
        this.upgradeTime = upgradeTime;
    }

    protected void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
