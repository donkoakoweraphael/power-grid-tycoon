package viewmodel;

import model.GameModel;
import model.entity.City;
import model.entity.PowerPlant;
import model.entity.Residence;
import model.entity.plant.*;
import model.enums.PlantStatus;
import model.enums.GameState;
import observer.GameModelObserver;
import observer.GameViewObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel adaptateur entre le modèle et les vues Swing.
 * Fournit des méthodes de formatage et calculs dérivés.
 */
public class GameViewModel implements GameModelObserver {

    private final GameModel model;
    private final List<GameViewObserver> viewListeners;

    public GameViewModel(GameModel model) {
        this.model = model;
        this.viewListeners = new ArrayList<>();
        model.addObserver(this);
    }

    // ========== View Listener Registration ==========
    public void addViewListener(GameViewObserver listener) {
        viewListeners.add(listener);
    }

    public void removeViewListener(GameViewObserver listener) {
        viewListeners.remove(listener);
    }

    @Override
    public void onModelUpdated(GameModel updatedModel) {
        for (GameViewObserver v : viewListeners) {
            v.onViewUpdated();
        }
    }

    // ========== Model Access ==========
    public GameModel getModel() {
        return model;
    }

    public City getCity() {
        return model.getCity();
    }

    // ========== Formatted Texts ==========
    public String getCityName() {
        return model.getCity().getName();
    }

    public String getCurrentDayText() {
        return "Jour " + model.getCity().getCurrentDay();
    }

    public String getCurrentHourText() {
        return String.format("%02d:00", model.getCity().getCurrentHour());
    }

    public String getCoinsText() {
        return String.format("%.0f pièces", model.getCity().getTotalCoins());
    }

    public String getPopulationText() {
        return String.format("%d habitants", model.getCity().getTotalPopulation());
    }

    public String getHappinessText() {
        return String.format("%.1f%%", model.getCity().getGlobalHappiness());
    }

    public String getPollutionText() {
        return String.format("%.1f PP", model.getCity().getTotalPollution());
    }

    public String getPriceText() {
        return String.format("%.2f pièces/MWh", model.getCity().getElectricityPrice());
    }

    // ========== Energy Metrics ==========
    public double getTotalProduction() {
        return model.getCity().getTotalEnergyProduced();
    }

    public double getTotalDemand() {
        return model.getCity().getTotalEnergyDemand();
    }

    public double getTotalStoredEnergy() {
        return model.getCity().getPowerPlants().stream()
                .mapToDouble(PowerPlant::getCurrentEnergyStored)
                .sum();
    }

    public double getTotalStorageCapacity() {
        return model.getCity().getTotalStorageCapacity();
    }

    public String getEnergyStatusText() {
        double prod = getTotalProduction();
        double demand = getTotalDemand();
        double balance = prod - demand;
        String status = balance >= 0 ? "Surplus" : "Déficit";
        return String.format("Prod: %.1f MW | Dem: %.1f MW | %s: %.1f MW",
                prod, demand, status, Math.abs(balance));
    }

    public String getStorageText() {
        return String.format("%.1f / %.1f MWh", getTotalStoredEnergy(), getTotalStorageCapacity());
    }

    // ========== Entity Lists ==========
    public List<PowerPlant> getPowerPlants() {
        return model.getCity().getPowerPlants();
    }

    public List<Residence> getResidences() {
        return model.getCity().getResidences();
    }

    // ========== Plant Info ==========
    public String getPlantInfo(PowerPlant plant) {
        return String.format("%s (Lvl %d) | %.1f MW | %s",
                plant.getClass().getSimpleName().replace("Plant", ""),
                plant.getLevel(),
                plant.getPowerOutput(),
                plant.getStatus());
    }

    public String getResidenceInfo(Residence res) {
        double totalDemand = res.getEnergyDemand() * res.getCurrentOccupancy();
        return String.format("Lvl %d | Pop: %d/%d | Demande: %.2f MW",
                res.getLevel(), res.getCurrentOccupancy(), res.getMaxCapacity(), totalDemand);
    }

    // ========== Shop Data ==========
    public record PlantShopInfo(String type, String name, int cost, int production, int storage, String description) {
    }

    public List<PlantShopInfo> getAvailablePlants() {
        List<PlantShopInfo> shop = new ArrayList<>();
        List<PowerPlant> templates = List.of(
                new CoalPlant("tmp"),
                new NaturalGasPlant("tmp"),
                new SolarPlant("tmp"),
                new WindPlant("tmp"),
                new NuclearPlant("tmp"),
                new HydroPlant("tmp"),
                new BatteryStorage("tmp"));

        for (PowerPlant p : templates) {
            String type = p.getClass().getSimpleName().replace("Plant", "").replace("Storage", "").toLowerCase();
            if (p instanceof NaturalGasPlant) {
                type = "gas";
            }
            shop.add(new PlantShopInfo(
                    type,
                    p.getShopName(),
                    (int) p.getConstructionCost(),
                    (int) p.getPowerOutput(),
                    (int) p.getStorageCapacity(),
                    p.getShopDescription()));
        }
        return shop;
    }

    // ========== Game State ==========
    public boolean isGameOver() {
        return model.getState() == GameState.GAME_OVER;
    }

    public GameState getGameState() {
        return model.getState();
    }

    // ========== Stats History ==========
    public List<Double> getCoinHistory() {
        return model.getCoinHistory();
    }

    public List<Double> getHappinessHistory() {
        return model.getHappinessHistory();
    }

    public List<Double> getPollutionHistory() {
        return model.getPollutionHistory();
    }
}
