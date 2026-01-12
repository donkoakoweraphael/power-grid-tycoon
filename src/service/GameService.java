package service;

import model.entity.PowerPlant;

/**
 * Service defining all possible player actions.
 * Acts as the business logic entry point for user interactions.
 */
public interface GameService {

    /**
     * Allows a player to buy and place a new power plant.
     */
    void buyPowerPlant(model.GameModel model, String type, String id);

    /**
     * Starts an upgrade for a specific building (PowerPlant or Residence).
     */
    void upgradeBuilding(model.GameModel model, String buildingId);

    /**
     * Changes the electricity price set by the player.
     */
    void setElectricityPrice(model.GameModel model, double newPrice);

    /**
     * Manually toggles the operational status of a plant.
     */
    void togglePlantStatus(model.GameModel model, PowerPlant plant);

    /**
     * Advances the game to the next day (The "Next Day" button action).
     */
    void nextDay(model.GameModel model);

    /**
     * Creates a new game with default starting conditions.
     */
    model.GameModel createNewGame(String cityName);

    /**
     * Saves the current game.
     */
    void saveGame(model.GameModel model, String fileName);

    /**
     * Loads a game save.
     */
    model.GameModel loadGame(String fileName);
}
