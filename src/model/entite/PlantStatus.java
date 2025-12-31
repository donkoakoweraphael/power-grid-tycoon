package model.entite;

/**
 * Enum representing the operational status of a power plant.
 */
public enum PlantStatus {
    /**
     * The plant is currently under construction.
     */
    UNDER_CONSTRUCTION,
    
    /**
     * The plant is currently being upgraded.
     */
    UPGRADING,
    
    /**
     * The plant is active and operational.
     */
    ACTIVE,
    
    /**
     * The plant is inactive (manually stopped or due to failure).
     */
    INACTIVE
}
