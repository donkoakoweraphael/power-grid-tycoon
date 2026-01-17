package service.impl;

import model.entity.Residence;
import service.ResidenceService;

/**
 * Implementation of ResidenceService.
 */
public class ResidenceServiceImpl implements ResidenceService {

    @Override
    public void regenerateDailyValues(Residence residence) {
        // Calculate bounds based on level and store in attributes
        double demandMin = Residence.BASE_ENERGY_DEMAND_MIN
                * Math.pow(Residence.DEMAND_GROWTH_RATE, residence.getLevel() - 1);
        double demandMax = Residence.BASE_ENERGY_DEMAND_MAX
                * Math.pow(Residence.DEMAND_GROWTH_RATE, residence.getLevel() - 1);

        double powerMin = Residence.BASE_PURCHASING_POWER_MIN
                * Math.pow(Residence.PURCHASING_POWER_GROWTH_RATE, residence.getLevel() - 1);
        double powerMax = Residence.BASE_PURCHASING_POWER_MAX
                * Math.pow(Residence.PURCHASING_POWER_GROWTH_RATE, residence.getLevel() - 1);

        residence.setEnergyDemandMin(demandMin);
        residence.setEnergyDemandMax(demandMax);
        residence.setPurchasingPowerMin(powerMin);
        residence.setPurchasingPowerMax(powerMax);

        // Randomize within bounds
        double baseDemand = demandMin + (demandMax - demandMin) * Math.random();

        // Multiply by 10 for better game balance (houses need ~0.5 MW each)
        baseDemand *= 10.0;

        // Add User's "Chaos Factor" (+/- 5 variance?)
        double variance = (Math.random() * 10.0) - 5.0; // [-5, +5]

        // Ensure demand doesn't go negative
        residence.setEnergyDemand(Math.max(0.1, baseDemand + (variance * 0.001)));

        residence.setPurchasingPower(powerMin + (powerMax - powerMin) * Math.random());
    }

    @Override
    public double calculateTotalDemand(Residence residence) {
        // Demand per resident * number of residents
        return residence.getEnergyDemand() * residence.getCurrentOccupancy();
    }

    @Override
    public void updateOccupancy(Residence residence, double happiness) {
        int current = residence.getCurrentOccupancy();
        int max = residence.getMaxCapacity();

        // 1. High Happiness (> 70) -> Hyper Growth (20% - 50% of Max Capacity)
        if (happiness > model.entity.City.HAPPINESS_THRESHOLD_GROWTH && current < max) {
            double rate = 0.20 + (0.50 - 0.20) * Math.random(); // 20% to 50%
            int moveIn = (int) (max * rate);

            // Ensure at least 5 people move in for massive growth
            if (moveIn < 5)
                moveIn = 5;

            residence.setCurrentOccupancy(Math.min(max, current + moveIn));
        }
        // 2. Low Happiness (< 40) -> Hyper Decay (20% - 50% of Current Population)
        else if (happiness < model.entity.City.HAPPINESS_THRESHOLD_DECAY && current > 0) {
            double rate = 0.20 + (0.50 - 0.20) * Math.random(); // 20% to 50%
            int leave = (int) (current * rate);

            // Ensure at least 5 people leave
            if (leave < 5)
                leave = 5;

            residence.setCurrentOccupancy(Math.max(0, current - leave));
        }
        // 3. Average Happiness (40-70) -> High Fluctuation (10% - 20% of Current
        // Population)
        else {
            if (current > 0 && current < max) {
                double rate = 0.10 + (0.20 - 0.10) * Math.random(); // 10% to 20%
                int change = (int) (current * rate);

                // Ensure at least 2 person flux
                if (change < 2)
                    change = 2;

                // Random Direction (50/50)
                if (Math.random() < 0.5) {
                    change = -change; // Leave
                }

                int newVal = current + change;
                residence.setCurrentOccupancy(Math.max(0, Math.min(max, newVal)));
            }
        }
    }

    @Override
    public double calculateRevenue(Residence residence, double electricityPrice) {
        if (!residence.isSupplied()) {
            return 0.0; // No power, no pay/taxes
        }

        // Revenue = price * energy consumed
        // But capped by purchasing power
        double totalDemand = calculateTotalDemand(residence);
        double realPrice = Math.min(electricityPrice, residence.getPurchasingPower());

        return totalDemand * realPrice;
    }

    @Override
    public void upgradeLevel(Residence residence) {
        if (residence.getLevel() < residence.getMaxLevel()) {
            residence.setLevel(residence.getLevel() + 1);

            // Simple x2 multiplier per level
            int newMax = residence.getMaxCapacity() * 2;
            residence.setMaxCapacity(newMax);

            // Immediately regenerate values for new level
            regenerateDailyValues(residence);
        }
    }
}
