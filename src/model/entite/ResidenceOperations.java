package model.entite;

/**
 * Interface defining all operations that can be performed on a residence.
 * Separates the contract from the implementation, following the architecture
 * used for PowerPlant.
 */
public interface ResidenceOperations {
    
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
     * Regenerates random values for energy demand and purchasing power
     * within the bounds defined by the current level.
     * Simulates fluctuation in market conditions or usage patterns.
     */
    void regenerateRandomValues();

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
}
