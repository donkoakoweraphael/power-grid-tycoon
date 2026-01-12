package service;

import model.entity.City;
import model.entity.PowerPlant;

/**
 * Service defining all possible player actions.
 * Acts as the business logic entry point for user interactions.
 */
public interface GameService {

    /**
     * Allows a player to buy and place a new power plant.
     */
    void buyPowerPlant(City city, String type, String id);

    /**
     * Starts an upgrade for a specific building (PowerPlant or Residence).
     */
    void upgradeBuilding(City city, String buildingId);

    /**
     * Changes the electricity price set by the player.
     */
    void setElectricityPrice(City city, double newPrice);

    /**
     * Manually toggles the operational status of a plant.
     */
    void togglePlantStatus(PowerPlant plant);

    /**
     * Advances the game to the next day (The "Next Day" button action).
     */
    void nextDay(City city);
}
