package service.impl;

import model.enums.PlantStatus;
import model.entity.PowerPlant;
import service.PowerPlantService;

/**
 * Implementation of PowerPlantService.
 */
public class PowerPlantServiceImpl implements PowerPlantService {

    @Override
    public double calculateProduction(PowerPlant plant) {
        if (plant.getStatus() == PlantStatus.ACTIVE) {
            return plant.getPowerOutput();
        }
        return 0.0;
    }

    @Override
    public double calculatePollution(PowerPlant plant) {
        if (plant.getStatus() == PlantStatus.ACTIVE || plant.getStatus() == PlantStatus.UPGRADING) {
            return plant.getPollutionRate();
        }
        return 0.0;
    }

    @Override
    public void updateProgress(PowerPlant plant) {
        if (plant.getStatus() == PlantStatus.UNDER_CONSTRUCTION || plant.getStatus() == PlantStatus.UPGRADING) {
            int remaining = plant.getRemainingTime() - 1;
            plant.setRemainingTime(Math.max(0, remaining));

            if (plant.getRemainingTime() == 0) {
                if (plant.getStatus() == PlantStatus.UPGRADING) {
                    upgradeLevel(plant);
                }
                plant.setStatus(PlantStatus.ACTIVE);
            }
        }
    }

    @Override
    public void prepareNextLevelStats(PowerPlant plant) {
        if (plant.getLevel() < plant.getMaxLevel()) {
            // Formula: BaseCost * (Multiplier ^ Level)
            double cost = PowerPlant.UPGRADE_COST_BASE * Math.pow(PowerPlant.UPGRADE_COST_MULTIPLIER, plant.getLevel());
            int time = PowerPlant.UPGRADE_TIME_BASE + (plant.getLevel() - 1);

            plant.setUpgradeCost(cost);
            plant.setUpgradeTime(time);
        }
    }

    @Override
    public void upgradeLevel(PowerPlant plant) {
        if (plant.getLevel() < plant.getMaxLevel()) {
            plant.setLevel(plant.getLevel() + 1);

            // Stats growth based on constants
            plant.setPowerOutput(plant.getPowerOutput() * PowerPlant.POWER_OUTPUT_GROWTH_RATE);
            plant.setStorageCapacity(plant.getStorageCapacity() * PowerPlant.STORAGE_GROWTH_RATE);
            plant.setDailyCost(plant.getDailyCost() * PowerPlant.DAILY_COST_GROWTH_RATE);
            plant.setPollutionRate(plant.getPollutionRate() * PowerPlant.POLLUTION_REDUCTION_RATE);

            // Prepare stats for the NEXT level upgrade
            prepareNextLevelStats(plant);
        }
    }

    @Override
    public double storeEnergy(PowerPlant plant, double amount) {
        double current = plant.getCurrentEnergyStored();
        double capacity = plant.getStorageCapacity();
        double canStore = capacity - current;

        double toStore = Math.min(amount, canStore);
        plant.setCurrentEnergyStored(current + toStore);

        return toStore;
    }

    @Override
    public double consumeStoredEnergy(PowerPlant plant, double demand) {
        double current = plant.getCurrentEnergyStored();
        double toConsume = Math.min(demand, current);

        plant.setCurrentEnergyStored(current - toConsume);

        return toConsume;
    }
}
