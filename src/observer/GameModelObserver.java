package observer;

import model.GameModel;

/**
 * Interface for objects that want to be notified when the Game Model state
 * changes.
 */
public interface GameModelObserver {
    /**
     * Called when the game state, city data, or metrics change in the model.
     */
    void onModelUpdated(GameModel model);
}
