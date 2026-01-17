package exception;

/**
 * Thrown when the player does not have enough coins for an action.
 */
public class InsufficientFundsException extends GameException {
    public InsufficientFundsException(double required, double current) {
        super(String.format("Fonds insuffisants: %.2f requis, mais seulement %.2f disponibles", required, current));
    }
}
