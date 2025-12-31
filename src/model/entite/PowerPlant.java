package model.entite;

/**
 * Abstract base class representing a power plant in the game.
 * All specific power plant types inherit from this class.
 */
public abstract class PowerPlant implements PowerPlantOperations {

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

    // ========== Lifecycle Management Methods ==========
    // Implementation of PowerPlantOperations interface

    /**
     * Advances the plant by one day cycle.
     * Decrements remainingTime if under construction or upgrading.
     * Updates status when construction/upgrade completes.
     */
    @Override
    public void advanceCycle() {
        // To be implemented
    }

    /**
     * Checks if the plant can be upgraded to the next level.
     * 
     * @return true if level < maxLevel and plant is ACTIVE, false otherwise
     */
    @Override
    public boolean canUpgrade() {
        // To be implemented
        return false;
    }

    /**
     * Starts the upgrade process to the next level.
     * Sets status to UPGRADING and initializes upgrade timing.
     * 
     * @throws IllegalStateException if plant cannot be upgraded
     */
    @Override
    public void startUpgrade() {
        // To be implemented
    }

    /**
     * Completes the construction process.
     * Changes status from UNDER_CONSTRUCTION to INACTIVE.
     * Calculates initial upgrade cost.
     */
    @Override
    public void completeConstruction() {
        // To be implemented
    }

    /**
     * Activates the power plant.
     * Changes status from INACTIVE to ACTIVE.
     * 
     * @throws IllegalStateException if plant is under construction or upgrading
     */
    @Override
    public void activate() {
        // To be implemented
    }

    /**
     * Deactivates the power plant.
     * Changes status from ACTIVE to INACTIVE.
     */
    @Override
    public void deactivate() {
        // To be implemented
    }

    // ========== Energy Management Methods ==========

    /**
     * Produces energy for the current day.
     * Only produces if status is ACTIVE.
     * 
     * @return the amount of energy produced in MW (0 if not active)
     */
    @Override
    public double produceEnergy() {
        // To be implemented
        return 0.0;
    }

    /**
     * Stores energy in the plant's storage.
     * 
     * @param amount the amount of energy to store in MWh
     * @return the actual amount stored (limited by available capacity)
     */
    @Override
    public double storeEnergy(double amount) {
        // To be implemented
        return 0.0;
    }

    /**
     * Releases stored energy from the plant.
     * 
     * @param requested the amount of energy requested in MWh
     * @return the actual amount released (limited by current storage)
     */
    @Override
    public double releaseEnergy(double requested) {
        // To be implemented
        return 0.0;
    }

    /**
     * Gets the total available energy from this plant.
     * Includes both current production and stored energy.
     * 
     * @return total available energy in MWh
     */
    @Override
    public double getAvailableEnergy() {
        // To be implemented
        return 0.0;
    }

    // ========== Financial & Pollution Calculations ==========

    /**
     * Calculates the daily operational cost.
     * Returns 0 if plant is not ACTIVE.
     * 
     * @return daily cost in coins
     */
    @Override
    public double calculateDailyCost() {
        // To be implemented
        return 0.0;
    }

    /**
     * Calculates the cost to upgrade to the next level.
     * Uses the formula: upgradeCostBase * (upgradeCostMultiplier ^ (level - 1))
     * 
     * @return upgrade cost in coins, or 0 if at max level
     */
    @Override
    public double calculateUpgradeCost() {
        // To be implemented
        return 0.0;
    }

    /**
     * Calculates the time required to upgrade to the next level.
     * 
     * @return upgrade time in days, or 0 if at max level
     */
    @Override
    public int calculateUpgradeTime() {
        // To be implemented
        return 0;
    }

    /**
     * Calculates the pollution produced for the current day.
     * Returns 0 if plant is not ACTIVE.
     * 
     * @return pollution in Pollution Points (PP) per day
     */
    @Override
    public double calculatePollution() {
        // To be implemented
        return 0.0;
    }

    // ========== Status & Information Methods ==========

    /**
     * Checks if the plant is operational (ACTIVE status).
     * 
     * @return true if status is ACTIVE, false otherwise
     */
    @Override
    public boolean isOperational() {
        // To be implemented
        return false;
    }

    /**
     * Checks if the plant is under construction.
     * 
     * @return true if status is UNDER_CONSTRUCTION, false otherwise
     */
    @Override
    public boolean isUnderConstruction() {
        // To be implemented
        return false;
    }

    /**
     * Checks if the plant is being upgraded.
     * 
     * @return true if status is UPGRADING, false otherwise
     */
    @Override
    public boolean isUpgrading() {
        // To be implemented
        return false;
    }

    /**
     * Gets a human-readable description of the current status.
     * 
     * @return status description string
     */
    @Override
    public String getStatusDescription() {
        // To be implemented
        return "";
    }

    /**
     * Calculates the storage to production efficiency ratio.
     * Useful for strategic decisions (higher ratio = better buffering capacity).
     * 
     * @return ratio of storageCapacity / powerOutput, or infinity if powerOutput is
     *         0
     */
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
