package model.entite;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator class for the city simulation.
 * Manages buildings, economy, and global metrics.
 * Acts as a data container for city state.
 */
public class City {

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
    }

    // ========== Getters ==========

    public String getName() {
        return name;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public double getTotalCoins() {
        return totalCoins;
    }

    public double getElectricityPrice() {
        return electricityPrice;
    }

    public double getGlobalHappiness() {
        return globalHappiness;
    }

    public int getTotalPopulation() {
        return totalPopulation;
    }

    public double getTotalPollution() {
        return totalPollution;
    }

    public double getTotalEnergyAvailable() {
        return totalEnergyAvailable;
    }

    public double getTotalStorageCapacity() {
        return totalStorageCapacity;
    }

    public double getTotalEnergyDemand() {
        return totalEnergyDemand;
    }

    public double getAvgPurchasingPower() {
        return avgPurchasingPower;
    }

    public List<PowerPlant> getPowerPlants() {
        return powerPlants;
    }

    public List<Residence> getResidences() {
        return residences;
    }

    // ========== Setters ==========

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public void setTotalCoins(double totalCoins) {
        this.totalCoins = totalCoins;
    }

    public void setElectricityPrice(double electricityPrice) {
        this.electricityPrice = electricityPrice;
    }

    public void setGlobalHappiness(double globalHappiness) {
        this.globalHappiness = globalHappiness;
    }

    public void setTotalPopulation(int totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public void setTotalPollution(double totalPollution) {
        this.totalPollution = totalPollution;
    }

    public void setTotalEnergyAvailable(double totalEnergyAvailable) {
        this.totalEnergyAvailable = totalEnergyAvailable;
    }

    public void setTotalStorageCapacity(double totalStorageCapacity) {
        this.totalStorageCapacity = totalStorageCapacity;
    }

    public void setTotalEnergyDemand(double totalEnergyDemand) {
        this.totalEnergyDemand = totalEnergyDemand;
    }

    public void setAvgPurchasingPower(double avgPurchasingPower) {
        this.avgPurchasingPower = avgPurchasingPower;
    }

    public void setPowerPlants(List<PowerPlant> powerPlants) {
        this.powerPlants = powerPlants;
    }

    public void setResidences(List<Residence> residences) {
        this.residences = residences;
    }

    // ========== Standard Methods ==========

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", currentDay=" + currentDay +
                ", totalCoins=" + totalCoins +
                ", happiness=" + globalHappiness +
                ", population=" + totalPopulation +
                '}';
    }

    // ========== Other Methods ==========

    public void addPowerPlant(PowerPlant plant) {
        this.powerPlants.add(plant);
    }

    public void addResidence(Residence residence) {
        this.residences.add(residence);
    }
}
