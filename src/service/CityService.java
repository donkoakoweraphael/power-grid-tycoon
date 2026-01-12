package service;

import model.entity.City;

/**
 * Interface definition for City orchestration operations.
 */
public interface CityService {
    /**
     * Orchestrates the daily simulation for the city.
     */
    void simulateDay(City city);
}
