package model.entite;

/**
 * Interface defining all operations that can be performed on a residence.
 * Separates the contract from the implementation, following the architecture
 * used for PowerPlant.
 */
public interface ResidenceOperations {

    // ========== Getters ==========

    String getId();

    int getLevel();

    int getMaxLevel();

    int getMaxCapacity();

    int getCurrentOccupancy();

    /**
     * Gets the daily energy demand of this residence in MWh.
     * 
     * @return energy demand per day
     */
    double getEnergyDemand();

    double getEnergyDemandMin();

    double getEnergyDemandMax();

    /**
     * Gets the purchasing power of this residence (price they are willing to pay).
     * 
     * @return purchasing power in coins/MWh
     */
    double getPurchasingPower();

    double getPurchasingPowerMin();

    double getPurchasingPowerMax();

    /**
     * Checks if the residence has been sufficiently supplied with energy for the
     * current cycle.
     * 
     * @return true if supplied energy >= energy demand
     */
    boolean isSupplied();

    /**
     * Gets the remaining capacity for new occupants.
     * 
     * @return maxCapacity - currentOccupancy
     */
    int getAvailableCapacity();

    /**
     * Checks if the residence is full.
     * 
     * @return true if currentOccupancy >= maxCapacity
     */
    boolean isFull();

    // ========== Operations ==========

    /**
     * Assigns a number of occupants to the residence.
     * Updates currentOccupancy.
     * 
     * @param count Number of new occupants to add (can be negative to remove)
     * @return The actual number of occupants added (limited by capacity)
     */
    int assignOccupants(int count);

    /**
     * Supplies energy to the residence for the current day.
     * Updates the isSupplied status based on whether the amount meets the demand.
     * 
     * @param amount Energy supplied in MWh
     */
    void supplyEnergy(double amount);

    /**
     * Upgrades the residence to the next level.
     * Increases capacity and regenerates demand/purchasing power values.
     * 
     * @throws IllegalStateException if already at max level
     */
    void upgrade();

    /**
     * Regenerates random values for energy demand and purchasing power
     * within the bounds defined by the current level.
     * Simulates fluctuation in market conditions or usage patterns.
     */
    void regenerateRandomValues();
}
