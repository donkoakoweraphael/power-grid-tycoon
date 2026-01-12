package viewmodel;

import model.GameModel;
import model.enums.GameState;
import model.enums.PlantStatus;
import model.entity.PowerPlant;
import model.entity.Residence;
import observer.GameModelObserver;
import observer.GameViewObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the game. Acts as an adapter between the Model and the View.
 * It listens to the Model changes and notifies the View listeners.
 */
public class GameViewModel implements GameModelObserver {

    /**
     * Data Transfer Object for the Shop UI.
     */
    public record PlantShopInfo(String type, String name, double cost, double production, double storage,
            String description) {
    }

    private final GameModel model;
    private final List<GameViewObserver> viewListeners = new ArrayList<>();

    public GameViewModel(GameModel model) {
        this.model = model;
        // Register this ViewModel as an observer of the Model
        this.model.addObserver(this);
    }

    // ========== View Notification ==========

    public void addViewListener(GameViewObserver listener) {
        viewListeners.add(listener);
    }

    private void notifyView() {
        for (GameViewObserver listener : viewListeners) {
            listener.onViewUpdated();
        }
    }

    @Override
    public void onModelUpdated(GameModel model) {
        // When the model changes, we notify our view listeners
        notifyView();
    }

    // ========== Data Accessors for the View ==========

    // --- Global Info ---
    public String getCityName() {
        return model.getCity().getName();
    }

    public String getCurrentDayText() {
        return "Day " + model.getCity().getCurrentDay();
    }

    public String getCoinsText() {
        return String.format("%.0f Coins", model.getCity().getTotalCoins());
    }

    public double getHappinessValue() {
        return model.getCity().getGlobalHappiness();
    }

    public String getHappinessText() {
        return String.format("%.1f %%", model.getCity().getGlobalHappiness());
    }

    public String getPopulationText() {
        return model.getCity().getTotalPopulation() + " Citizens";
    }

    public double getPollutionValue() {
        return model.getCity().getTotalPollution();
    }

    public String getPollutionText() {
        return String.format("%.1f Tons", model.getCity().getTotalPollution());
    }

    public String getPurchasingPowerText() {
        return String.format("%.2f Coins/MWh", model.getCity().getAvgPurchasingPower());
    }

    // --- Energy Metrics ---
    public String getProductionText() {
        return String.format("%.1f MWh", model.getCity().getTotalEnergyAvailable());
    }

    public String getDemandText() {
        return String.format("%.1f MWh", model.getCity().getTotalEnergyDemand());
    }

    public String getEnergyRatiosText() {
        return String.format("<html>Production: %.1f<br/>Storage: %.1f MWh</html>",
                model.getCity().getTotalEnergyAvailable(),
                model.getCity().getTotalStorageCapacity());
    }

    public String getTotalCapacityText() {
        double maxProduction = model.getCity().getPowerPlants().stream()
                .filter(p -> p.getStatus() == PlantStatus.ACTIVE)
                .mapToDouble(PowerPlant::getPowerOutput)
                .sum();
        return String.format("%.1f MWh/Day", maxProduction);
    }

    public double getGridSaturation() {
        if (model.getCity().getTotalEnergyAvailable() == 0)
            return 0;
        return model.getCity().getTotalEnergyDemand() / model.getCity().getTotalEnergyAvailable();
    }

    public String getStorageText() {
        return String.format("%.1f / %.1f MWh",
                calculateTotalStoredEnergy(),
                model.getCity().getTotalStorageCapacity());
    }

    private double calculateTotalStoredEnergy() {
        return model.getCity().getPowerPlants().stream()
                .mapToDouble(PowerPlant::getCurrentEnergyStored)
                .sum();
    }

    public double getStorageRatio() {
        if (model.getCity().getTotalStorageCapacity() == 0)
            return 0;
        return calculateTotalStoredEnergy() / model.getCity().getTotalStorageCapacity();
    }

    // --- Economy ---
    public double getElectricityPrice() {
        return model.getCity().getElectricityPrice();
    }

    // --- Lists ---
    public List<PowerPlant> getPowerPlants() {
        return model.getCity().getPowerPlants();
    }

    public List<Residence> getResidences() {
        return model.getCity().getResidences();
    }

    public String getResidenceInfo(Residence res) {
        return String.format("Lvl %d | Pop: %d/%d | Demand: %.2f MW",
                res.getLevel(), res.getCurrentOccupancy(), res.getMaxCapacity(), res.getEnergyDemand());
    }

    public List<String> getPlantDetails(PowerPlant plant) {
        List<String> details = new ArrayList<>();
        details.add("Type: " + plant.getClass().getSimpleName().replace("Plant", "").toUpperCase());
        details.add("Level: " + plant.getLevel() + " / " + plant.getMaxLevel());
        details.add("Status: " + plant.getStatus());
        details.add("Production: " + String.format("%.1f MW", plant.getPowerOutput()));
        details.add("Storage: " + String.format("%.1f MWh", plant.getStorageCapacity()));
        details.add("Stored: " + String.format("%.1f MWh", plant.getCurrentEnergyStored()));
        details.add("Daily Cost: " + String.format("%.0f Coins", plant.getDailyCost()));
        details.add("Pollution: " + String.format("%.1f Tons/day", plant.getPollutionRate()));
        if (plant.getLevel() < plant.getMaxLevel()) {
            details.add("Next Upgrade: " + String.format("%.0f Coins", plant.getUpgradeCost()));
        }
        return details;
    }

    // --- Stats History ---
    public List<Double> getCoinHistory() {
        return model.getCoinHistory();
    }

    public List<Double> getDemandHistory() {
        return model.getDemandHistory();
    }

    public List<Double> getHappinessHistory() {
        return model.getHappinessHistory();
    }

    public List<Double> getPollutionHistory() {
        return model.getPollutionHistory();
    }

    // --- Game Status ---
    public boolean isGameOver() {
        return model.getState() == GameState.GAME_OVER;
    }

    public GameState getGameState() {
        return model.getState();
    }

    // --- Shop & Construction ---
    public List<PlantShopInfo> getAvailablePlants() {
        List<PlantShopInfo> plants = new ArrayList<>();
        // Record format: type, full name, cost, production, storage, description
        plants.add(new PlantShopInfo("COAL", "COAL POWER PLANT", 1000, 120, 0, "High output, high pollution."));
        plants.add(new PlantShopInfo("GAS", "NATURAL GAS PLANT", 1500, 100, 0, "Stable, moderate pollution."));
        plants.add(new PlantShopInfo("SOLAR", "SOLAR POWER FARM", 2000, 80, 50, "Eco-friendly, variable output."));
        plants.add(new PlantShopInfo("WIND", "WIND ENERGY FARM", 2500, 95, 60, "Green energy, high storage."));
        plants.add(new PlantShopInfo("NUCLEAR", "ADVANCED NUCLEAR PLANT", 8000, 500, 100, "Massive clean power."));
        plants.add(new PlantShopInfo("HYDRO", "HYDROELECTRIC DAM", 5000, 300, 200, "Very stable, expensive."));
        plants.add(new PlantShopInfo("BATTERY", "LITHIUM BATTERY STATION", 3000, 0, 1000, "Massive energy storage."));
        return plants;
    }

    public String getFormattedPrice() {
        return String.format("%.2f Coins/MWh", model.getCity().getElectricityPrice());
    }
}
