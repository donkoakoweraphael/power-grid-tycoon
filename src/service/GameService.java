package service;

import model.entity.PowerPlant;

/**
 * Service defining all possible player actions.
 * Acts as the business logic entry point for user interactions.
 */
public interface GameService {

    /**
     * Allows a player to buy and place a new power plant at specific coordinates.
     */
    void buyPowerPlant(model.GameModel model, String type, String id, int x, int y);

    /**
     * Allows a player to build a new residence at specific coordinates.
     */
    void buildResidence(model.GameModel model, String id, int x, int y);

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
     * Advances the game to the next time step (Hour).
     */
    void nextDay(model.GameModel model);

    /**
     * Advances the game by multiple days.
     */
    void nextDays(model.GameModel model, int days);

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

    /**
     * Retrieves metadata for a save slot.
     */
    service.dto.SaveMetadata getSaveMetadata(String fileName);
}
