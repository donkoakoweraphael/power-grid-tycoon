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

        // Population Growth (Once per day)
        city.getResidences().forEach(r -> residenceService.updateOccupancy(r, city.getGlobalHappiness()));

        // Urban expansion every day (User request: was 7 days)
        // No need for modulo check if it's every day, or strictly check == 1 day if we
        // want it every day?
        // Since performDailyTasks runs once per day, we just call manageUrbanExpansion
        // directly.
        manageUrbanExpansion(city);
    }

    @Override
    public void calculateGlobalMetrics(City city) {
        int population = city.getResidences().stream()
                .mapToInt(model.entity.Residence::getCurrentOccupancy).sum();

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
                    java.awt.Point freeSpot = findFreeGridPosition(city);

                    if (freeSpot != null) {
                        String id = "res-auto-" + city.getCurrentDay() + "-" + System.currentTimeMillis() + "-" + i;
                        model.entity.Residence res = new model.entity.Residence(id);
                        res.setPosition(freeSpot.x, freeSpot.y);

                        // Initial setup for the new residence
                        residenceService.regenerateDailyValues(res);
                        res.setCurrentOccupancy(5); // Initial families moving in

                        city.addResidence(res);
                        city.addEvent("Expansion: New Residence auto-built at (" + freeSpot.x + "," + freeSpot.y + ")");
                    } else {
                        city.addEvent("Expansion Stalled: No free space on grid!");
                        break;
                    }
                }
            }
        }
    }

    private java.awt.Point findFreeGridPosition(City city) {
        // Spiral search or simple linear search for empty spot
        // Let's look for a spot adjacent to existing buildings for organic growth
        // Simple heuristic: Line by line (or random?)
        // Better: Scan grid, find clear spot.

        int w = city.getWidth();
        int h = city.getHeight();

        // Strategy: Fill from Top-Left to Bottom-Right
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (!city.isCellOccupied(x, y)) {
                    return new java.awt.Point(x, y);
                }
            }
        }
        return null;
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
