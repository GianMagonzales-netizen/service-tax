package com.gianmarco.soa.location.util;

/**
 * Utility class for coordinate operations.
 * Within project limits: simulated location, no real GPS integration.
 */
public class CoordinatesUtil {

    // =====================================================
    // SIMULATED PASSENGER LOCATIONS - LIMA, PERU
    // =====================================================

    /**
     * Miraflores
     */
    public static final double MIRAFLORES_LAT = -12.121100;
    public static final double MIRAFLORES_LNG = -77.029700;

    /**
     * San Isidro
     */
    public static final double SAN_ISIDRO_LAT = -12.097200;
    public static final double SAN_ISIDRO_LNG = -77.036900;

    /**
     * Centro de Lima
     */
    public static final double DOWNTOWN_LIMA_LAT = -12.046000;
    public static final double DOWNTOWN_LIMA_LNG = -77.042000;

    /**
     * Barranco
     */
    public static final double BARRANCO_LAT = -12.148800;
    public static final double BARRANCO_LNG = -77.021500;

    /**
     * San Miguel
     */
    public static final double SAN_MIGUEL_LAT = -12.077500;
    public static final double SAN_MIGUEL_LNG = -77.091000;

    /**
     * Santiago de Surco
     */
    public static final double SURCO_LAT = -12.145500;
    public static final double SURCO_LNG = -76.991000;

    /**
     * La Molina
     */
    public static final double LA_MOLINA_LAT = -12.082500;
    public static final double LA_MOLINA_LNG = -76.928000;

    /**
     * Callao
     */
    public static final double CALLAO_LAT = -12.061000;
    public static final double CALLAO_LNG = -77.139000;

    /**
     * Los Olivos
     */
    public static final double LOS_OLIVOS_LAT = -11.963500;
    public static final double LOS_OLIVOS_LNG = -77.070500;

    /**
     * Chorrillos
     */
    public static final double CHORRILLOS_LAT = -12.178000;
    public static final double CHORRILLOS_LNG = -77.016000;

    /**
     * Ate
     */
    public static final double ATE_LAT = -12.027500;
    public static final double ATE_LNG = -76.919000;

    /**
     * Villa El Salvador
     */
    public static final double VILLA_EL_SALVADOR_LAT = -12.213000;
    public static final double VILLA_EL_SALVADOR_LNG = -76.936000;

    // =====================================================
    // SIMULATED DRIVER LOCATIONS
    // =====================================================

    /**
     * Driver near Miraflores
     */
    public static final double DRIVER_MIRAFLORES_LAT = -12.115000;
    public static final double DRIVER_MIRAFLORES_LNG = -77.032500;

    /**
     * Driver near San Isidro
     */
    public static final double DRIVER_SAN_ISIDRO_LAT = -12.092000;
    public static final double DRIVER_SAN_ISIDRO_LNG = -77.049000;

    /**
     * Driver near Downtown Lima
     */
    public static final double DRIVER_DOWNTOWN_LAT = -12.052500;
    public static final double DRIVER_DOWNTOWN_LNG = -77.028000;

    /**
     * Driver near Barranco
     */
    public static final double DRIVER_BARRANCO_LAT = -12.158000;
    public static final double DRIVER_BARRANCO_LNG = -77.011000;

    /**
     * Driver near Callao
     */
    public static final double DRIVER_CALLAO_LAT = -12.070000;
    public static final double DRIVER_CALLAO_LNG = -77.126000;

    /**
     * Driver near La Molina
     */
    public static final double DRIVER_LA_MOLINA_LAT = -12.090000;
    public static final double DRIVER_LA_MOLINA_LNG = -76.942000;

    /**
     * Driver near Los Olivos
     */
    public static final double DRIVER_LOS_OLIVOS_LAT = -11.972000;
    public static final double DRIVER_LOS_OLIVOS_LNG = -77.058000;

    /**
     * Driver near Chorrillos
     */
    public static final double DRIVER_CHORRILLOS_LAT = -12.168000;
    public static final double DRIVER_CHORRILLOS_LNG = -77.031000;

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Creates a coordinate string for logging or display.
     *
     * @param lat latitude
     * @param lng longitude
     * @return formatted string "(lat, lng)"
     */
    public static String formatCoordinates(
            double lat,
            double lng
    ) {
        return String.format(
                "(%.6f, %.6f)",
                lat,
                lng
        );
    }

    /**
     * Validates coordinate bounds.
     *
     * @param lat latitude
     * @param lng longitude
     * @return true when coordinates are valid
     */
    public static boolean isValidCoordinates(
            double lat,
            double lng
    ) {
        return lat >= -90
                && lat <= 90
                && lng >= -180
                && lng <= 180;
    }

    /**
     * Validates nullable coordinate values.
     *
     * @param lat latitude
     * @param lng longitude
     * @return true when both values exist and are valid
     */
    public static boolean isValidCoordinates(
            Double lat,
            Double lng
    ) {
        if (lat == null || lng == null) {
            return false;
        }

        return isValidCoordinates(
                lat.doubleValue(),
                lng.doubleValue()
        );
    }

    /**
     * Checks whether coordinates are inside the simulated
     * Lima metropolitan area.
     *
     * @param lat latitude
     * @param lng longitude
     * @return true when coordinates are within Lima
     */
    public static boolean isWithinLimaArea(
            double lat,
            double lng
    ) {
        return lat >= -12.30
                && lat <= -11.80
                && lng >= -77.25
                && lng <= -76.80;
    }

    /**
     * Returns a random simulated pickup location.
     *
     * @return array [latitude, longitude]
     */
    public static double[] getRandomPickupLocation() {
        double[][] locations = {
                {
                        MIRAFLORES_LAT,
                        MIRAFLORES_LNG
                },
                {
                        SAN_ISIDRO_LAT,
                        SAN_ISIDRO_LNG
                },
                {
                        DOWNTOWN_LIMA_LAT,
                        DOWNTOWN_LIMA_LNG
                },
                {
                        BARRANCO_LAT,
                        BARRANCO_LNG
                },
                {
                        SAN_MIGUEL_LAT,
                        SAN_MIGUEL_LNG
                },
                {
                        LOS_OLIVOS_LAT,
                        LOS_OLIVOS_LNG
                },
                {
                        CHORRILLOS_LAT,
                        CHORRILLOS_LNG
                },
                {
                        ATE_LAT,
                        ATE_LNG
                }
        };

        int randomIndex =
                (int) (
                        Math.random()
                                * locations.length
                );

        return locations[randomIndex];
    }

    /**
     * Returns a random simulated destination location.
     *
     * @return array [latitude, longitude]
     */
    public static double[] getRandomDestinationLocation() {
        double[][] locations = {
                {
                        MIRAFLORES_LAT,
                        MIRAFLORES_LNG
                },
                {
                        SAN_ISIDRO_LAT,
                        SAN_ISIDRO_LNG
                },
                {
                        DOWNTOWN_LIMA_LAT,
                        DOWNTOWN_LIMA_LNG
                },
                {
                        BARRANCO_LAT,
                        BARRANCO_LNG
                },
                {
                        SURCO_LAT,
                        SURCO_LNG
                },
                {
                        LA_MOLINA_LAT,
                        LA_MOLINA_LNG
                },
                {
                        CALLAO_LAT,
                        CALLAO_LNG
                },
                {
                        VILLA_EL_SALVADOR_LAT,
                        VILLA_EL_SALVADOR_LNG
                }
        };

        int randomIndex =
                (int) (
                        Math.random()
                                * locations.length
                );

        return locations[randomIndex];
    }

    /**
     * Returns a random simulated driver location.
     *
     * @return array [latitude, longitude]
     */
    public static double[] getRandomDriverLocation() {
        double[][] locations = {
                {
                        DRIVER_MIRAFLORES_LAT,
                        DRIVER_MIRAFLORES_LNG
                },
                {
                        DRIVER_SAN_ISIDRO_LAT,
                        DRIVER_SAN_ISIDRO_LNG
                },
                {
                        DRIVER_DOWNTOWN_LAT,
                        DRIVER_DOWNTOWN_LNG
                },
                {
                        DRIVER_BARRANCO_LAT,
                        DRIVER_BARRANCO_LNG
                },
                {
                        DRIVER_CALLAO_LAT,
                        DRIVER_CALLAO_LNG
                },
                {
                        DRIVER_LA_MOLINA_LAT,
                        DRIVER_LA_MOLINA_LNG
                },
                {
                        DRIVER_LOS_OLIVOS_LAT,
                        DRIVER_LOS_OLIVOS_LNG
                },
                {
                        DRIVER_CHORRILLOS_LAT,
                        DRIVER_CHORRILLOS_LNG
                }
        };

        int randomIndex =
                (int) (
                        Math.random()
                                * locations.length
                );

        return locations[randomIndex];
    }
}