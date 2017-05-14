package nz.kiwidevs.kiwibug.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nz.kiwidevs.kiwibug.ReverseGeocodeCallback;
import nz.kiwidevs.kiwibug.TagFoundActivity;
import nz.kiwidevs.kiwibug.TagRecord;
import nz.kiwidevs.kiwibug.TagRecordListViewAdapter;

/**
 * Created by Michael on 13.05.2017.
 */

public class ReverseGeocodingTask extends AsyncTask<LatLng,Void,Address> {

    Context context;
    TagRecord tag;
    ReverseGeocodeCallback reverseGeocodeCallback;

    public ReverseGeocodingTask(Context context, TagRecord tag, ReverseGeocodeCallback reverseGeocodeCallback) {
        this.context = context;
        this.tag = tag;
        this.reverseGeocodeCallback = reverseGeocodeCallback;
    }

    @Override
    protected Address doInBackground(LatLng... params) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(params[0].latitude, params[0].longitude, 1);

            return addresses.get(0);
        } catch (IOException e) {
            Log.e("ReverseGeocodingTask", "Could not get geocoding results");
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Address result) {
        Log.v("ReverseGeocoding","Address:" + result);

        if(result != null){

            tag.setAddress(result.getAddressLine(0));
            reverseGeocodeCallback.onReverseGeocodeComplete();

        }
    }
}
