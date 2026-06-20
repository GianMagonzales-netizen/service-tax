package com.gianmarco.soa.auth.location.service;

import org.springframework.stereotype.Service;

@Service
public class DistanceService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double EARTH_RADIUS_MILES = 3959.0;

    /**
     * Calculates distance between two points using Haversine formula.
     * Within project limits: simulated location, no real GPS integration.
     *
     * @param lat1 Latitude of first point (in degrees)
     * @param lon1 Longitude of first point (in degrees)
     * @param lat2 Latitude of second point (in degrees)
     * @param lon2 Longitude of second point (in degrees)
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates distance in miles.
     */
    public double calculateDistanceInMiles(double lat1, double lon1, double lat2, double lon2) {
        double distanceInKm = calculateDistance(lat1, lon1, lat2, lon2);
        return distanceInKm * 0.621371;
    }

    /**
     * Calculates estimated fare based on distance.
     */
    public double calculateEstimatedFare(double distanceKm, String serviceType) {
        double baseFare = 3.0;
        double ratePerKm = 2.5;

        if ("CONFORT".equalsIgnoreCase(serviceType)) {
            ratePerKm = 4.0;
        }

        return baseFare + (distanceKm * ratePerKm);
    }

    /**
     * Calculates estimated travel time in minutes.
     */
    public int calculateEstimatedTimeMinutes(double distanceKm) {
        double averageSpeedKmh = 30.0;
        double timeHours = distanceKm / averageSpeedKmh;
        return (int) Math.round(timeHours * 60);
    }

    /**
     * Checks if driver is within range.
     */
    public boolean isWithinRange(double driverLat, double driverLng,
                                 double pickupLat, double pickupLng,
                                 double maxDistanceKm) {
        double distance = calculateDistance(driverLat, driverLng, pickupLat, pickupLng);
        return distance <= maxDistanceKm;
    }
}