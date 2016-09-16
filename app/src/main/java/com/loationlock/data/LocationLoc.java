package com.loationlock.data;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class LocationLoc {

    private String deviceId;
    private double latitude;
    private double longitude;
    private double altitude;
    private long timeInmillis;
    private double speed;
    private String provider;


    public LocationLoc(String deviceId, double latitude, double longitude, double altitude, long timeInmillis, String provider, double speed) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timeInmillis = timeInmillis;
        this.speed = speed;
        this.provider = provider;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(long altitude) {
        this.altitude = altitude;
    }

    public long getTimeInmillis() {
        return timeInmillis;
    }

    public void setTimeInmillis(long timeInmillis) {
        this.timeInmillis = timeInmillis;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(DataConstants.DEVICE_ID, deviceId);
        result.put(DataConstants.LOCATION_LATITUDE, latitude);
        result.put(DataConstants.LOCATION_LONGITUDE, longitude);
        result.put(DataConstants.LOCATION_ALTITUDE, altitude);
        result.put(DataConstants.LOCATION_TIME, timeInmillis);
        result.put(DataConstants.LOCATION_PROVIDER, provider);
        result.put(DataConstants.LOCATION_SPEED, speed);

        return result;
    }

}
