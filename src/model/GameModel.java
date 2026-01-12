package model;

import model.entity.City;
import model.enums.GameState;
import model.observer.GameObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * Main model facade for the game.
 * Implements the Observer pattern to notify UI of changes.
 */
public class GameModel implements Serializable {
    private City city;
    private GameState state;
    private transient List<GameObserver> observers = new ArrayList<>();

    // Historical Statistics (Last 30 days)
    private final LinkedList<Double> coinHistory = new LinkedList<>();
    private final LinkedList<Double> demandHistory = new LinkedList<>();
    private final LinkedList<Double> happinessHistory = new LinkedList<>();
    private final LinkedList<Double> pollutionHistory = new LinkedList<>();

    public GameModel(City city) {
        this.city = city;
        this.state = GameState.RUNNING;
        initObserversList();
    }

    /**
     * Re-initializes the observer list after deserialization.
     */
    private void initObserversList() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
        notifyObservers();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        notifyObservers();
    }

    // ========== Observer Pattern ==========

    public void addObserver(GameObserver observer) {
        initObserversList();
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        initObserversList();
        observers.remove(observer);
    }

    public void notifyObservers() {
        initObserversList();
        for (GameObserver o : observers) {
            o.onGameStateChanged(this);
        }
    }

    // ========== History Management ==========

    public void recordDailyStats() {
        if (city == null)
            return;

        recordValue(coinHistory, city.getTotalCoins());
        recordValue(demandHistory, city.getTotalEnergyDemand());
        recordValue(happinessHistory, city.getGlobalHappiness());
        recordValue(pollutionHistory, city.getTotalPollution());
    }

    private void recordValue(LinkedList<Double> list, double value) {
        list.addLast(value);
        if (list.size() > 30) {
            list.removeFirst();
        }
    }

    public List<Double> getCoinHistory() {
        return new ArrayList<>(coinHistory);
    }

    public List<Double> getDemandHistory() {
        return new ArrayList<>(demandHistory);
    }

    public List<Double> getHappinessHistory() {
        return new ArrayList<>(happinessHistory);
    }

    public List<Double> getPollutionHistory() {
        return new ArrayList<>(pollutionHistory);
    }
}
