package model.entity;

/**
 * Represents a residential building in the city.
 * Consumes energy and houses the population.
 * Acts as a data container for residence state.
 */
public class Residence extends Building {

    // ========== Class Variables (Static) - Constants ==========

    // Level 1 Defaults
    public static final int BASE_MAX_CAPACITY = 20;
    public static final double BASE_ENERGY_DEMAND_MIN = 0.200; // 200 kWh/resident/day
    public static final double BASE_ENERGY_DEMAND_MAX = 0.500; // 500 kWh/resident/day
    public static final double BASE_PURCHASING_POWER_MIN = 35.0; // coins/MWh
    public static final double BASE_PURCHASING_POWER_MAX = 60.0; // coins/MWh
    public static final int DEFAULT_MAX_LEVEL = 5;
    
    // Hourly Demand Profile (0h to 23h). 1.0 = Average.
    public static final double[] HOURLY_DEMAND_CURVE = {
        0.5, 0.4, 0.4, 0.4, 0.5, 0.6, // 00-05 Night
        1.1, 1.3, 1.0, 0.9, 0.8, 0.8, // 06-11 Morning
        0.8, 0.8, 0.9, 1.0, 1.2, 1.4, // 12-17 Afternoon
        1.6, 1.7, 1.6, 1.4, 1.1, 0.8  // 18-23 Evening Peak
    };

    // Growth Rates per Level
    public static final double CAPACITY_GROWTH_RATE = 1.5; // +50% capacity/level
    public static final double DEMAND_GROWTH_RATE = 1.1; // +10% demand/level
    public static final double PURCHASING_POWER_GROWTH_RATE = 1.15; // +15% purchasing power/level

    // Population Dynamics
    public static final int GROWTH_CYCLE_DAYS = 7;
    public static final double MIN_GROWTH_RATE = 0.02; // 2%
    public static final double MAX_GROWTH_RATE = 0.10; // 10%
    public static final double MIN_DECAY_RATE = 0.05; // 5%
    public static final double MAX_DECAY_RATE = 0.15; // 15%

    // ========== Instance Variables ==========

    // id, level, maxLevel inherited from Building

    // Occupancy
    private int maxCapacity;
    private int currentOccupancy;

    // Demande énergétique PAR RÉSIDENCE
    private double energyDemand; // MWh/jour (Base Daily Demand)
    private double energyDemandMin; // Intervalle
    private double energyDemandMax;

    // Économie PAR RÉSIDENCE
    private double purchasingPower; // coins/MWh (pour toute la résidence)
    private double purchasingPowerMin;
    private double purchasingPowerMax;

    // Status
    private boolean isSupplied;

    // ========== Constructor ==========

    public Residence(String id) {
        super(id, DEFAULT_MAX_LEVEL);
        this.maxCapacity = BASE_MAX_CAPACITY;
        this.currentOccupancy = 0;
        this.isSupplied = true; // Default to true until first check
    }
    
    // Get demand specific to the hour
    public double getByHourDemand(int hour) {
        if (hour < 0 || hour > 23) return 0;
        // Daily Demand / 24 * Curve Factor
        return (energyDemand / 24.0) * HOURLY_DEMAND_CURVE[hour];
    }

    // ========== Getters ==========

    // id, level, maxLevel getters inherited from Building

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public double getEnergyDemand() {
        return energyDemand;
    }

    public double getEnergyDemandMin() {
        return energyDemandMin;
    }

    public double getEnergyDemandMax() {
        return energyDemandMax;
    }

    public double getPurchasingPower() {
        return purchasingPower;
    }

    public double getPurchasingPowerMin() {
        return purchasingPowerMin;
    }

    public double getPurchasingPowerMax() {
        return purchasingPowerMax;
    }

    public boolean isSupplied() {
        return isSupplied;
    }

    public int getAvailableCapacity() {
        return maxCapacity - currentOccupancy;
    }

    public boolean isFull() {
        return currentOccupancy >= maxCapacity;
    }

    // ========== Setters ==========

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public void setEnergyDemand(double energyDemand) {
        this.energyDemand = energyDemand;
    }

    public void setEnergyDemandMin(double energyDemandMin) {
        this.energyDemandMin = energyDemandMin;
    }

    public void setEnergyDemandMax(double energyDemandMax) {
        this.energyDemandMax = energyDemandMax;
    }

    public void setPurchasingPower(double purchasingPower) {
        this.purchasingPower = purchasingPower;
    }

    public void setPurchasingPowerMin(double purchasingPowerMin) {
        this.purchasingPowerMin = purchasingPowerMin;
    }

    public void setPurchasingPowerMax(double purchasingPowerMax) {
        this.purchasingPowerMax = purchasingPowerMax;
    }

    public void setSupplied(boolean supplied) {
        isSupplied = supplied;
    }

    // ========== Standard Methods ==========

    @Override
    public String toString() {
        return "Residence{" +
                "id='" + id + '\'' +
                ", level=" + level +
                ", currentOccupancy=" + currentOccupancy +
                ", energyDemand=" + energyDemand +
                ", isSupplied=" + isSupplied +
                '}';
    }

}
