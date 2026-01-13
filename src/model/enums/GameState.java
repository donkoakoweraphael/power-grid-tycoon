package model.enums;

/**
 * Represents the current status of the game session.
 */
public enum GameState {
    /**
     * The game is active and time can advance.
     */
    RUNNING,

    /**
     * The game is paused.
     */
    PAUSED,

    /**
     * The player has lost (e.g., happiness at 0 or bankrupt).
     */
    GAME_OVER
}
