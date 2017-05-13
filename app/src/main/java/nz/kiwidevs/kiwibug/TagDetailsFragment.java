package nz.kiwidevs.kiwibug;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Arrays;

import nz.kiwidevs.kiwibug.utils.ReverseGeocodingTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TagDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagDetailsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;

    private OnFragmentInteractionListener mListener;

    private String tagIdentifier;

    private ProgressDialog progressDialog;

    private GoogleMap googleMap;

    private int polylineColour = Color.BLUE;

    private ListView listViewLocationHistory;

    private TagRecordListViewAdapter adapter;
    private  ArrayList<TagRecord> tagRecordArrayList = new ArrayList<>();

    public TagDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagDetailsFragment newInstance(String param1, String param2) {
        TagDetailsFragment fragment = new TagDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_tag_details, container, false);

        getActivity().setTitle("Tag Details");

        Bundle bundle = getArguments();
        tagIdentifier = bundle.getString("Tag ID");

        MapView mapView = (MapView) view.findViewById(R.id.mapViewTagRoute);
        listViewLocationHistory = (ListView) view.findViewById(R.id.listViewTagLocationHistory);

        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                googleMap = map;


                //Lets use the approximate center of NZ and then zoom in to the users location
                LatLng nelson = new LatLng(-41.270632, 173.283965);


                CameraPosition cameraPosition = new CameraPosition.Builder().target(nelson).zoom(5).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



            }
        });


        //@TODO: Pass TagID to TagDetailsFragment and replace ID in URL
        Ion.with(this)
                .load("http://netweb.bplaced.net/kiwibug/api.php?action=getTagData&id=test")
                .as(new TypeToken<TagRecord[]>(){})
                .setCallback(new FutureCallback<TagRecord[]>() {
                    @Override
                    public void onCompleted(Exception e, TagRecord[] tagRecordArray) {
                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        PolylineOptions poly = new PolylineOptions().color(polylineColour).width(5);

                        for(TagRecord tagRecord : tagRecordArray){
                            tagRecordArrayList.add(tagRecord);

                            LatLng currentMarkerLatLng = new LatLng(tagRecord.getLatitude(),tagRecord.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(currentMarkerLatLng).title(tagRecord.getID() + " " + tagRecord.getTagID()));

                            //Log.d("TagDetails",tagRecord.getID() + " " + tagRecord.getTagTime());

                            poly.add(currentMarkerLatLng);

                            //Async gecoding task here
                            new ReverseGeocodingTask(context, tagRecord).execute(currentMarkerLatLng);



                        }

                        setAdapter();

                        //listViewLocationHistory
                       adapter.refreshAdapter(tagRecordArrayList);



                        googleMap.addPolyline(poly);


                    }
                });






        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void setAdapter(){
        adapter = new TagRecordListViewAdapter(context, R.layout.tagrecord_row, tagRecordArrayList);
        listViewLocationHistory.setAdapter(adapter);
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

        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction(Uri uri);
    }




}
