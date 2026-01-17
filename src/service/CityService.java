package service;

import model.entity.City;

/**
 * Interface definition for City orchestration operations.
 */
public interface CityService {
    /**
     * Orchestrates the daily simulation for the city.
     */
    /**
     * Simulates one hour of city life.
     * Updates time, processes events, energy, finances, and happiness.
     *
     * @param city The city to simulate
     */
    void simulateHour(City city);

    /**
     * Updates global metrics (population, pollution, etc.).
     */
    void calculateGlobalMetrics(City city);

    /**
     * Manages energy distribution between production and demand.
     */
    void manageEnergy(City city);

    /**
     * Processes financial transactions (revenue and operating costs).
     */
    void processFinances(City city);

    /**
     * Recalculates city happiness based on several factors.
     */
    void updateGlobalHappiness(City city);

    /**
     * Handles automatic urban expansion (upgrading or building residences)
     * when the city is saturated and happy.
     */
    void manageUrbanExpansion(City city);
}
