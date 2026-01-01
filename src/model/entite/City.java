package model.entite;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator class for the city simulation.
 * Manages buildings, economy, and global metrics.
 */
public class City implements CityOperations {

    private String name;
    private int currentDay;
    private double totalCoins;
    private double electricityPrice;

    private double globalHappiness;
    private int totalPopulation;
    private double totalPollution;

    // Global Electricity Metrics
    private double totalEnergyAvailable;
    private double totalStorageCapacity;
    private double totalEnergyDemand;
    private double avgPurchasingPower;

    private List<PowerPlant> powerPlants;
    private List<Residence> residences;

    /**
     * Constructor for City.
     * 
     * @param name         Name of the city
     * @param initialCoins Starting budget
     */
    public City(String name, double initialCoins) {
        this.name = name;
        this.totalCoins = initialCoins;
        this.currentDay = 1;
        this.electricityPrice = 12.0; // Default price (coins/MWh)

        this.globalHappiness = 100.0;
        this.totalPopulation = 0;
        this.totalPollution = 0.0;

        this.powerPlants = new ArrayList<>();
        this.residences = new ArrayList<>();

        updateGlobalMetrics();
    }

    @Override
    public void advanceDay() {
        // 1. Advance cycle for all buildings (construction/upgrades progress)
        for (PowerPlant plant : powerPlants) {
            plant.advanceCycle();
        }

        // 2. Manage Energy
        distributeEnergy();

        // 3. Update Metrics
        updateGlobalMetrics();

        // 4. Handle Weekly Growth
        if (currentDay % 7 == 0) {
            handleWeeklyGrowth();
        }

        currentDay++;
    }

    @Override
    public void distributeEnergy() {
        // To be implemented: Logic for moving energy from plants to residences
    }

    @Override
    public void updateGlobalMetrics() {
        int pop = 0;
        double demand = 0;
        double pollution = 0;
        double storage = 0;
        double sumPP = 0;

        for (Residence res : residences) {
            pop += res.getCurrentOccupancy();
            demand += res.getEnergyDemand();
            sumPP += res.getPurchasingPower();
        }

        for (PowerPlant plant : powerPlants) {
            pollution += plant.calculatePollution();
            storage += plant.getStorageCapacity();
        }

        this.totalPopulation = pop;
        this.totalEnergyDemand = demand;
        this.totalPollution = pollution;
        this.totalStorageCapacity = storage;
        this.avgPurchasingPower = residences.isEmpty() ? 0 : sumPP / residences.size();

        // Happiness logic to be refined
    }

    @Override
    public void handleWeeklyGrowth() {
        // To be implemented: Weekly population growth and auto-upgrade/build logic
    }

    // ========== Getters & Setters ==========

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCurrentDay() {
        return currentDay;
    }

    @Override
    public double getTotalCoins() {
        return totalCoins;
    }

    @Override
    public void setTotalCoins(double coins) {
        this.totalCoins = coins;
    }

    @Override
    public double getElectricityPrice() {
        return electricityPrice;
    }

    @Override
    public void setElectricityPrice(double price) {
        this.electricityPrice = price;
    }

    @Override
    public double getGlobalHappiness() {
        return globalHappiness;
    }

    @Override
    public int getTotalPopulation() {
        return totalPopulation;
    }

    @Override
    public double getTotalPollution() {
        return totalPollution;
    }

    @Override
    public double getTotalEnergyAvailable() {
        return totalEnergyAvailable;
    }

    @Override
    public double getTotalStorageCapacity() {
        return totalStorageCapacity;
    }

    @Override
    public double getTotalEnergyDemand() {
        return totalEnergyDemand;
    }

    @Override
    public double getAvgPurchasingPower() {
        return avgPurchasingPower;
    }

    @Override
    public List<PowerPlant> getPowerPlants() {
        return powerPlants;
    }

    @Override
    public List<Residence> getResidences() {
        return residences;
    }

    @Override
    public void addPowerPlant(PowerPlant plant) {
        this.powerPlants.add(plant);
    }

    @Override
    public void addResidence(Residence residence) {
        this.residences.add(residence);
    }
}
