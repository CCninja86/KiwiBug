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
    private Map<String, LatLng> locationHistoryMap;

    public LocationHistory(String tagID){

    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public Map<String, LatLng> getLocationHistory() {
        return locationHistoryMap;
    }

    public void setLocationHistory(Activity context) {
        
    }

    public Set<Map.Entry<String, LatLng>> getChronologicalLocationHistory(){
        TreeMap<String, LatLng> sorted = new TreeMap<>(locationHistoryMap);
        Set<Map.Entry<String, LatLng>> mappings = sorted.entrySet();

        return mappings;
    }
}
