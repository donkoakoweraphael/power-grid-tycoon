package service.impl;

import model.enums.PlantStatus;
import model.entity.PowerPlant;
import service.PowerPlantService;

/**
 * Implementation of PowerPlantService.
 */
public class PowerPlantServiceImpl implements PowerPlantService {

    @Override
    public double calculateProduction(PowerPlant plant, int hour) {
        // Only ACTIVE and UPGRADING plants produce power.
        // UNDER_CONSTRUCTION, PAUSED, BROKEN, INACTIVE produce 0.
        if (plant.getStatus() != PlantStatus.ACTIVE && plant.getStatus() != PlantStatus.UPGRADING) {
            return 0.0;
        }

        double baseOutput = plant.getPowerOutput();
        double hourlyBase = baseOutput / 24.0;

        if (plant instanceof model.entity.plant.SolarPlant) {
            // Solaire : Courbe en cloche de 6h a 18h
            // Peak a 12h (midi)
            if (hour < 6 || hour > 18) {
                return 0.0;
            }
            // Simple sine curve for daytime
            double t = (hour - 6) / 12.0; // [0, 1]
            double factor = Math.sin(Math.PI * t); // 0 at 6h, 1 at 12h, 0 at 18h
            return baseOutput / 8.0 * factor; // Higher instantaneous peak to compensate for night
        }

        if (plant instanceof model.entity.plant.WindPlant) {
            // Eolien : Facteur aléatoire (variation douce entre 0.4 et 1.6)
            double randomFactor = 0.4 + (Math.random() * 1.2);
            return hourlyBase * randomFactor;
        }

        // Autres (Charbon, Gaz, Hydro, Nucleaire) : Stable 24/7
        return hourlyBase;
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
            // Formula: ConstructionCost * (Multiplier ^ Level)
            // As requested by user: Upgrade price is deduced from base construction price
            double cost = plant.getConstructionCost() * Math.pow(plant.getUpgradeCostMultiplier(), plant.getLevel());
            int time = plant.getUpgradeTimeBase() + (plant.getLevel() - 1);

            plant.setUpgradeCost(cost);
            plant.setUpgradeTime(time);
        }
    }

    @Override
    public void upgradeLevel(PowerPlant plant) {
        if (plant.getLevel() < plant.getMaxLevel()) {
            plant.setLevel(plant.getLevel() + 1);

            // Fix: Use plant-specific growth rates
            plant.setPowerOutput(plant.getPowerOutput() * plant.getPowerOutputGrowthRate());
            plant.setStorageCapacity(plant.getStorageCapacity() * plant.getStorageGrowthRate());
            plant.setDailyCost(plant.getDailyCost() * plant.getDailyCostGrowthRate());
            // Pollution usually decreases or stays same, so we multiply by reduction rate
            // (e.g. 0.98 or 1.0)
            plant.setPollutionRate(plant.getPollutionRate() * plant.getPollutionReductionRate());

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

    @Override
    public void togglePlantStatus(PowerPlant plant) {
        if (plant.getStatus() == PlantStatus.ACTIVE) {
            plant.setStatus(PlantStatus.PAUSED);
        } else if (plant.getStatus() == PlantStatus.PAUSED) {
            plant.setStatus(PlantStatus.ACTIVE);
        }
    }
}
