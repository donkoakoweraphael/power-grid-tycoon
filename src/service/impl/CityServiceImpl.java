package service.impl;

import model.entity.City;
import service.CityService;
import service.PowerPlantService;
import service.ResidenceService;

/**
 * Implementation of CityService.
 */
public class CityServiceImpl implements CityService {

    private final PowerPlantService powerPlantService;
    private final ResidenceService residenceService;

    public CityServiceImpl(PowerPlantService powerPlantService, ResidenceService residenceService) {
        this.powerPlantService = powerPlantService;
        this.residenceService = residenceService;
    }

    @Override
    public void simulateDay(City city) {
        // Orchestrate production, consumption, and finances
    }
}
