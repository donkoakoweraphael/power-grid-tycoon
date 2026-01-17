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

            // Simple x2 multiplier per level
            plant.setPowerOutput(plant.getPowerOutput() * 2.0);
            plant.setStorageCapacity(plant.getStorageCapacity() * 2.0);
            plant.setDailyCost(plant.getDailyCost() * 2.0);
            plant.setPollutionRate(plant.getPollutionRate() * 2.0); // Pollution also doubles

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
