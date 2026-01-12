import model.GameModel;
import controller.GameController;
import service.GameService;
import service.impl.GameServiceImpl;
import view.GameView;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Power Grid Tycoon game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Setup Services
            GameService gameService = new GameServiceImpl();

            // 2. Create a new game model
            GameModel model = gameService.createNewGame("Anticity");

            // 3. Setup Controller
            GameController controller = new GameController(model);

            // 4. Launch View
            new GameView(controller);

            System.out.println("Game started: Anticity is under your control.");
        });
    }
}
