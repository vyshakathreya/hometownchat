package edu.sdsu.vyshak.hometownchat;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivityList extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latitude;
    private String longitude;
    private String name;
    private String country;
    private String state;
    private boolean action;
    private double parseLat;
    private double parseLong;
    ArrayList<String> longitudes = new ArrayList<>();
    ArrayList<String> latitudes = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> countries = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_list);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        longitude = getIntent().getStringExtra("long");
        latitude = getIntent().getStringExtra("lat");
        name = getIntent().getStringExtra("name");
        state = getIntent().getStringExtra("state");
        country = getIntent().getStringExtra("country");
        longitudes = getIntent().getStringArrayListExtra("longlist");
        latitudes = getIntent().getStringArrayListExtra("latlist");
        countries = getIntent().getStringArrayListExtra("countrylist");
        states = getIntent().getStringArrayListExtra("stateslist");
        names = getIntent().getStringArrayListExtra("namelist");
        action = getIntent().getBooleanExtra("action",action);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (action) { //for single option selected
            parseLat = Double.parseDouble(latitude);
            parseLong = Double.parseDouble(longitude);

            if(parseLat==0.0 || parseLong==0.0) {
                Geocoder locator = new Geocoder(this);
                Log.d("onMapReady","Geocoder"+state+","+country+parseLat);
                try {
                    List<Address> sanDiego =
                            locator.getFromLocationName(state +","+ country,3);
                    for (Address givenLocation: sanDiego) {
                        if (givenLocation.hasLatitude())
                            parseLat=givenLocation.getLatitude();
                        if (givenLocation.hasLongitude())
                            parseLong=givenLocation.getLongitude();
                    }
                } catch (java.io.IOException IOException){
                    Toast.makeText(this, "Please try again for better results",
                            Toast.LENGTH_LONG).show();
                } catch (Exception error) {
                    Log.e("rew", "Address lookup Error", error);
                }
            }
        }

        LatLng newpoint = new LatLng(parseLat,parseLong);
        Log.d("onMapReady", "single action" + newpoint);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newpoint));
        mMap.addMarker(new MarkerOptions().position(newpoint).title(name));


        if (!action) {
            for (int i = 0; i < names.size(); i++) {
                parseLat=Double.parseDouble(latitudes.get(i));
                parseLong=Double.parseDouble(longitudes.get(i));
                if(parseLat==0.0 || parseLong==0.0) {
                    Geocoder locator = new Geocoder(this);
                    try {
                        List<Address> sanDiego =
                                locator.getFromLocationName(states.get(i) + "," + countries.get(i), 3);
                        for (Address givenLocation : sanDiego) {
                            if (givenLocation.hasLatitude())
                                parseLat = givenLocation.getLatitude();
                            if (givenLocation.hasLongitude())
                                parseLong = givenLocation.getLongitude();
                        }
                    } catch (java.io.IOException IOException){
                        Toast.makeText(this, "Please try again for better results",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception error) {
                        Log.e("rew", "Address lookup Error", error);
                    }
                }
                if(names.get(i)!= null){
                    mMap.addMarker(new MarkerOptions().position(new LatLng(parseLat,parseLong)).title(names.get(i)));
                }
            }
        }
    }

}