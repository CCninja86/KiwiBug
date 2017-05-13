package nz.kiwidevs.kiwibug;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by James on 1/05/2016.
 */
public class Globals {

    private static Globals instance;
    private Location currentLocation;
    private LocationManager locationManager;

    private Globals(){

    }

    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }

        return instance;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }


}