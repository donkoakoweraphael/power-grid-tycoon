package model.observer;

import model.GameModel;

/**
 * Interface for objects that want to be notified when the game state changes.
 */
public interface GameObserver {
    /**
     * Called when the game state, city data, or metrics change.
     */
    void onGameStateChanged(GameModel model);
}
