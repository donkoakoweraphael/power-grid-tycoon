package model.entite;

/**
 * Abstract base class for all game entities (buildings).
 * Defines common attributes like id, level, and maxLevel.
 */
public abstract class Building {

    protected String id;
    protected int level;
    protected int maxLevel;

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

    // ========== Standard Methods ==========

    @Override
    public String toString() {
        return "Building{" +
                "id='" + id + '\'' +
                ", level=" + level +
                ", maxLevel=" + maxLevel +
                '}';
    }
}
