package model.enums;

/**
 * Enum representing the operational status of a power plant.
 */
public enum PlantStatus {
    PLANNING,
    UNDER_CONSTRUCTION,
    ACTIVE,
    PAUSED, // Player manually paused for maintenance or saving fuel
    UPGRADING,
    BROKEN, // Event-driven failure
    INACTIVE
}
