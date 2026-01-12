package service.impl;

import model.entity.City;
import model.entity.PowerPlant;
import service.CityService;
import service.PowerPlantService;
import service.ResidenceService;

/**
 * Implementation of CityService.
 */
public class CityServiceImpl implements CityService {

    private final PowerPlantService powerPlantService;
    private final ResidenceService residenceService;

    public CityServiceImpl() {
        this.powerPlantService = new PowerPlantServiceImpl();
        this.residenceService = new ResidenceServiceImpl();
    }

    @Override
    public void simulateDay(City city) {
        // 1. Update buildings progress (construction/upgrade)
        city.getPowerPlants().forEach(powerPlantService::updateProgress);

        // 2. Fluctuations (New day values)
        city.getResidences().forEach(residenceService::regenerateDailyValues);

        // 3. Orchestration
        calculateGlobalMetrics(city);
        manageEnergy(city);
        processFinances(city);
        updateGlobalHappiness(city);

        // Urban expansion every week
        if (city.getCurrentDay() % model.entity.Residence.GROWTH_CYCLE_DAYS == 0) {
            manageUrbanExpansion(city);
        }

        // 4. Next day
        city.setCurrentDay(city.getCurrentDay() + 1);
    }

    @Override
    public void calculateGlobalMetrics(City city) {
        int population = city.getResidences().stream()
                .mapToInt(r -> {
                    if (city.getCurrentDay() % model.entity.Residence.GROWTH_CYCLE_DAYS == 0) {
                        residenceService.updateOccupancy(r, city.getGlobalHappiness());
                    }
                    return r.getCurrentOccupancy();
                }).sum();

        double demand = city.getResidences().stream()
                .mapToDouble(residenceService::calculateTotalDemand).sum();

        double capacity = city.getPowerPlants().stream()
                .mapToDouble(PowerPlant::getStorageCapacity).sum();

        double purchasingPower = city.getResidences().stream()
                .mapToDouble(model.entity.Residence::getPurchasingPower).average().orElse(0.0);

        city.setTotalPopulation(population);
        city.setTotalEnergyDemand(demand);
        city.setTotalStorageCapacity(capacity);
        city.setAvgPurchasingPower(purchasingPower);
    }

    @Override
    public void manageEnergy(City city) {
        double production = city.getPowerPlants().stream()
                .mapToDouble(powerPlantService::calculateProduction).sum();

        double demand = city.getTotalEnergyDemand();
        double available = production;

        // Try to satisfy demand with production
        if (available >= demand) {
            // Surplus logic
            double surplus = available - demand;
            city.setTotalEnergyAvailable(demand); // All demand met
            city.getResidences().forEach(r -> r.setSupplied(true));

            // Store surplus
            double remainingSurplus = surplus;
            for (PowerPlant p : city.getPowerPlants()) {
                if (remainingSurplus <= 0)
                    break;
                remainingSurplus -= powerPlantService.storeEnergy(p, remainingSurplus);
            }
        } else {
            // Deficit logic: Try using batteries
            double deficit = demand - available;
            double fromBatteries = 0;
            for (PowerPlant p : city.getPowerPlants()) {
                fromBatteries += powerPlantService.consumeStoredEnergy(p, deficit - fromBatteries);
            }

            double totalAvailable = available + fromBatteries;
            city.setTotalEnergyAvailable(totalAvailable);

            boolean allSupplied = totalAvailable >= demand;
            city.getResidences().forEach(r -> r.setSupplied(allSupplied));
            // In a more complex version, we would supply residences one by one
        }
    }

    @Override
    public void processFinances(City city) {
        double revenue = city.getResidences().stream()
                .mapToDouble(r -> residenceService.calculateRevenue(r, city.getElectricityPrice()))
                .sum();

        double costs = city.getPowerPlants().stream()
                .mapToDouble(PowerPlant::getDailyCost)
                .sum();

        city.setTotalCoins(city.getTotalCoins() + revenue - costs);
    }

    @Override
    public void updateGlobalHappiness(City city) {
        double happiness = 100.0;

        // Penalty for pollution
        happiness -= (city.getTotalPollution() / 1000.0);

        // Penalty for high prices (arbitrary base price of 10)
        if (city.getElectricityPrice() > 15) {
            happiness -= (city.getElectricityPrice() - 15) * 2;
        }

        // Penalty if demand not met
        if (city.getTotalEnergyAvailable() < city.getTotalEnergyDemand()) {
            happiness -= 30.0;
        }

        city.setGlobalHappiness(Math.max(0, Math.min(100, happiness)));

        // Update pollution metrics for next day
        double dailyPollution = city.getPowerPlants().stream()
                .mapToDouble(powerPlantService::calculatePollution).sum();

        // Dissipation: natural cleanup every day
        double currentTotal = city.getTotalPollution();
        double updatedPollution = (currentTotal + dailyPollution) * (1 - City.POLLUTION_DISSIPATION_RATE);

        city.setTotalPollution(Math.max(0, updatedPollution));
    }

    @Override
    public void manageUrbanExpansion(City city) {
        int totalCapacity = city.getResidences().stream()
                .mapToInt(model.entity.Residence::getMaxCapacity).sum();

        // If city is nearly full and people are happy, expand
        if (city.getTotalPopulation() >= totalCapacity * City.SATURATION_THRESHOLD_DENSIFY
                && city.getGlobalHappiness() > City.HAPPINESS_THRESHOLD_GROWTH) {

            // Phase 1: Try upgrading existing residences (Densification)
            for (model.entity.Residence r : city.getResidences()) {
                if (r.getLevel() < r.getMaxLevel()) {
                    residenceService.upgradeLevel(r);
                    // Check if we gained enough capacity (just densify one by one for balance)
                }
            }

            // Re-calculate capacity after densification
            int newTotalCapacity = city.getResidences().stream()
                    .mapToInt(model.entity.Residence::getMaxCapacity).sum();

            // Phase 2: Build new residences if still tight
            if (city.getTotalPopulation() >= newTotalCapacity * City.SATURATION_THRESHOLD_EXPAND) {
                int countToBuild = 2; // Build 2 new residences per expansion phase
                for (int i = 0; i < countToBuild; i++) {
                    String id = "res-" + city.getResidences().size() + "-" + city.getCurrentDay();
                    city.addResidence(new model.entity.Residence(id));
                }
            }
        }
    }
}
