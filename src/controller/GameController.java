package controller;

import model.GameModel;
import service.GameService;
import service.impl.GameServiceImpl;
import viewmodel.GameViewModel;
import model.entity.PowerPlant;

/**
 * Main controller for the game.
 * Glue between the View and the Service/Model.
 */
public class GameController {
    private final GameModel model;
    private final GameService gameService;
    private final GameViewModel viewModel;

    public GameController(GameModel model) {
        this.model = model;
        this.gameService = new GameServiceImpl();
        this.viewModel = new GameViewModel(model);
    }

    public GameViewModel getViewModel() {
        return viewModel;
    }

    // ========== User Actions ==========

    public void handleNextDay() {
        gameService.nextDay(model);
    }

    public void handleBuyPlant(String type, String id) {
        gameService.buyPowerPlant(model, type, id);
    }

    public void handleUpgradeBuilding(String id) {
        gameService.upgradeBuilding(model, id);
    }

    public void handleSetPrice(double price) {
        gameService.setElectricityPrice(model, price);
    }

    public void handleTogglePlant(PowerPlant plant) {
        gameService.togglePlantStatus(model, plant);
    }

    public void handleSave(String slot) {
        gameService.saveGame(model, slot);
    }

    public void handleLoad(String slot) {
        GameModel loaded = gameService.loadGame(slot);
        if (loaded != null) {
            model.setCity(loaded.getCity());
            model.setState(loaded.getState());
            // Note: In a real app we might want to replace the whole model reference
            // but here we update the fields to not break existing references.
        }
    }
}
