package model.entite;

/**
 * Represents a residential building in the city.
 * Consumes energy and houses the population.
 * Acts as a data container for residence state.
 */
public class Residence extends Building {

    // ========== Class Variables (Static) - Constants ==========

    // Level 1 Defaults
    public static final int BASE_MAX_CAPACITY = 20;
    public static final double BASE_ENERGY_DEMAND_MIN = 0.010; // 10 kWh/day
    public static final double BASE_ENERGY_DEMAND_MAX = 0.020; // 20 kWh/day
    public static final double BASE_PURCHASING_POWER_MIN = 8.0; // coins/MWh
    public static final double BASE_PURCHASING_POWER_MAX = 12.0; // coins/MWh
    public static final int DEFAULT_MAX_LEVEL = 5;

    // Growth Rates per Level
    public static final double CAPACITY_GROWTH_RATE = 1.5; // +50% capacity/level
    public static final double DEMAND_GROWTH_RATE = 1.1; // +10% demand/level
    public static final double PURCHASING_POWER_GROWTH_RATE = 1.15; // +15% purchasing power/level

    // ========== Instance Variables ==========

    // id, level, maxLevel inherited from Building

    // Occupancy
    private int maxCapacity;
    private int currentOccupancy;

    // Demande énergétique PAR RÉSIDENCE
    private double energyDemand; // MWh/jour (pour toute la résidence)
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

        regenerateRandomValues(); // Initialize random demand/power
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

    // ========== Fluctuation Methods ==========

    /**
     * Regenerates random values for energy demand and purchasing power
     * within the bounds defined by the current level.
     */
    public void regenerateRandomValues() {
        // Calculate bounds based on level and store in attributes
        this.energyDemandMin = BASE_ENERGY_DEMAND_MIN * Math.pow(DEMAND_GROWTH_RATE, level - 1);
        this.energyDemandMax = BASE_ENERGY_DEMAND_MAX * Math.pow(DEMAND_GROWTH_RATE, level - 1);

        this.purchasingPowerMin = BASE_PURCHASING_POWER_MIN * Math.pow(PURCHASING_POWER_GROWTH_RATE, level - 1);
        this.purchasingPowerMax = BASE_PURCHASING_POWER_MAX * Math.pow(PURCHASING_POWER_GROWTH_RATE, level - 1);

        // Randomize within bounds using Math.random()
        this.energyDemand = energyDemandMin + (energyDemandMax - energyDemandMin) * Math.random();
        this.purchasingPower = purchasingPowerMin + (purchasingPowerMax - purchasingPowerMin) * Math.random();
    }
}
