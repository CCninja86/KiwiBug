package nz.kiwidevs.kiwibug;

import android.location.Location;

/**
 * Created by James on 1/05/2016.
 */
public class Globals {

    private static Globals instance;
    private Location currentLocation;

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
}