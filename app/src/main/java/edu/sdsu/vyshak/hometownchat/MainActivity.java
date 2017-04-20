package edu.sdsu.vyshak.hometownchat;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    DBHelper mydb;
    private String url;
    private double latitude;
    private double longitude;
    private String nickname;
    private String city;
    private String state;
    private String country;
    private int page=0;
    private int year;
    private int dbcount;



    private int servercount;
    private int userId;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 123;

    ArrayList<String> users = new ArrayList<String>();
    Geocoder locator;
    SearchFragment fragmentSearch = new SearchFragment();
    ResultListFragment fragmentResult = new ResultListFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.findfriends:
                    userlist(page);
                    loadResultFragment();
                    return true;
                case R.id.logout:
                    logout();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydb = new DBHelper(this);
        new GetDatabaseTask().execute(mydb,null,null);
        locator = new Geocoder(this);
        dbcount=mydb.getMaxid();
        Log.d(TAG,"dbcount"+dbcount);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("check","login");
        startActivityForResult(loginIntent,RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                auth = FirebaseAuth.getInstance();
                setContentView(R.layout.activity_main);
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                loadSearchFragment();
                getCount();
                userlist(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    private void getCount() {
        final int gCount=0;
        String url =" http://bismarck.sdsu.edu/hometown/nextid";
        Log.d(TAG,"resultatMain");//better value than http://bismarck.sdsu.edu/hometown/count
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        setServercount(Integer.parseInt(response));

                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("rew", "error", error);
            }
        });
        VolleyQueue.instance(this).add(stringRequest);


    }

    private void logout() {
        auth.getInstance().signOut();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("check","logout");
        startActivity(loginIntent);

    }

    private void loadSearchFragment() {
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        fragmentTransaction.add(R.id.content1, fragmentSearch);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadResultFragment() {
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        fragmentTransaction.add(R.id.content2, fragmentResult);
        fragmentTransaction.commit();
    }

    public void userlist(int page) {
        Toast.makeText(this,"Loading...", Toast.LENGTH_SHORT).show();
        Response.Listener<JSONArray> success_state = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.d("rewstate", response.toString());
                users.clear();
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
        Log.d("","servendcount"+servercount);
        if(dbcount==0)
            url = "http://bismarck.sdsu.edu/hometown/users?&page="+page+"&reverse=true";
        else if(servercount>dbcount)
            url = "http://bismarck.sdsu.edu/hometown/users?&afterid="+dbcount+"&beforeid="+servercount+"&page="+page+"&reverse=true";
        else
            url = "http://bismarck.sdsu.edu/hometown/users?&afterid="+dbcount+"&page="+page+"&reverse=true";
        Log.d(TAG,"url " + url);
        JsonArrayRequest getRequestState = new JsonArrayRequest(url, success_state, failure);
        VolleyQueue.instance(this).add(getRequestState);
    }

    public int getServercount() {
        return servercount;
    }

    public void setServercount(int servercount) {
        this.servercount = servercount;
    }
}
