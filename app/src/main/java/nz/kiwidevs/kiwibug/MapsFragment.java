package nz.kiwidevs.kiwibug;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends android.support.v4.app.Fragment implements LocationListener, LocationHistoryCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GoogleMap googleMap;
    private MapView mapView;

    private Marker currentLocationMarker;

    private LocationManager locationManager;

    private Globals globals;

    private ProgressDialog progressDialog;

    private String[] allTagIDs;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        getActivity().setTitle("Locate Tags");

        globals = Globals.getInstance();

        FloatingActionButton buttonHints = (FloatingActionButton) view.findViewById(R.id.buttonHints);
        buttonHints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HintActivity.class);
                startActivity(intent);
            }
        });

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                googleMap = map;

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }


                //Lets use the approximate center of NZ and then zoom in to the users location
                LatLng nelson = new LatLng(-41.270632, 173.283965);
                //googleMap.addMarker(new MarkerOptions().position(nelson).title("Nelson"));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(nelson).zoom(5).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Launch TagDetails Fragment via callback to MainActivity (need to pass data: Marker Title), thus a Fragment should be used)
                        mListener.onMapsFragmentInteraction(marker.getTitle());
                        return true;
                    }
                });

            }
        });


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        globals.setLocationManager(locationManager);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        Location oldLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        globals.setCurrentLocation(oldLocation);


        getLatestTags();





        return view;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String tagIdentifier) {
        if (mListener != null) {
            mListener.onMapsFragmentInteraction(tagIdentifier);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(false);
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(googleMap != null){
                googleMap.setMyLocationEnabled(true);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

        getLatestTags();
    }

    @Override
    public void onLocationChanged(Location location) {
       // Toast.makeText(getActivity(), "Location Changed", Toast.LENGTH_SHORT).show();

        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        globals.setCurrentLocation(location);
    }


    public void getLatestTags(){

            Ion.with(this)
                    .load("http://netweb.bplaced.net/kiwibug/api.php?action=getUniqueIdentifiers")
                    .as(new TypeToken<String[]>(){})
                    .setCallback(new FutureCallback<String[]>() {
                        @Override
                        public void onCompleted(Exception e, String[] tagIDArray) {

                            if(progressDialog != null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                                progressDialog = null;
                            }

                            for(String currentTagID : tagIDArray){
                                addLastTagLocation(currentTagID);
                            }

                        }
                    });


    }

    /**
     * Adds a marker for the latest known location for the specified TagID on the map
     * @param TagID
     */
    public void addLastTagLocation(String TagID){
        Ion.with(this)
                .load("http://netweb.bplaced.net/kiwibug/api.php?action=getCurrentLocation&id=" + TagID)
                .as(new TypeToken<TagRecord[]>(){})
                .setCallback(new FutureCallback<TagRecord[]>() {
                    @Override
                    public void onCompleted(Exception e, TagRecord[] tagRecordArray) {

                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }

                        TagRecord currentTagLocation = tagRecordArray[0];

                        LatLng currentMarkerLatLng = new LatLng(currentTagLocation.getLatitude(),currentTagLocation.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(currentMarkerLatLng).title(currentTagLocation.getTagID()).icon(BitmapDescriptorFactory
                                .defaultMarker(273)));



                    }
                });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void locationHistoryDownloadComplete(LocationHistory locationHistory) {
        LatLng lastKnownLocation = locationHistory.getLastKnownLocation();
        googleMap.addMarker(new MarkerOptions().position(lastKnownLocation).title(locationHistory.getTagID()));
        Toast.makeText(getActivity(), "New Tag Discovered", Toast.LENGTH_SHORT).show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMapsFragmentInteraction(String tagIdentifier);
    }
}
