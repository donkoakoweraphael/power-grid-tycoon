package model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator class for the city simulation.
 * Manages buildings, economy, and global metrics.
 * Acts as a data container for city state.
 */
public class City implements Serializable {
    private static final long serialVersionUID = -6757152567066355887L;

    // Simulation Constants
    public static final double POLLUTION_DISSIPATION_RATE = 0.05; // 5% per day
    public static final double SATURATION_THRESHOLD_DENSIFY = 0.90; // 90% full
    public static final double SATURATION_THRESHOLD_EXPAND = 0.95; // 95% full
    public static final double HAPPINESS_THRESHOLD_GROWTH = 70.0;
    public static final double HAPPINESS_THRESHOLD_DECAY = 40.0;

    // Initial Values
    public static final double INITIAL_COINS = 5000.0;
    public static final int INITIAL_POPULATION = 100;
    public static final double DEFAULT_ELECTRICITY_PRICE = 12.0;

    public static final int INITIAL_GRID_WIDTH = 30;
    public static final int INITIAL_GRID_HEIGHT = 15;

    private String name;
    private int currentDay;
    private int currentHour; // 0-23
    private double totalCoins;
    private double electricityPrice;

    // Grid System
    private int width;
    private int height;
    private Building[][] grid;

    private double globalHappiness;
    private int totalPopulation;
    private double totalPollution;

    // Global Electricity Metrics
    private double totalEnergyProduced; // Pure hourly production
    private double totalEnergyAvailable;
    private double totalStorageCapacity;
    private double totalEnergyDemand;
    private double avgPurchasingPower;

    private List<PowerPlant> powerPlants;
    private List<Residence> residences;

    private java.util.List<String> eventLog;
    private static final int MAX_EVENT_LOG_SIZE = 5;

    private List<Double> profitHistory;

    /**
     * Default constructor for City.
     */
    public City() {
        this("New City", INITIAL_COINS);
    }

    /**
     * Constructor for City with name and coins.
     * 
     * @param name         Name of the city
     * @param initialCoins Starting budget
     */
    public City(String name, double initialCoins) {
        this.name = name;
        this.totalCoins = initialCoins;
        this.currentDay = 1;
        this.electricityPrice = DEFAULT_ELECTRICITY_PRICE;

        this.width = INITIAL_GRID_WIDTH;
        this.height = INITIAL_GRID_HEIGHT;
        this.grid = new Building[width][height];

        this.globalHappiness = 100.0;
        this.totalPopulation = 0; // Will be populated by createNewGame logic
        this.totalPollution = 0.0;

        this.powerPlants = new ArrayList<>();
        this.residences = new ArrayList<>();
        this.eventLog = new java.util.ArrayList<>();
        this.profitHistory = new ArrayList<>();
    }

    // ========== Getters ==========

    public List<Double> getProfitHistory() {
        return new ArrayList<>(profitHistory);
    }

    public String getName() {
        return name;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getCurrentHour() {
        return currentHour;
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

    public double getTotalEnergyProduced() {
        return totalEnergyProduced;
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

    public void setCurrentHour(int currentHour) {
        this.currentHour = currentHour;
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

    public void setTotalEnergyProduced(double totalEnergyProduced) {
        this.totalEnergyProduced = totalEnergyProduced;
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
        placeOnGrid(plant);
    }

    public void addResidence(Residence residence) {
        this.residences.add(residence);
        placeOnGrid(residence);
    }

    private void placeOnGrid(Building b) {
        if (b.getX() >= 0 && b.getX() < width && b.getY() >= 0 && b.getY() < height) {
            grid[b.getX()][b.getY()] = b;
        }
    }

    public boolean isCellOccupied(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return true; // Out of bounds is "occupied"
        return grid[x][y] != null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Building[][] getGrid() {
        return grid;
    }

    public void addEvent(String message) {
        String timestamp = String.format("Day %d, %02d:00", currentDay, currentHour);
        eventLog.add(0, timestamp + " - " + message);

        if (eventLog.size() > MAX_EVENT_LOG_SIZE) {
            eventLog.remove(eventLog.size() - 1);
        }
    }

    public java.util.List<String> getEventLog() {
        return new java.util.ArrayList<>(eventLog);
    }

    public void addProfit(double profit) {
        profitHistory.add(profit);
        // Keep only last 30 days
        if (profitHistory.size() > 30) {
            profitHistory.remove(0);
        }
    }

    public double getTotalPowerCapacity() {
        return powerPlants.stream()
                .mapToDouble(PowerPlant::getPowerOutput)
                .sum();
    }

    public int getTotalHousingCapacity() {
        return residences.stream()
                .mapToInt(Residence::getMaxCapacity)
                .sum();
    }
}
