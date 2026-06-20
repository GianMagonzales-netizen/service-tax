package com.gianmarco.soa.auth.location.util;

/**
 * Utility class for coordinate operations.
 * Within project limits: simulated location, no real GPS integration.
 */
public class CoordinatesUtil {

    // ===== SIMULATED COORDINATES FOR LIMA, PERU =====

    /**
     * Miraflores, Lima - Tourist district
     */
    public static final double MIRAFLORES_LAT = -12.1219;
    public static final double MIRAFLORES_LNG = -77.0268;

    /**
     * San Isidro, Lima - Business district
     */
    public static final double SAN_ISIDRO_LAT = -12.0981;
    public static final double SAN_ISIDRO_LNG = -77.0435;

    /**
     * Downtown Lima (Centro de Lima)
     */
    public static final double DOWNTOWN_LIMA_LAT = -12.0464;
    public static final double DOWNTOWN_LIMA_LNG = -77.0428;

    /**
     * Barranco, Lima - Bohemian district
     */
    public static final double BARRANCO_LAT = -12.1500;
    public static final double BARRANCO_LNG = -77.0200;

    /**
     * San Miguel, Lima
     */
    public static final double SAN_MIGUEL_LAT = -12.0789;
    public static final double SAN_MIGUEL_LNG = -77.0833;

    /**
     * Surco, Lima
     */
    public static final double SURCO_LAT = -12.1359;
    public static final double SURCO_LNG = -77.0071;

    /**
     * La Molina, Lima
     */
    public static final double LA_MOLINA_LAT = -12.0733;
    public static final double LA_MOLINA_LNG = -76.9378;

    /**
     * Callao (Near the airport)
     */
    public static final double CALLAO_LAT = -12.0650;
    public static final double CALLAO_LNG = -77.1377;

    // ===== SIMULATED DRIVER LOCATIONS =====

    /**
     * Driver located near Miraflores
     */
    public static final double DRIVER_MIRAFLORES_LAT = -12.1200;
    public static final double DRIVER_MIRAFLORES_LNG = -77.0250;

    /**
     * Driver located near San Isidro
     */
    public static final double DRIVER_SAN_ISIDRO_LAT = -12.1000;
    public static final double DRIVER_SAN_ISIDRO_LNG = -77.0450;

    /**
     * Driver located near Downtown Lima
     */
    public static final double DRIVER_DOWNTOWN_LAT = -12.0450;
    public static final double DRIVER_DOWNTOWN_LNG = -77.0400;

    /**
     * Driver located near Barranco
     */
    public static final double DRIVER_BARRANCO_LAT = -12.1480;
    public static final double DRIVER_BARRANCO_LNG = -77.0180;

    // ===== UTILITY METHODS =====

    /**
     * Creates a coordinate string for logging/display.
     *
     * @param lat Latitude
     * @param lng Longitude
     * @return Formatted string "(lat, lng)"
     */
    public static String formatCoordinates(double lat, double lng) {
        return String.format("(%.6f, %.6f)", lat, lng);
    }

    /**
     * Validates if coordinates are within reasonable bounds.
     *
     * @param lat Latitude
     * @param lng Longitude
     * @return true if coordinates are valid
     */
    public static boolean isValidCoordinates(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    /**
     * Validates if coordinates are not null (for Double objects).
     *
     * @param lat Latitude (can be null)
     * @param lng Longitude (can be null)
     * @return true if both are not null and valid
     */
    public static boolean isValidCoordinates(Double lat, Double lng) {
        if (lat == null || lng == null) {
            return false;
        }
        return isValidCoordinates(lat.doubleValue(), lng.doubleValue());
    }

    /**
     * Checks if a coordinate is within Lima metropolitan area (simulated).
     * This is a SIMULATED bounding box for Lima, Peru.
     *
     * @param lat Latitude
     * @param lng Longitude
     * @return true if within Lima area
     */
    public static boolean isWithinLimaArea(double lat, double lng) {
        // Simulated bounding box for Lima, Peru
        return lat >= -12.25 && lat <= -11.85 && lng >= -77.20 && lng <= -76.85;
    }

    /**
     * Gets a random pickup location for testing.
     *
     * @return Array with [latitude, longitude]
     */
    public static double[] getRandomPickupLocation() {
        double[][] locations = {
                {MIRAFLORES_LAT, MIRAFLORES_LNG},
                {SAN_ISIDRO_LAT, SAN_ISIDRO_LNG},
                {DOWNTOWN_LIMA_LAT, DOWNTOWN_LIMA_LNG},
                {BARRANCO_LAT, BARRANCO_LNG},
                {SAN_MIGUEL_LAT, SAN_MIGUEL_LNG}
        };
        int randomIndex = (int) (Math.random() * locations.length);
        return locations[randomIndex];
    }

    /**
     * Gets a random destination location for testing.
     *
     * @return Array with [latitude, longitude]
     */
    public static double[] getRandomDestinationLocation() {
        double[][] locations = {
                {MIRAFLORES_LAT, MIRAFLORES_LNG},
                {SAN_ISIDRO_LAT, SAN_ISIDRO_LNG},
                {DOWNTOWN_LIMA_LAT, DOWNTOWN_LIMA_LNG},
                {BARRANCO_LAT, BARRANCO_LNG},
                {SURCO_LAT, SURCO_LNG},
                {LA_MOLINA_LAT, LA_MOLINA_LNG}
        };
        int randomIndex = (int) (Math.random() * locations.length);
        return locations[randomIndex];
    }
}