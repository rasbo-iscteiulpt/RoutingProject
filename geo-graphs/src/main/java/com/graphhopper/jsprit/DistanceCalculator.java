package com.graphhopper.jsprit;

import org.quasar.geographs.graphstream.Coordinate;

import com.graphhopper.jsprit.core.util.DistanceUnit;

public class DistanceCalculator {
	
	/** 
	 * R = earth’s radius (mean radius = 6,371km)
	 */
    private static final double R = 6371.0; // km
	
	/**
     * This is the implementation Haversine Distance Algorithm between two places
     * <p>
     * double lon1 = coord1.getX();
     * double lon2 = coord2.getX();
     * double lat1 = coord1.getY();
     * double lat2 = coord2.getY();
     *
     * @param coord1 - from coord
     * @param coord2 - to coord
     * @return great circle distance
     */
	
    public static double calculateDistance(Coordinate coord1, Coordinate coord2, DistanceUnit distanceUnit) {
        double lon1 = coord1.getLongitude();
        double lon2 = coord2.getLongitude();
        double lat1 = coord1.getLatitude();
        double lat2 = coord2.getLatitude();
        
        double delta_Lat = Math.toRadians(lat2 - lat1);
        double delta_Lon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(delta_Lat / 2) * Math.sin(delta_Lat / 2) + Math.sin(delta_Lon / 2) * Math.sin(delta_Lon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distance = R * c;
        if (distanceUnit.equals(DistanceUnit.Meter)) {
            distance = distance * 1000.;
        }
        return distance;
    }
}
