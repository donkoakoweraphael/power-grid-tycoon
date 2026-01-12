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
    public void buyPowerPlant(GameModel model, String type, String id) {
        City city = model.getCity();
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
        city.addPowerPlant(plant);

        powerPlantService.prepareNextLevelStats(plant);
        model.notifyObservers();
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
    }

    @Override
    public void togglePlantStatus(GameModel model, PowerPlant plant) {
        if (plant.getStatus() == PlantStatus.UNDER_CONSTRUCTION || plant.getStatus() == PlantStatus.UPGRADING) {
            throw new BusinessRuleException(
                    "Cannot toggle status while the plant is under construction or being upgraded.");
        }

        if (plant.getStatus() == PlantStatus.ACTIVE) {
            plant.setStatus(PlantStatus.INACTIVE);
        } else {
            plant.setStatus(PlantStatus.ACTIVE);
        }
        model.notifyObservers();
    }

    @Override
    public void nextDay(GameModel model) {
        if (model.getState() == GameState.GAME_OVER) {
            throw new BusinessRuleException("Cannot advance day: Game is Over.");
        }

        // Handle Autosave BEFORE simulation to allow recovery from disaster
        saveGame(model, "autosave");

        cityService.simulateDay(model.getCity());
        model.recordDailyStats();

        // Check for Game Over
        if (model.getCity().getGlobalHappiness() <= 0) {
            model.setState(GameState.GAME_OVER);
        }

        model.notifyObservers();
    }

    @Override
    public GameModel createNewGame(String cityName) {
        City city = new City(cityName, 5000.0); // Starting budget

        // Add one basic residence to start
        city.addResidence(new Residence("res-start-1"));

        GameModel model = new GameModel(city);
        model.recordDailyStats(); // Initial stats
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
            return new SaveMetadata("", 0, 0, false);
        }

        GameModel loadedModel = persistenceService.load(fileName);
        if (loadedModel == null) {
            return new SaveMetadata("", 0, 0, false);
        }

        return new SaveMetadata(
                loadedModel.getCity().getName(),
                loadedModel.getCity().getCurrentDay(),
                loadedModel.getCity().getTotalCoins(),
                true);
    }
}
