package com.istiak.blooddb.utils;

/**
 * Created by anik on 2/9/17.
 */
public class DistanceUtil {

    public String distance(double lat1, double lon1, double lat2,
                           double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        String distanceInMeters = String.valueOf(Math.sqrt(distance));

        return distanceInMeters;
    }
}
