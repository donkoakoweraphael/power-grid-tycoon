package model.entity;

import java.io.Serializable;

/**
 * Abstract base class for all game entities (buildings).
 * Defines common attributes like id, level, and maxLevel.
 */
public abstract class Building implements Serializable {

    protected String id;
    protected int level;
    protected int maxLevel;

    protected int x;
    protected int y;

    /**
     * Constructor for Building.
     * 
     * @param id       Unique identifier
     * @param maxLevel Maximum level this entity can reach
     */
    protected Building(String id, int maxLevel) {
        this.id = id;
        this.level = 1;
        this.maxLevel = maxLevel;
        this.x = -1; // Default unplaced
        this.y = -1;
    }

    // ========== Getters ==========

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // ========== Setters ==========

    public void setId(String id) {
        this.id = id;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public double getHealth() {
        return health;
    }
    
    public void setHealth(double health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }
    
    public double getMaxHealth() {
        return maxHealth;
    }
    
    public void takeDamage(double damage) {
        this.health = Math.max(0, this.health - damage);
    }
    
    public boolean isDestroyed() {
        return health <= 0;
    }

    // ========== Standard Methods ==========

    @Override
    public String toString() {
        return "Building{" +
                "id='" + id + '\'' +
                ", pos=(" + x + "," + y + ")" +
                ", level=" + level +
                ", maxLevel=" + maxLevel +
                '}';
    }
}
