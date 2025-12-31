package model.entite;

/**
 * Interface defining all operations that can be performed on a power plant.
 * This interface separates the contract from the implementation.
 */
public interface PowerPlantOperations {

    // ========== Lifecycle Management Methods ==========

    /**
     * Advances the plant by one day cycle.
     * Decrements remainingTime if under construction or upgrading.
     * Updates status when construction/upgrade completes.
     */
    void advanceCycle();

    /**
     * Checks if the plant can be upgraded to the next level.
     * 
     * @return true if level < maxLevel and plant is ACTIVE, false otherwise
     */
    boolean canUpgrade();

    /**
     * Starts the upgrade process to the next level.
     * Sets status to UPGRADING and initializes upgrade timing.
     * 
     * @throws IllegalStateException if plant cannot be upgraded
     */
    void startUpgrade();

    /**
     * Completes the construction process.
     * Changes status from UNDER_CONSTRUCTION to INACTIVE.
     * Calculates initial upgrade cost.
     */
    void completeConstruction();

    /**
     * Activates the power plant.
     * Changes status from INACTIVE to ACTIVE.
     * 
     * @throws IllegalStateException if plant is under construction or upgrading
     */
    void activate();

    /**
     * Deactivates the power plant.
     * Changes status from ACTIVE to INACTIVE.
     */
    void deactivate();

    // ========== Energy Management Methods ==========

    /**
     * Produces energy for the current day.
     * Only produces if status is ACTIVE.
     * 
     * @return the amount of energy produced in MW (0 if not active)
     */
    double produceEnergy();

    /**
     * Stores energy in the plant's storage.
     * 
     * @param amount the amount of energy to store in MWh
     * @return the actual amount stored (limited by available capacity)
     */
    double storeEnergy(double amount);

    /**
     * Releases stored energy from the plant.
     * 
     * @param requested the amount of energy requested in MWh
     * @return the actual amount released (limited by current storage)
     */
    double releaseEnergy(double requested);

    /**
     * Gets the total available energy from this plant.
     * Includes both current production and stored energy.
     * 
     * @return total available energy in MWh
     */
    double getAvailableEnergy();

    // ========== Financial & Pollution Calculations ==========

    /**
     * Calculates the daily operational cost.
     * Returns 0 if plant is not ACTIVE.
     * 
     * @return daily cost in coins
     */
    double calculateDailyCost();

    /**
     * Calculates the cost to upgrade to the next level.
     * Uses the formula: upgradeCostBase * (upgradeCostMultiplier ^ (level - 1))
     * 
     * @return upgrade cost in coins, or 0 if at max level
     */
    double calculateUpgradeCost();

    /**
     * Calculates the time required to upgrade to the next level.
     * 
     * @return upgrade time in days, or 0 if at max level
     */
    int calculateUpgradeTime();

    /**
     * Calculates the pollution produced for the current day.
     * Returns 0 if plant is not ACTIVE.
     * 
     * @return pollution in Pollution Points (PP) per day
     */
    double calculatePollution();

    // ========== Status & Information Methods ==========

    /**
     * Checks if the plant is operational (ACTIVE status).
     * 
     * @return true if status is ACTIVE, false otherwise
     */
    boolean isOperational();

    /**
     * Checks if the plant is under construction.
     * 
     * @return true if status is UNDER_CONSTRUCTION, false otherwise
     */
    boolean isUnderConstruction();

    /**
     * Checks if the plant is being upgraded.
     * 
     * @return true if status is UPGRADING, false otherwise
     */
    boolean isUpgrading();

    /**
     * Gets a human-readable description of the current status.
     * 
     * @return status description string
     */
    String getStatusDescription();

    /**
     * Calculates the storage to production efficiency ratio.
     * Useful for strategic decisions (higher ratio = better buffering capacity).
     * 
     * @return ratio of storageCapacity / powerOutput, or infinity if powerOutput is
     *         0
     */
    double getEfficiencyRatio();
}
