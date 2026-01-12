package service;

import model.GameModel;

/**
 * Service for saving and loading game states to/from files.
 */
public interface PersistenceService {
    /**
     * Saves the current game state to a file.
     */
    void save(GameModel model, String fileName);

    /**
     * Loads a game state from a file.
     */
    GameModel load(String fileName);
}
