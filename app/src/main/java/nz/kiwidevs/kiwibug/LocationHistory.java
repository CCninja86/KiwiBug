package nz.kiwidevs.kiwibug;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by james on 12/05/2017.
 */

public class LocationHistory {

    private String tagID;
    private Map<String, LatLng> locationHistory;

    public LocationHistory(String tagID){

    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public Map<String, LatLng> getLocationHistory() {
        return locationHistory;
    }

    public void setLocationHistory(Map<String, LatLng> locationHistory) {
        this.locationHistory = locationHistory;
    }

    public Set<Map.Entry<String, LatLng>> getChronologicalLocationHistory(){
        TreeMap<String, LatLng> sorted = new TreeMap<>(locationHistory);
        Set<Map.Entry<String, LatLng>> mappings = sorted.entrySet();

        return mappings;
    }

    public LatLng getLastKnownLocation(){
        Set<Map.Entry<String, LatLng>> chronologicalHistory = getChronologicalLocationHistory();
        LatLng lastKnownLocation = null;
        int counter = 0;


        for(Map.Entry<String, LatLng> entry : chronologicalHistory){
            if(counter == chronologicalHistory.size() - 1){
                lastKnownLocation = entry.getValue();
            }

            counter++;
        }

        return lastKnownLocation;
    }
}
