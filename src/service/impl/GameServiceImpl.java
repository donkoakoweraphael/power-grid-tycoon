package service.impl;

import model.enums.GameState;
import model.enums.PlantStatus;
import model.entity.*;
import model.entity.plant.*;
import service.CityService;
import service.GameService;
import service.PowerPlantService;
import service.PersistenceService;
import service.ResidenceService;
import exception.InsufficientFundsException;
import exception.BusinessRuleException;
import model.GameModel;
import service.dto.SaveMetadata;

import java.util.Optional;

/**
 * Implementation of the GameService.
 * Orchestrates actions between the controller and the technical services.
 */
public class GameServiceImpl implements GameService {

    private final CityService cityService;
    private final PowerPlantService powerPlantService;
    private final ResidenceService residenceService;
    private PersistenceService persistenceService; // Injected later or default

    public GameServiceImpl() {
        this.cityService = new CityServiceImpl();
        this.powerPlantService = new PowerPlantServiceImpl();
        this.residenceService = new ResidenceServiceImpl();
        this.persistenceService = new PersistenceServiceImpl();
    }

    @Override
    public void buyPowerPlant(GameModel model, String type, String id, int x, int y) {
        City city = model.getCity();

        // Validate Grid Position
        if (city.isCellOccupied(x, y)) {
            throw new BusinessRuleException(
                    "Construction impossible: Cellule (" + x + "," + y + ") est occupee ou hors limites.");
        }

        PowerPlant plant;
        switch (type.toLowerCase()) {
            case "coal":
                plant = new CoalPlant(id);
                break;
            case "gas":
                plant = new NaturalGasPlant(id);
                break;
            case "solar":
                plant = new SolarPlant(id);
                break;
            case "wind":
                plant = new WindPlant(id);
                break;
            case "nuclear":
                plant = new NuclearPlant(id);
                break;
            case "hydro":
                plant = new HydroPlant(id);
                break;
            case "battery":
                plant = new BatteryStorage(id);
                break;
            default:
                throw new BusinessRuleException("Unknown power plant type: " + type);
        }

        if (city.getTotalCoins() < plant.getConstructionCost()) {
            throw new InsufficientFundsException(plant.getConstructionCost(), city.getTotalCoins());
        }

        city.setTotalCoins(city.getTotalCoins() - plant.getConstructionCost());

        // Set Position
        plant.setPosition(x, y);
        city.addPowerPlant(plant); // This also updates the grid

        powerPlantService.prepareNextLevelStats(plant);

        // Log event
        city.addEvent("Construction: " + plant.getClass().getSimpleName() + " a (" + x + "," + y + ")");

        // Immediate metrics update
        cityService.calculateGlobalMetrics(city);
        cityService.manageEnergy(city);

        model.notifyObservers();
        saveGame(model, "autosave");
    }

    @Override
    public void buildResidence(GameModel model, String id, int x, int y) {
        City city = model.getCity();

        if (city.isCellOccupied(x, y)) {
            throw new BusinessRuleException("Construction impossible: Cellule (" + x + "," + y + ") est occupee.");
        }

        double cost = 1000.0; // Fixed cost for now

        if (city.getTotalCoins() < cost) {
            throw new InsufficientFundsException(cost, city.getTotalCoins());
        }

        city.setTotalCoins(city.getTotalCoins() - cost);

        Residence residence = new Residence(id);
        residence.setPosition(x, y);

        // Initialize demand BEFORE adding to city
        residenceService.regenerateDailyValues(residence);

        // Give it some initial occupancy (random between 5-15 people)
        int initialOccupancy = 5 + (int) (Math.random() * 11);
        residence.setCurrentOccupancy(initialOccupancy);

        city.addResidence(residence);

        // Log event
        city.addEvent("Construction: Maison a (" + x + "," + y + ") - " + initialOccupancy + " habitants");

        model.notifyObservers();
        saveGame(model, "autosave");
    }

    @Override
    public void upgradeBuilding(GameModel model, String buildingId) {
        City city = model.getCity();
        // Search in PowerPlants
        Optional<PowerPlant> plantOpt = city.getPowerPlants().stream()
                .filter(p -> p.getId().equals(buildingId)).findFirst();

        if (plantOpt.isPresent()) {
            PowerPlant plant = plantOpt.get();
            if (plant.getLevel() >= plant.getMaxLevel()) {
                throw new BusinessRuleException("Power plant already at maximum level.");
            }
            if (city.getTotalCoins() < plant.getUpgradeCost()) {
                throw new InsufficientFundsException(plant.getUpgradeCost(), city.getTotalCoins());
            }

            city.setTotalCoins(city.getTotalCoins() - plant.getUpgradeCost());
            powerPlantService.upgradeLevel(plant);
            model.notifyObservers();
            saveGame(model, "autosave");
            return;
        }

        // Search in Residences
        Optional<Residence> resOpt = city.getResidences().stream()
                .filter(r -> r.getId().equals(buildingId)).findFirst();

        if (resOpt.isPresent()) {
            Residence res = resOpt.get();
            if (res.getLevel() >= res.getMaxLevel()) {
                throw new BusinessRuleException("Residence already at maximum level.");
            }
            residenceService.upgradeLevel(res);
            model.notifyObservers();
            saveGame(model, "autosave");
            return;
        }

        throw new BusinessRuleException("Building not found: " + buildingId);
    }

    @Override
    public void setElectricityPrice(GameModel model, double newPrice) {
        if (newPrice < 0)
            throw new BusinessRuleException("Price cannot be negative.");
        model.getCity().setElectricityPrice(newPrice);
        model.notifyObservers();
        saveGame(model, "autosave");
    }

    @Override
    public void togglePlantStatus(GameModel model, PowerPlant plant) {
        // Validation logic is now partly in service, but we can keep the business rule
        // check here or move it.
        // Let's keep the check for UI feedback consistency, but delegate the state
        // change.
        if (plant.getStatus() == PlantStatus.UNDER_CONSTRUCTION || plant.getStatus() == PlantStatus.UPGRADING) {
            throw new BusinessRuleException(
                    "Cannot toggle status while the plant is under construction or being upgraded.");
        }

        powerPlantService.togglePlantStatus(plant);

        // Immediate metrics update
        cityService.calculateGlobalMetrics(model.getCity());
        cityService.manageEnergy(model.getCity());

        model.notifyObservers();
        saveGame(model, "autosave");
    }

    @Override
    public void nextDay(GameModel model) {
        // Rolling Day Save (before simulation, so player can return to start of day)
        saveGame(model, "day_save");

        cityService.simulateHour(model.getCity());
        model.recordDailyStats();

        // Check game over conditions
        City city = model.getCity();
        boolean gameOver = false;
        String reason = "";

        // Happiness below 5% (was 20%, too strict)
        if (city.getGlobalHappiness() < 5.0) {
            gameOver = true;
            reason = "GAME OVER: Bonheur trop faible (< 5%) - Emeutes!";
        }

        // Money below -10
        if (city.getTotalCoins() < -10.0) {
            gameOver = true;
            reason = "GAME OVER: Faillite (argent < -10)!";
        }

        if (gameOver) {
            model.setState(GameState.GAME_OVER);
            System.out.println(reason);
            // Stop the timer in UI if running
        }

        model.notifyObservers();
        saveGame(model, "autosave");
    }

    @Override
    public void nextDays(GameModel model, int days) {
        for (int i = 0; i < days; i++) {
            if (model.getState() == GameState.GAME_OVER)
                break;
            nextDay(model);
        }
    }

    @Override
    public GameModel createNewGame(String cityName) {
        City city = new City();
        city.setName(cityName);

        // Starting infrastructure
        SolarPlant solar = new SolarPlant("solar-start-1");
        solar.setStatus(PlantStatus.ACTIVE);
        solar.setPosition(0, 0); // Top-Left
        city.addPowerPlant(solar);

        // Prepare stats for next level (upgrade cost, etc.)
        powerPlantService.prepareNextLevelStats(solar);

        // Initial Population centered around 100
        int targetPop = City.INITIAL_POPULATION;
        int residentsPerHouse = Residence.BASE_MAX_CAPACITY;
        int housesNeeded = (int) Math.ceil((double) targetPop / residentsPerHouse);

        int gridCursorX = 1;
        int gridCursorY = 0;

        for (int i = 1; i <= housesNeeded; i++) {
            Residence res = new Residence("res-start-" + i);
            // Distribute population
            int popInThisHouse = Math.min(residentsPerHouse, targetPop);
            res.setCurrentOccupancy(popInThisHouse);

            // Place on grid
            res.setPosition(gridCursorX, gridCursorY);
            city.addResidence(res);

            // Move cursor
            gridCursorX++;
            if (gridCursorX >= city.getWidth()) {
                gridCursorX = 0;
                gridCursorY++;
            }

            targetPop -= popInThisHouse;

            // Initialize demand and purchasing power
            residenceService.regenerateDailyValues(res);
        }

        // Initialize metrics so they are not zero on start
        city.setCurrentHour(8); // Start at 8 AM for Solar production
        cityService.calculateGlobalMetrics(city);
        cityService.manageEnergy(city);

        // Force the starting storage to exactly 50%
        solar.setCurrentEnergyStored(solar.getStorageCapacity() / 2.0);

        GameModel model = new GameModel(city);
        model.recordDailyStats(); // Initial stats

        // Immediate Autosave
        saveGame(model, "autosave");

        return model;
    }

    @Override
    public void saveGame(GameModel model, String fileName) {
        if (persistenceService != null) {
            persistenceService.save(model, fileName);
        }
    }

    @Override
    public GameModel loadGame(String fileName) {
        if (persistenceService != null) {
            return persistenceService.load(fileName);
        }
        return null;
    }

    @Override
    public SaveMetadata getSaveMetadata(String fileName) {
        if (persistenceService == null || !persistenceService.exists(fileName)) {
            return new SaveMetadata("", 0, 0, false, "");
        }

        GameModel loadedModel = persistenceService.load(fileName);
        if (loadedModel == null) {
            return new SaveMetadata("", 0, 0, false, "");
        }

        // Get file modification date
        java.io.File file = new java.io.File("saves/" + fileName + ".tycoon");
        String savedAt = "";
        if (file.exists()) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            savedAt = sdf.format(new java.util.Date(file.lastModified()));
        }

        return new SaveMetadata(
                loadedModel.getCity().getName(),
                loadedModel.getCity().getCurrentDay(),
                loadedModel.getCity().getTotalCoins(),
                true,
                savedAt);
    }
}
