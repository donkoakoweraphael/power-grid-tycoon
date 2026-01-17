package controller;

import model.GameModel;
import service.GameService;
import service.impl.GameServiceImpl;
import model.entity.PowerPlant;
import model.enums.PlantStatus;

/**
 * Main controller for the game.
 * Logic bridge only, no UI dependencies.
 */
public class GameController {
    private GameModel model;
    private GameService gameService;

    public GameController() {
        this.gameService = new GameServiceImpl();
    }

    public void startConsole(String cityName) {
        this.model = gameService.createNewGame(cityName);
        System.out.println("New Game Started: " + cityName);
        printStatus();
    }
    
    public void printStatus() {
        if (model == null) return;
        System.out.println("\n=== Day " + model.getCity().getCurrentDay() + " | TIME: " + model.getCity().getCurrentHour() + ":00 ===");
        System.out.printf("Money: %.2f | Pop: %d | Happiness: %.2f%%%n", 
            model.getCity().getTotalCoins(), 
            model.getCity().getTotalPopulation(), 
            model.getCity().getGlobalHappiness());
        
        // Calculate actual production for display
        double actualProduction = model.getCity().getPowerPlants().stream()
            .mapToDouble(p -> {
                if (p.getStatus() == PlantStatus.ACTIVE) {
                    return p.getPowerOutput() / 24.0;
                }
                return 0.0;
            }).sum();
        
        double demand = model.getCity().getTotalEnergyDemand();
        double deficit = demand - actualProduction;
        
        System.out.printf("Energy: %.2f MW production | %.2f MW demand", actualProduction, demand);
        if (deficit > 0) {
            System.out.printf(" | DEFICIT: %.2f MW%n", deficit);
        } else {
            System.out.printf(" | Surplus: %.2f MW%n", -deficit);
        }
        
        // Event Board
        if (!model.getCity().getEventLog().isEmpty()) {
            System.out.println("\n📰 RECENT EVENTS:");
            for (String event : model.getCity().getEventLog()) {
                System.out.println("  " + event);
            }
        }
    }

    public void handleNextDay() {
        gameService.nextDay(model);
        printStatus();
    }

    public void handleBuyPlant(String type, String id, int x, int y) {
        try {
            gameService.buyPowerPlant(model, type, id, x, y);
            System.out.println("Built " + type + " at (" + x + "," + y + ")");
            printMap(); // auto-show map
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void handleBuildResidence(int x, int y) {
        try {
            String id = "res-" + System.currentTimeMillis();
            gameService.buildResidence(model, id, x, y);
            System.out.println("Built House at (" + x + "," + y + ")");
            printMap();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handleUpgradeBuilding(String id) {
        try {
            gameService.upgradeBuilding(model, id);
            System.out.println("Upgraded building: " + id);
        } catch (Exception e) {
            System.out.println("Error upgrading: " + e.getMessage());
        }
    }
    
    
    // Add other methods if needed for console interaction
    
    public void handleInfo(int x, int y) {
        if (model == null) return;
        
        // Check bounds
        if (x < 0 || x >= model.getCity().getWidth() || y < 0 || y >= model.getCity().getHeight()) {
            System.out.println("Coordinates out of bounds.");
            return;
        }

        model.entity.Building[][] grid = model.getCity().getGrid();
        model.entity.Building b = grid[x][y];

        System.out.println("\n=== INFO (" + x + "," + y + ") ===");
        if (b == null) {
            System.out.println("Empty Lot.");
        } else {
            System.out.printf("Health: %.1f / %.1f%n", b.getHealth(), b.getMaxHealth());
            
            if (b instanceof model.entity.Residence) {
            
            if (b instanceof model.entity.Residence) {
                model.entity.Residence r = (model.entity.Residence) b;
                System.out.println("Type: RESIDENCE (Lvl " + r.getLevel() + ")");
                System.out.println("Occupancy: " + r.getCurrentOccupancy() + " / " + r.getMaxCapacity() + " people");
                System.out.printf("Current Demand: %.2f MW (Hour %d)%n", r.getByHourDemand(model.getCity().getCurrentHour()), model.getCity().getCurrentHour());
                System.out.println("Supplied: " + (r.isSupplied() ? "YES" : "NO"));
                System.out.printf("Purchasing Power: %.2f coins/MWh%n", r.getPurchasingPower());
            } else if (b instanceof model.entity.PowerPlant) {
            model.entity.PowerPlant p = (model.entity.PowerPlant) b;
            System.out.println("Type: " + p.getClass().getSimpleName() + " (Lvl " + p.getLevel() + ")");
            System.out.println("Status: " + p.getStatus());
            System.out.printf("Output: %.2f MW%n", p.getPowerOutput());
            if (p.getStorageCapacity() > 0) {
                 System.out.printf("Storage: %.2f / %.2f MWh%n", p.getCurrentEnergyStored(), p.getStorageCapacity());
            }
            System.out.printf("Op. Cost: %.2f coins/day%n", p.getDailyCost());
            System.out.printf("Pollution: %.2f PP/day%n", p.getPollutionRate());
        }
        }
        System.out.println("====================\n");
    }

    public void printMap() {
        if (model == null) return;
        
        model.entity.Building[][] grid = model.getCity().getGrid();
        System.out.println("\n=== CITY MAP ===");
        System.out.print("   ");
        for(int x=0; x<model.getCity().getWidth(); x++) System.out.print(x + "  ");
        System.out.println();
        
        for (int y = 0; y < model.getCity().getHeight(); y++) {
            System.out.print(y + " ");
            for (int x = 0; x < model.getCity().getWidth(); x++) {
                String symbol = "[ ]";
                if (grid[x][y] != null) {
                    if (grid[x][y] instanceof model.entity.Residence) {
                        symbol = "[H]"; // H for House
                    } else if (grid[x][y] instanceof model.entity.PowerPlant) {
                        String type = grid[x][y].getClass().getSimpleName();
                        symbol = "[" + type.charAt(0) + "]"; // S for Solar, C for Coal...
                    }
                }
                System.out.print(symbol);
            }
            System.out.println();
        }
        System.out.println("================\n");
    }
}
