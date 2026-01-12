package exception;

/**
 * Thrown when the player does not have enough coins for an action.
 */
public class InsufficientFundsException extends GameException {
    public InsufficientFundsException(double required, double current) {
        super(String.format("Insufficient funds: required %.2f, but only have %.2f", required, current));
    }
}
