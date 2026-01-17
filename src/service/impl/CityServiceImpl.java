package service.impl;

import model.entity.City;
import model.entity.PowerPlant;
import model.entity.Building;
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
    public void simulateHour(City city) {
        // Renamed concept: this simulates one HOUR now.

        // 1. Advance Time
        int hour = city.getCurrentHour() + 1;
        if (hour >= 24) {
            hour = 0;
            city.setCurrentDay(city.getCurrentDay() + 1);

            // Daily Tasks (Construction, Growth, etc.)
            performDailyTasks(city);
        }
        city.setCurrentHour(hour);

        // 2. Random Events (Hourly)
        handleRandomEvents(city);

        // 3. Orchestration (Hourly)
        calculateGlobalMetrics(city);
        manageEnergy(city);
        processFinances(city);
        updateGlobalHappiness(city);
    }

    private void performDailyTasks(City city) {
        // Update buildings progress (construction/upgrade)
        city.getPowerPlants().forEach(powerPlantService::updateProgress);

        // Fluctuations (New day base values)
        city.getResidences().forEach(residenceService::regenerateDailyValues);

        // Daily Population Update (Growth/Decay)
        city.getResidences().forEach(r -> residenceService.updateOccupancy(r, city.getGlobalHappiness()));

        // Urban expansion every day
        manageUrbanExpansion(city);
    }

    @Override
    public void calculateGlobalMetrics(City city) {
        int population = city.getResidences().stream()
                .mapToInt(model.entity.Residence::getCurrentOccupancy)
                .sum();

        double demand = city.getResidences().stream()
                .mapToDouble(r -> {
                    // Hourly demand calculation
                    return r.getByHourDemand(city.getCurrentHour()) * r.getCurrentOccupancy();
                }).sum();

        double capacity = city.getPowerPlants().stream()
                .mapToDouble(PowerPlant::getStorageCapacity).sum();

        double purchasingPower = city.getResidences().stream()
                .mapToDouble(model.entity.Residence::getPurchasingPower).average().orElse(0.0);

        // Calculate current pollution from all power plants
        double pollution = city.getPowerPlants().stream()
                .mapToDouble(p -> powerPlantService.calculatePollution(p) / 24.0).sum();

        city.setTotalPopulation(population);
        city.setTotalEnergyDemand(demand);
        city.setTotalStorageCapacity(capacity);
        city.setAvgPurchasingPower(purchasingPower);
        city.setTotalPollution(pollution);
    }

    @Override
    public void manageEnergy(City city) {
        // Production per Hour
        double production = city.getPowerPlants().stream()
                .mapToDouble(p -> powerPlantService.calculateProduction(p, city.getCurrentHour())).sum();

        city.setTotalEnergyProduced(production);

        double demand = city.getTotalEnergyDemand(); // Already hourly
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
        // Revenue per Hour
        // Consumption * Price
        double revenue = city.getResidences().stream()
                .mapToDouble(r -> {
                    if (!r.isSupplied())
                        return 0.0;
                    double hourlyDemand = r.getByHourDemand(city.getCurrentHour()) * r.getCurrentOccupancy();
                    double realPrice = Math.min(city.getElectricityPrice(), r.getPurchasingPower());
                    return hourlyDemand * realPrice;
                })
                .sum();

        // Costs per Hour (Daily Cost / 24)
        double costs = city.getPowerPlants().stream()
                .mapToDouble(p -> p.getDailyCost() / 24.0)
                .sum();

        double profit = revenue - costs;
        city.setTotalCoins(city.getTotalCoins() + profit);

        // Log profit (aggregating hourly profit to daily history might be noisy?)
        // Wait, processFinances is called HOURLY.
        // profitHistory generally tracks DAILY or trend?
        // If I add hourly profit, the graph will have 24 points per day. That's fine
        // for "Profit History".
        // Or I can just track "Income Rate" vs "Expense Rate"?
        // User asked for "Profit History".
        // Let's store hourly profit. It might fluctuate.
        // Actually, if I store hourly, 30 points is 30 hours = 1.25 days.
        // City.profitHistory limitation (30 items) means it will show last 30 HOURS.
        // Maybe I should only record it once per day?
        // processFinances is called in NEXT DAY? No, simulateDay (hourly).

        // Refinement: The user sees "Profit Graph".
        // If the graph shows last 30 entries, and we add every hour -> last 1.5 days.
        // If we want 30 DAYS history, we need to aggregate or record only at end of
        // day.
        // But `GameFrame` chart updates often?
        // Let's modify City.addProfit to NOT limit strictly to 30 if we want detailed
        // history, or limit to larger number (e.g. 720 for 30 days).
        // Or, only add to history if it's hour 0?
        // But then the graph is flat for 24 clicks.
        // The happiness graph updates every hour?
        // Let's check `ChartPanel` logic. It likely polls `model`.
        // If I just add hourly profit, is it useful? Maybe.
        // Let's stick to adding hourly for now, but maybe increase list size in City.

        city.addProfit(profit);
    }

    @Override
    public void updateGlobalHappiness(City city) {
        /**
         * HAPPINESS CALCULATION FORMULA:
         * 
         * 1. BASE = 100 points
         * 
         * 2. ENERGY SUPPLY (main factor):
         * - Ratio = Production / Demand
         * - 100%+ → 100 points (perfect)
         * - 90-100% → 90-100 points (good)
         * - 75-90% → 70-90 points (acceptable)
         * - 50-75% → 40-70 points (poor)
         * - < 50% → 0-40 points (critical)
         * 
         * 3. POLLUTION PENALTY:
         * - Penalty = TotalPollution / 1000
         * - Example: 500 pollution → -0.5 points
         * 1000 pollution → -1 point
         * 
         * 4. ELECTRICITY PRICE PENALTY:
         * - If price > 15 coins/MWh:
         * Penalty = (Price - 15) × 2
         * - Example: Price 20 → -10 points
         * Price 25 → -20 points
         * 
         * FINAL = max(0, min(100, EnergyScore - PollutionPenalty - PricePenalty))
         */

        double demand = city.getTotalEnergyDemand();
        double available = city.getTotalEnergyAvailable();

        double happiness = 100.0;

        // 1. Calculate base happiness from energy supply
        if (demand > 0) {
            double supplyRatio = available / demand;

            if (supplyRatio >= 1.0) {
                happiness = 100.0;
            } else if (supplyRatio >= 0.9) {
                happiness = 90.0 + (supplyRatio - 0.9) * 100;
            } else if (supplyRatio >= 0.75) {
                happiness = 70.0 + (supplyRatio - 0.75) * 133.33;
            } else if (supplyRatio >= 0.5) {
                happiness = 40.0 + (supplyRatio - 0.5) * 120;
            } else {
                happiness = supplyRatio * 80;
            }
        }

        // 2. Apply pollution penalty
        double pollutionPenalty = city.getTotalPollution() / 1000.0;
        happiness -= pollutionPenalty;

        // 3. Apply electricity price penalty (Relative to purchasing power)
        // User Logic: Penalty if price exceeds a percentage of purchasing power.
        // Let's assume affordable is < 20% of purchasing power (just a heuristic).
        // Or simpler: Price vs Avg Purchasing Power.
        // If Price > AvgPurchasingPower * 0.15 (15%), then penalty.
        // Average PP is around 100-150. 15% is 15-22. So similar range but dynamic.
        double avgPurchasingPower = city.getAvgPurchasingPower();
        double threshold = avgPurchasingPower * 0.20; // 20% of income allowed for energy

        if (city.getElectricityPrice() > threshold) {
            // Gradient penalty: how much above threshold?
            double excess = city.getElectricityPrice() - threshold;
            // Penalty multiplier
            double pricePenalty = excess * 3.0; // Steep penalty
            happiness -= pricePenalty;
        }

        // Clamp to [0, 100]
        city.setGlobalHappiness(Math.max(0, Math.min(100, happiness)));

        // Update pollution accumulation
        double dailyPollution = city.getPowerPlants().stream()
                .mapToDouble(powerPlantService::calculatePollution).sum();

        double currentTotal = city.getTotalPollution();
        double updatedPollution = (currentTotal + dailyPollution) * (1 - City.POLLUTION_DISSIPATION_RATE);

        city.setTotalPollution(Math.max(0, updatedPollution));
    }

    @Override
    public void manageUrbanExpansion(City city) {
        int maxIterations = 50; // Safety break
        int iterations = 0;

        while (city.getTotalPopulation() >= city.getTotalHousingCapacity() * 0.95 && iterations < maxIterations) {
            iterations++;
            boolean capacityChanged = false;

            // Decision: Build (0) vs Upgrade (1)
            boolean tryBuild = Math.random() < 0.5;

            if (tryBuild) {
                if (tryBuildNewResidence(city)) {
                    capacityChanged = true;
                } else {
                    // Fallback to upgrade if build failed (e.g., grid full)
                    if (tryUpgradeExistingResidence(city)) {
                        capacityChanged = true;
                    }
                }
            } else {
                if (tryUpgradeExistingResidence(city)) {
                    capacityChanged = true;
                } else {
                    // Fallback to build if upgrade failed (e.g., all max level)
                    if (tryBuildNewResidence(city)) {
                        capacityChanged = true;
                    }
                }
            }

            // If we couldn't do anything, stop trying
            if (!capacityChanged) {
                break;
            }
        }
    }

    private boolean tryBuildNewResidence(City city) {
        // Find empty spot
        for (int x = 0; x < city.getWidth(); x++) {
            for (int y = 0; y < city.getHeight(); y++) {
                if (!city.isCellOccupied(x, y)) {
                    String id = "res-" + city.getResidences().size() + "-" + city.getCurrentDay() + "-"
                            + (int) (Math.random() * 1000);
                    model.entity.Residence r = new model.entity.Residence(id);
                    r.setPosition(x, y); // Important: set position before adding/placing
                    // City.addResidence calls placeOnGrid which uses x,y.
                    // But Residence constructor doesn't take x,y.
                    // We must ensure x,y are set.
                    // Looking at City.java: placeOnGrid uses b.getX().
                    // So we must set them first.
                    city.addResidence(r);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryUpgradeExistingResidence(City city) {
        // Find candidates
        java.util.List<model.entity.Residence> upgradeable = city.getResidences().stream()
                .filter(r -> r.getLevel() < r.getMaxLevel())
                .collect(java.util.stream.Collectors.toList());

        if (upgradeable.isEmpty()) {
            return false;
        }

        // Pick one randomly
        int info = (int) (Math.random() * upgradeable.size());
        model.entity.Residence r = upgradeable.get(info);
        residenceService.upgradeLevel(r);
        return true;
    }

    private void handleRandomEvents(City city) {
        double rand = Math.random();

        // 3% chance of minor event per hour
        if (rand < 0.03) {
            triggerMinorEvent(city);
        }
        // 1% chance of major event per hour
        else if (rand < 0.04) {
            triggerMajorEvent(city);
        }
    }

    private void triggerMinorEvent(City city) {
        double eventType = Math.random();

        if (eventType < 0.5) {
            // FIRE - affects single building
            int x = (int) (Math.random() * city.getWidth());
            int y = (int) (Math.random() * city.getHeight());

            Building b = city.getGrid()[x][y];
            if (b != null) {
                b.takeDamage(30);
                city.addEvent("[FIRE] at (" + x + "," + y + ")! Building damaged (-30 health)");

                if (b.isDestroyed()) {
                    handleBuildingDestruction(city, b, x, y, "fire");
                }
            }
        } else {
            // STORM - affects 2x2 area
            int centerX = (int) (Math.random() * city.getWidth());
            int centerY = (int) (Math.random() * city.getHeight());

            int damaged = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int x = centerX + dx;
                    int y = centerY + dy;

                    if (x >= 0 && x < city.getWidth() && y >= 0 && y < city.getHeight()) {
                        Building b = city.getGrid()[x][y];
                        if (b != null) {
                            b.takeDamage(20);
                            damaged++;

                            if (b.isDestroyed()) {
                                handleBuildingDestruction(city, b, x, y, "storm");
                            }
                        }
                    }
                }
            }

            if (damaged > 0) {
                city.addEvent("[STORM] near (" + centerX + "," + centerY + ")! " + damaged + " buildings damaged");
            }
        }
    }

    private void triggerMajorEvent(City city) {
        double eventType = Math.random();

        if (eventType < 0.6) {
            // EARTHQUAKE - affects 3x3 area with heavy damage
            int centerX = (int) (Math.random() * city.getWidth());
            int centerY = (int) (Math.random() * city.getHeight());

            int damaged = 0;
            int destroyed = 0;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int x = centerX + dx;
                    int y = centerY + dy;

                    if (x >= 0 && x < city.getWidth() && y >= 0 && y < city.getHeight()) {
                        Building b = city.getGrid()[x][y];
                        if (b != null) {
                            b.takeDamage(50);
                            damaged++;

                            if (b.isDestroyed()) {
                                destroyed++;
                                handleBuildingDestruction(city, b, x, y, "earthquake");
                            }
                        }
                    }
                }
            }

            if (damaged > 0) {
                city.addEvent("[EARTHQUAKE] at (" + centerX + "," + centerY + ")! " + damaged + " damaged, " + destroyed
                        + " destroyed");
            }
        } else {
            // TORNADO - affects a line of buildings
            int startX = (int) (Math.random() * city.getWidth());
            int startY = (int) (Math.random() * city.getHeight());
            boolean horizontal = Math.random() < 0.5;

            int damaged = 0;
            int destroyed = 0;

            for (int i = 0; i < 5; i++) {
                int x = horizontal ? startX + i : startX;
                int y = horizontal ? startY : startY + i;

                if (x >= 0 && x < city.getWidth() && y >= 0 && y < city.getHeight()) {
                    Building b = city.getGrid()[x][y];
                    if (b != null) {
                        b.takeDamage(40);
                        damaged++;

                        if (b.isDestroyed()) {
                            destroyed++;
                            handleBuildingDestruction(city, b, x, y, "tornado");
                        }
                    }
                }
            }

            if (damaged > 0) {
                city.addEvent("[TORNADO] from (" + startX + "," + startY + ")! " + damaged + " damaged, " + destroyed
                        + " destroyed");
            }
        }
    }

    private void handleBuildingDestruction(City city, Building b, int x, int y, String cause) {
        // Remove from grid
        city.getGrid()[x][y] = null;

        // Remove from city lists and handle consequences
        if (b instanceof model.entity.Residence) {
            model.entity.Residence r = (model.entity.Residence) b;
            int lostPopulation = r.getCurrentOccupancy();

            city.getResidences().remove(r);
            city.setTotalPopulation(city.getTotalPopulation() - lostPopulation);

            System.out.println("💀 RESIDENCE DESTROYED by " + cause + " at (" + x + "," + y + ")! " + lostPopulation
                    + " people lost.");

            // Massive happiness penalty for deaths
            city.setGlobalHappiness(city.getGlobalHappiness() - 15);

        } else if (b instanceof model.entity.PowerPlant) {
            city.getPowerPlants().remove(b);
            System.out.println("💥 POWER PLANT DESTROYED by " + cause + " at (" + x + "," + y + ")!");

            // Moderate happiness penalty
            city.setGlobalHappiness(city.getGlobalHappiness() - 10);
        }
    }
}
