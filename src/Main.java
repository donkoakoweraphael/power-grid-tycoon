import controller.GameController;
import javax.swing.SwingUtilities;

/**
 * Entry point for the Power Grid Tycoon game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Setup Controller
            GameController controller = new GameController();

            // 2. Start Application (shows menu)
            controller.start();

            System.out.println("Power Grid Tycoon started.");
        });
    }
}
