import controller.GameController;
import java.util.Scanner;

/**
 * Entry point for the Power Grid Tycoon game (Console Version).
 */
public class Main {
    public static void main(String[] args) {
        GameController controller = new GameController();
        System.out.println("Starting Power Grid Tycoon Engine...");
        
        controller.startConsole("SimCity");
        
        // Simple Loop for testing
        Scanner scanner = new Scanner(System.in);
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
