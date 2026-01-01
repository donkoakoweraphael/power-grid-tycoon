package model.entite;

import java.util.List;

/**
 * Interface defining the operations for managing a city.
 * Orchestrates residences, power plants, and global city metrics.
 */
public interface CityOperations {

    /**
     * Advances the city simulation by one day.
     * Triggers production, distribution, and metric updates.
     */
    void advanceDay();

    /**
     * Distributes available energy from power plants to residences.
     * Prioritizes supply based on city logic and updates isSupplied status for
     * residences.
     */
    void distributeEnergy();

    /**
     * Recalculates global city metrics (population, demand, happiness, pollution).
     */
    void updateGlobalMetrics();

    /**
     * Handles the weekly cycle logic (population growth and urban expansion).
     * Triggered every 7 days.
     */
    void handleWeeklyGrowth();

    // ========== Getters & Setters ==========

    String getName();

    int getCurrentDay();

    double getTotalCoins();

    void setTotalCoins(double coins);

    double getElectricityPrice();

    void setElectricityPrice(double price);

    double getGlobalHappiness();

    int getTotalPopulation();

    double getTotalPollution();

    double getTotalEnergyAvailable();

    double getTotalStorageCapacity();

    double getTotalEnergyDemand();

    double getAvgPurchasingPower();

    List<PowerPlant> getPowerPlants();

    List<Residence> getResidences();

    void addPowerPlant(PowerPlant plant);

    void addResidence(Residence residence);
}
