package service.impl;

import model.entity.City;
import model.entity.PowerPlant;
import service.CityService;
import service.GameService;
import service.PowerPlantService;
import service.ResidenceService;

/**
 * Implementation of the GameService.
 * Orchestrates actions between the controller and the technical services.
 */
public class GameServiceImpl implements GameService {

    private final CityService cityService;
    private final PowerPlantService powerPlantService;
    private final ResidenceService residenceService;

    public GameServiceImpl() {
        this.cityService = new CityServiceImpl();
        this.powerPlantService = new PowerPlantServiceImpl();
        this.residenceService = new ResidenceServiceImpl();
    }

    @Override
    public void buyPowerPlant(City city, String type, String id) {
        // Business logic for buying a plant
    }

    @Override
    public void upgradeBuilding(City city, String buildingId) {
        // Logic for checking money and starting upgrade
    }

    @Override
    public void setElectricityPrice(City city, double newPrice) {
        city.setElectricityPrice(newPrice);
    }

    @Override
    public void togglePlantStatus(PowerPlant plant) {
        // Logic for activating/deactivating
    }

    @Override
    public void nextDay(City city) {
        // Logic to trigger the simulation cycle
    }
}
