package controller;

import model.GameModel;
import service.GameService;
import service.impl.GameServiceImpl;
import model.entity.PowerPlant;
import model.entity.PowerPlant;

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

    public GameModel getModel() {
        return model;
    }

    public void startConsole(String cityName) {
        this.model = gameService.createNewGame(cityName);
        System.out.println("Nouvelle partie demarree: " + cityName);
        printStatus();
    }

    public void printStatus() {
        if (model == null)
            return;
        System.out.println("\n=== Jour " + model.getCity().getCurrentDay() + " | HEURE: "
                + model.getCity().getCurrentHour() + ":00 ===");
        System.out.printf("Argent: %.2f | Pop: %d | Bonheur: %.2f%%%n",
                model.getCity().getTotalCoins(),
                model.getCity().getTotalPopulation(),
                model.getCity().getGlobalHappiness());

        // Use pure production from city metrics
        double actualProduction = model.getCity().getTotalEnergyProduced();

        double demand = model.getCity().getTotalEnergyDemand();
        double deficit = demand - actualProduction;

        System.out.printf("Energie: %.2f MW production | %.2f MW demande", actualProduction, demand);

        // Battery context
        double currentStorage = model.getCity().getPowerPlants().stream()
                .mapToDouble(PowerPlant::getCurrentEnergyStored).sum();
        double totalCapacity = model.getCity().getTotalStorageCapacity();

        if (deficit > 0) {
            double coveredByBattery = Math.min(deficit, currentStorage);
            double missing = deficit - coveredByBattery;

            if (missing > 0) {
                System.out.printf(" | CRITIQUE: Manque %.2f MW (Batt: %.2f)%n", missing, coveredByBattery);
            } else {
                System.out.printf(" | COUVERT: %.2f MW via Batterie%n", coveredByBattery);
            }
        } else {
            double surplus = -deficit; // deficit = demand - prod. if prod > demand, deficit is negative.
            double availableSpace = totalCapacity - currentStorage;
            double toStore = Math.min(surplus, availableSpace);
            double lost = surplus - toStore;

            System.out.printf(" | Surplus: +%.2f MW (Stocke: %.2f, Perdu: %.2f)%n", surplus, toStore, lost);
        }

        // Tableau des evenements
        if (!model.getCity().getEventLog().isEmpty()) {
            System.out.println("\nEVENEMENTS RECENTS:");
            for (String event : model.getCity().getEventLog()) {
                System.out.println("  " + event);
            }
        }
    }

    public void handleNextDay() {
        gameService.nextDay(model);
        printStatus();
    }

    public void handleBuyPlant(String type, String id, int x, int y) throws Exception {
        gameService.buyPowerPlant(model, type, id, x, y);
        System.out.println("Construction: " + type + " en (" + x + "," + y + ")");
        printMap();
    }

    public void handleBuildResidence(int x, int y) throws Exception {
        String id = "res-" + System.currentTimeMillis();
        gameService.buildResidence(model, id, x, y);
        System.out.println("Construction: Maison en (" + x + "," + y + ")");
        printMap();
    }

    public void handleUpgradeBuilding(String id) {
        try {
            gameService.upgradeBuilding(model, id);
            System.out.println("Upgraded building: " + id);
        } catch (Exception e) {
            System.out.println("Error upgrading: " + e.getMessage());
        }
    }

    public void handleTogglePlant(String id) {
        model.getCity().getPowerPlants().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .ifPresent(plant -> {
                    try {
                        gameService.togglePlantStatus(model, plant);
                        System.out.println("Toggled plant: " + id + " to " + plant.getStatus());
                    } catch (Exception e) {
                        System.out.println("Error toggling plant: " + e.getMessage());
                    }
                });
    }

    public void setElectricityPrice(GameModel model, double price) {
        gameService.setElectricityPrice(model, price);
        // Ensure observers are notified - GameServiceImpl should handle this but let's
        // be sure
        model.notifyObservers();
    }

    // Add other methods if needed for console interaction

    public void handleInfo(int x, int y) {
        if (model == null)
            return;

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
                model.entity.Residence r = (model.entity.Residence) b;
                System.out.println("Type: RESIDENCE (Lvl " + r.getLevel() + ")");
                System.out.println("Occupancy: " + r.getCurrentOccupancy() + " / " + r.getMaxCapacity() + " people");
                System.out.printf("Current Demand: %.2f MW (Hour %d)%n",
                        r.getByHourDemand(model.getCity().getCurrentHour()), model.getCity().getCurrentHour());
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
        if (model == null)
            return;

        model.entity.Building[][] grid = model.getCity().getGrid();
        System.out.println("\n=== CITY MAP ===");
        System.out.print("   ");
        for (int x = 0; x < model.getCity().getWidth(); x++)
            System.out.print(x + "  ");
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

    public void saveGame(String name) {
        gameService.saveGame(model, name);
        System.out.println("Partie sauvegardee: " + name);
    }

    public void loadGame(String name) {
        GameModel loaded = gameService.loadGame(name);
        if (loaded != null) {
            this.model = loaded;
            System.out.println("Partie chargee: " + name);
        } else {
            throw new RuntimeException("Sauvegarde introuvable: " + name);
        }
    }

    public service.dto.SaveMetadata getSaveMetadata(String slotName) {
        return gameService.getSaveMetadata(slotName);
    }
}
