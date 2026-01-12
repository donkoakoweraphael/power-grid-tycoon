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
        residence.setEnergyDemand(demandMin + (demandMax - demandMin) * Math.random());
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

        if (happiness > model.entity.City.HAPPINESS_THRESHOLD_GROWTH && current < max) {
            // Growth
            double rate = Residence.MIN_GROWTH_RATE
                    + (Residence.MAX_GROWTH_RATE - Residence.MIN_GROWTH_RATE) * Math.random();
            int moveIn = (int) (max * rate) + 1;
            residence.setCurrentOccupancy(Math.min(max, current + moveIn));
        } else if (happiness < model.entity.City.HAPPINESS_THRESHOLD_DECAY && current > 0) {
            // Decay
            double rate = Residence.MIN_DECAY_RATE
                    + (Residence.MAX_DECAY_RATE - Residence.MIN_DECAY_RATE) * Math.random();
            int leave = (int) (current * rate) + 1;
            residence.setCurrentOccupancy(Math.max(0, current - leave));
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

            // New capacity
            int newMax = (int) (Residence.BASE_MAX_CAPACITY
                    * Math.pow(Residence.CAPACITY_GROWTH_RATE, residence.getLevel() - 1));
            residence.setMaxCapacity(newMax);

            // Immediately regenerate values for new level
            regenerateDailyValues(residence);
        }
    }
}
