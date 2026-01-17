package service.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for save file information.
 */
public record SaveMetadata(String cityName, int day, double coins, boolean exists, String savedAt)
        implements Serializable {
}
