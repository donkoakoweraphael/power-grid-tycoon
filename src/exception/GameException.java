package exception;

/**
 * Base class for all business logic exceptions in the game.
 */
public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }
}
