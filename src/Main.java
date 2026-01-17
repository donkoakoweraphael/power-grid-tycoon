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
        // Set system Look & Feel (Windows native on Windows)
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            // Fallback to default if system L&F not available
        }
        
        // Menu principal
        String[] options = {"Nouvelle Partie", "Charger une Partie", "Quitter"};
        int choice = javax.swing.JOptionPane.showOptionDialog(
            null,
            "Bienvenue dans Power Grid Tycoon!\n\nQue voulez-vous faire?",
            "Power Grid Tycoon",
            javax.swing.JOptionPane.DEFAULT_OPTION,
            javax.swing.JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);
        
        GameController controller = new GameController();
        String cityName = "SimCity";
        
        if (choice == 0) {
            // Nouvelle partie - demander le nom de la ville
            cityName = javax.swing.JOptionPane.showInputDialog(
                null, 
                "Entrez le nom de votre ville:", 
                "Nouvelle Partie",
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            
            if (cityName == null || cityName.trim().isEmpty()) {
                cityName = "SimCity";
            }
            controller.startConsole(cityName.trim());
            
        } else if (choice == 1) {
            // Charger une partie - lister les sauvegardes disponibles
            java.io.File savesDir = new java.io.File("saves");
            String[] saveFiles = savesDir.list((dir, name) -> name.endsWith(".tycoon"));
            
            if (saveFiles == null || saveFiles.length == 0) {
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Aucune sauvegarde trouvee!\n\nDemarrage d'une nouvelle partie.",
                    "Pas de sauvegarde",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                controller.startConsole("SimCity");
            } else {
                // Formatter les noms (retirer .tycoon)
                String[] saveNames = new String[saveFiles.length];
                for (int i = 0; i < saveFiles.length; i++) {
                    saveNames[i] = saveFiles[i].replace(".tycoon", "");
                }
                
                String selectedSave = (String) javax.swing.JOptionPane.showInputDialog(
                    null,
                    "Choisissez une sauvegarde:",
                    "Charger une Partie",
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    saveNames,
                    saveNames[0]);
                
                if (selectedSave != null) {
                    try {
                        controller.startConsole("temp");
                        controller.loadGame(selectedSave);
                        cityName = controller.getModel().getCity().getName();
                    } catch (Exception e) {
                        javax.swing.JOptionPane.showMessageDialog(null, 
                            "Erreur lors du chargement: " + e.getMessage(),
                            "Erreur",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                        controller.startConsole("SimCity");
                    }
                } else {
                    controller.startConsole("SimCity");
                }
            }
        } else {
            // Quitter
            System.exit(0);
        }
        
        final String finalCityName = cityName;
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameFrame(controller, controller.getModel(), finalCityName);
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
                    } catch (Exception e) {
                        System.out.println("Erreur: " + e.getMessage());
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
                    } catch (Exception e) {
                        System.out.println("Erreur: " + e.getMessage());
                    }
                }
            } else if (input.startsWith("u ")) {
                 String[] parts = input.split(" ");
                if (parts.length == 2) controller.handleUpgradeBuilding(parts[1]);
            }
        }
    }
}
