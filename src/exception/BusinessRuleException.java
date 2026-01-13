package exception;

/**
 * Thrown when a business rule is violated (e.g., upgrading a max-level
 * building).
 */
public class BusinessRuleException extends GameException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
