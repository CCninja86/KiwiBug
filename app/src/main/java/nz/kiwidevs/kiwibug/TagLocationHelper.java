package nz.kiwidevs.kiwibug;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by James on 13/05/2017.
 */

public class TagLocationHelper {

    private LocationHistoryCallback locationHistoryCallback;

    public TagLocationHelper(){

    }

    public void loadLoactionHistory(Activity context, String tagID){
        Ion.with(context)
                .load("netweb.bplaced.net/kiwibug/api.php?action=getLocationHistory&id=" + tagID)
                .as(new TypeToken<LocationHistory>(){})
                .setCallback(new FutureCallback<LocationHistory>() {
                    @Override
                    public void onCompleted(Exception e, LocationHistory locationHistory) {
                        locationHistoryCallback.locationHistoryDownloadComplete(locationHistory);
                    }
                });
    }
}
