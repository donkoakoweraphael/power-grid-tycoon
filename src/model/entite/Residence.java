package model.entite;

/**
 * Represents a residential building in the city.
 * Consumes energy and houses the population.
 */
public class Residence extends Building implements ResidenceOperations {

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

    // id, level, maxLevel inherited from GameEntity

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

    // id, level, maxLevel getters inherited from GameEntity

    @Override
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    @Override
    public double getEnergyDemand() {
        return energyDemand;
    }

    @Override
    public double getEnergyDemandMin() {
        return energyDemandMin;
    }

    @Override
    public double getEnergyDemandMax() {
        return energyDemandMax;
    }

    @Override
    public double getPurchasingPower() {
        return purchasingPower;
    }

    @Override
    public double getPurchasingPowerMin() {
        return purchasingPowerMin;
    }

    @Override
    public double getPurchasingPowerMax() {
        return purchasingPowerMax;
    }

    @Override
    public boolean isSupplied() {
        return isSupplied;
    }

    @Override
    public int getAvailableCapacity() {
        return maxCapacity - currentOccupancy;
    }

    @Override
    public boolean isFull() {
        return currentOccupancy >= maxCapacity;
    }

    // ========== Operations Implementation ==========

    @Override
    public int assignOccupants(int count) {
        int spaceAvailable = maxCapacity - currentOccupancy;
        int toAdd = Math.min(count, spaceAvailable);

        // Handle negative count (removing occupants)
        if (count < 0) {
            toAdd = Math.max(count, -currentOccupancy);
        }

        currentOccupancy += toAdd;
        return toAdd;
    }

    @Override
    public void supplyEnergy(double amount) {
        // Threshold: generous tolerance (e.g. 95% of demand is considered supplied)
        this.isSupplied = amount >= (energyDemand * 0.95);
    }

    @Override
    public void upgrade() {
        if (level >= maxLevel) {
            throw new IllegalStateException("Residence is already at max level");
        }

        level++;

        // Update Capacity
        maxCapacity = (int) (maxCapacity * CAPACITY_GROWTH_RATE);

        // Update Values with new level factors
        regenerateRandomValues();
    }

    @Override
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
