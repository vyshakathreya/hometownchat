package edu.sdsu.vyshak.hometownchat;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivityList extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap myMap;
    private String name;
    private String TAG="MapsActivityList";
    private String state;
    private String queryMaps="SELECT nickname,state,country,year,latitude,longitude FROM users";
    private String queryMapsRecieved="SELECT nickname,state,country,year,latitude,longitude FROM users";
    private String nickname;
    private String city;
    private String country;
    private String url;
    private String category="all";
    private double latitude;
    private double longitude;
    private int userId;
    private int year;
    private int nextId=0;
    private int dbrCount;
    Geocoder locator;
    DBHelper mydb;

    ArrayList<String> longitudes = new ArrayList<>();
    ArrayList<String> latitudes = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> countries = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_list);
        mydb = new DBHelper(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        queryMapsRecieved = getIntent().getStringExtra("query");
        category = getIntent().getStringExtra("cat");
        Log.d(TAG,"recieved "+queryMapsRecieved + category);
        Button loadMore = (Button) findViewById(R.id.loadMore);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                nextId=+100;
                userListDesc(nextId);
            }
        });
    }

    private void userListDesc(int start) {
        names.clear();
        longitudes.clear();
        latitudes.clear();
        queryMaps=queryMapsRecieved;
        if(category=="country"){
            queryMaps = queryMaps +" ORDER BY id DESC LIMIT "+start+",100"; //"SELECT nickname,state,country,year,latitude,longitude FROM users WHERE country LIKE "+"\'"+country+"\'"
        }
        if(category=="countryState"){
            queryMaps = queryMaps+" ORDER BY id DESC LIMIT "+start+",100";
        }
        if(category=="countryYear") {
            queryMaps = queryMaps+" ORDER BY id DESC LIMIT "+start+",100";
        }
        if(category=="year"){
            queryMaps = queryMaps+" ORDER BY id DESC LIMIT "+start+",100";
        }
        if(category=="countryStateYear"){
            queryMaps =queryMaps+" ORDER BY id DESC LIMIT "+start+",100";
        }
        if(category=="all"){
            queryMaps = queryMaps+" ORDER BY id DESC LIMIT "+start+",100";
        }
        usersList.addAll(mydb.getUsers(queryMaps));
        Log.d(TAG, "userlist: "); //+usersList
        for(User user:usersList){
            longitudes.add(Double.toString(user.getLongitude()));
            latitudes.add(Double.toString(user.getLatitude()));
            countries.add(user.getCountry());
            states.add(user.getState());
            names.add(user.getNickname());
        }
        if(mydb.getUserCount(queryMaps) <= 100) {
            dbrCount = mydb.getminUserCount();
            Log.d(TAG,"minUserCount"+dbrCount);
            userNextList(0);
            usersList.addAll(mydb.getUsers(queryMaps));
        }
    }

    private void userNextList(int page) {
        Toast.makeText(this,"Loading...", Toast.LENGTH_SHORT).show();

        Response.Listener<JSONArray> success_state = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.d("rewstate", response.toString());
                for(int i=0; i<response.length(); i++){
                    try {
                        Log.d("getUsers","response length"+response.length());
                        JSONObject dataObj = response.getJSONObject(i);
                        userId = dataObj.getInt("id");
                        Log.d(TAG,"userid"+userId);
                        nickname = dataObj.getString("nickname");
                        city=dataObj.getString("city");
                        state=dataObj.getString("state");
                        country=dataObj.getString("country");
                        latitude=dataObj.getDouble("latitude");
                        longitude=dataObj.getDouble("longitude");
                        year=dataObj.getInt("year");
                        if(latitude==0 || longitude==0){
                            try {
                                List<Address> sanDiego =
                                        locator.getFromLocationName(state + "," + country, 3);
                                for (Address givenLocation : sanDiego) {
                                    if (givenLocation.hasLatitude())
                                        latitude=givenLocation.getLatitude();

                                    if (givenLocation.hasLongitude())
                                        longitude=givenLocation.getLongitude();
                                }
                            } catch (Exception error) {
                                Log.e("rew", "Address lookup Error", error);
                            }
                        }

                        mydb.insertUser(userId,nickname,null,city,state,country,latitude,longitude,year);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("statefail", error.toString());
            }
        };
        url = "http://bismarck.sdsu.edu/hometown/users?beforeid="+dbrCount+"&page="+page+"&reverse=true&pagesize=100";
        Log.d(TAG,"url " + url);
        JsonArrayRequest getRequestState = new JsonArrayRequest(url, success_state, failure);
        VolleyQueue.instance(this).add(getRequestState);
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
        myMap = googleMap;
        myMap.setOnInfoWindowClickListener(this);
        userListDesc(0);
        for (int i = 0; i < names.size(); i++) {
                Log.d(TAG, "onMapReady: "+latitudes.get(i)+longitudes.get(i));
            /*if(Double.parseDouble(latitudes.get(i))==0.0 || Double.parseDouble(latitudes.get(i))==0.0) {
                Geocoder locator = new Geocoder(this);
                try {
                    List<Address> sanDiego =
                            locator.getFromLocationName(states.get(i) + "," + countries.get(i), 3);
                    for (Address givenLocation : sanDiego) {
                        if (givenLocation.hasLatitude())
                            latitudes.set(i, String.valueOf(givenLocation.getLatitude()));
                        if (givenLocation.hasLongitude())
                            longitudes.set(i, String.valueOf(givenLocation.getLongitude()));
                    }
                } catch (java.io.IOException IOException) {
                    Toast.makeText(this, "Please try again",
                            Toast.LENGTH_LONG).show();
                } catch (Exception error) {
                    Log.e("rew", "Address lookup Error", error);
                    Toast.makeText(this, "Address lookup error. Please try again",
                            Toast.LENGTH_LONG).show();
                }*/

                myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitudes.get(i)),
                        Double.parseDouble(longitudes.get(i)))).title(names.get(i)));
            }
        }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG,"onInfoWindowclicklistener");
        Intent intentlaunch = new Intent(this,ChatActivity.class);
        String reciever = marker.getTitle();
        intentlaunch.putExtra("reciever",reciever);
        startActivity(intentlaunch);
    }


}