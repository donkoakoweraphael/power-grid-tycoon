package controller;

import model.GameModel;
import service.GameService;
import service.impl.GameServiceImpl;
import viewmodel.GameViewModel;
import model.entity.PowerPlant;
import javax.swing.JFrame;

/**
 * Main controller for the game.
 * Glue between the View and the Service/Model.
 */
public class GameController {
    private GameModel model;
    private GameService gameService;
    private GameViewModel viewModel;
    private JFrame currentView;

    public GameController() {
        this.gameService = new GameServiceImpl();
    }

    public void start() {
        showStartMenu();
    }

    private void showStartMenu() {
        if (currentView != null)
            currentView.dispose();
        currentView = new view.StartMenuView(this);
        currentView.setVisible(true);
    }

    public void handleNewGame(String cityName) {
        this.model = gameService.createNewGame(cityName);
        this.viewModel = new GameViewModel(model);
        launchGameView();
    }

    public void handleLoadGame(String slot) {
        GameModel loaded = gameService.loadGame(slot);
        if (loaded != null) {
            this.model = loaded;
            this.viewModel = new GameViewModel(model);
            launchGameView();
        }
    }

    public service.dto.SaveMetadata getSaveMetadata(String slot) {
        return gameService.getSaveMetadata(slot);
    }

    private void launchGameView() {
        if (currentView != null)
            currentView.dispose();
        currentView = new view.GameView(this);
        currentView.setVisible(true);
    }

    public GameViewModel getViewModel() {
        return viewModel;
    }

    // ========== User Actions ==========

    public void handleNextDay() {
        gameService.nextDay(model);
    }

    public void handleNextDays(int days) {
        gameService.nextDays(model, days);
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

    public void handleRenameCity(String newName) {
        if (newName != null && !newName.trim().isEmpty()) {
            model.getCity().setName(newName.trim());
            model.notifyObservers();
        }
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
