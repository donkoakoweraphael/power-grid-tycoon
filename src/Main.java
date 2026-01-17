import controller.GameController;
import view.swing.GameFrame;
import java.util.Scanner;

/**
 * Entry point for Power Grid Tycoon.
 * Choose between Console or Swing UI.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Power Grid Tycoon ===");
        System.out.println("1. Mode Console");
        System.out.println("2. Mode Interface Graphique");
        System.out.print("Choisissez le mode (1 ou 2): ");
        
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        
        if (choice.equals("2")) {
            launchSwingUI();
        } else {
            launchConsole(scanner);
        }
    }
    
    private static void launchSwingUI() {
        GameController controller = new GameController();
        controller.startConsole("SimCity");
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameFrame(controller, controller.getModel());
        });
    }
    
    private static void launchConsole(Scanner scanner) {
        GameController controller = new GameController();
        System.out.println("Starting Power Grid Tycoon Engine...");
        
        controller.startConsole("SimCity");
        
        // Simple Loop for testing
        while(true) {
            System.out.println("\nCommands: [n]ext, [m]ap, [i]nfo <x> <y>, [b]uy <type> <x> <y>, build [h]ouse <x> <y>, [u]pgrade <id>, [q]uit");
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equals("q")) break;
            
            if (input.equals("n")) {
                controller.handleNextDay();
            } else if (input.equals("m")) {
                controller.printMap();
            } else if (input.startsWith("i ") || input.startsWith("info ")) {
                // Format: i 2 3
                String[] parts = input.split(" ");
                if (parts.length == 3) {
                    try {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        controller.handleInfo(x, y);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                }
            } else if (input.startsWith("b ")) {
                // Format: b solar 2 3
                String[] parts = input.split(" ");
                if (parts.length == 4) {
                    try {
                        String type = parts[1];
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        controller.handleBuyPlant(type, type + "-" + System.currentTimeMillis(), x, y);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                }
            } else if (input.startsWith("build h ")) {
                 // Format: build h 2 3
                String[] parts = input.split(" ");
                if (parts.length == 4) {
                    try {
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        controller.handleBuildResidence(x, y);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates.");
                    }
                }
            } else if (input.startsWith("u ")) {
                 String[] parts = input.split(" ");
                if (parts.length == 2) controller.handleUpgradeBuilding(parts[1]);
            }
        }
    }
}
