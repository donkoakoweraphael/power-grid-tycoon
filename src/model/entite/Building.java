package model.entite;

/**
 * Abstract base class for all game entities.
 * Defines common attributes like id, level, and maxLevel.
 */
public abstract class Building {

    protected String id;
    protected int level;
    protected int maxLevel;

    /**
     * Constructor for GameEntity.
     * 
     * @param id       Unique identifier
     * @param maxLevel Maximum level this entity can reach
     */
    protected Building(String id, int maxLevel) {
        this.id = id;
        this.level = 1;
        this.maxLevel = maxLevel;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
