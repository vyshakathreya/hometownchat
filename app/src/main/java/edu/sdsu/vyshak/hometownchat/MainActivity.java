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
    private String urlCount;
    private double latitude;
    private double longitude;
    private String nickname;
    private String email;
    private String city;
    private String state;
    private String country;
    private int page;
    private int year;
    private int dbcount;
    private int servercount;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;

    ArrayList<String> users = new ArrayList<String>();
    Geocoder locator;
    FormFragment fragmentForm = new FormFragment();
    SearchFragment fragmentSearch = new SearchFragment();
    ResultListFragment fragmentResult = new ResultListFragment();
    LoginActivity loginActivity = new LoginActivity();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.myprofile:
                    loadFormFragment();
                    return true;
                case R.id.findfriends:
                    if(servercount>dbcount)
                        userlist();
                    loadSearchFragment();
                    loadResultFragment();
                    return true;
                case R.id.logout:
                    logout();


                    return true;
            }
            return false;
        }

    };

    private void logout() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        setContentView(R.layout.activity_main);
        getCount();
        Log.d(TAG,"user"+FirebaseAuth.getInstance().toString());
        mydb = new DBHelper(this);
        new GetDatabaseTask().execute(mydb);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        locator = new Geocoder(this);
        dbcount=mydb.getUserCount();
        Log.d(TAG,"dbcount"+dbcount);
        if(servercount>dbcount)
        userlist();
        loadSearchFragment();
        loadResultFragment();
    }

    private void getCount() {
        /*String urlCheckName="http://bismarck.sdsu.edu/hometown/count";

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                servercount=Integer.getInteger(response.toString());
                Log.d(TAG,"getCount()"+response.toString());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        JsonObjectRequest getRequest = new JsonObjectRequest(urlCheckName, success, failure);
        VolleyQueue.instance(this).add(getRequest);*/

        String url ="http://bismarck.sdsu.edu/hometown/count";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                       // page=Integer.getInteger(response)/25;
                        servercount=Integer.parseInt(response);
                        Log.d(TAG,"getCount"+servercount);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("rew", "error", error);
            }
        });
        VolleyQueue.instance(this).add(stringRequest);
    }

    private void loadSearchFragment() {
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragmentSearch);
        fragmentTransaction.remove(fragmentForm);
        fragmentTransaction.commit();
    }

    private void loadFormFragment() {
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        fragmentTransaction.replace(R.id.content1, fragmentForm);
        fragmentTransaction.remove(fragmentSearch);
        fragmentTransaction.remove(fragmentResult);
        fragmentTransaction.commit();
    }

    private void loadResultFragment() {
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        fragmentTransaction.replace(R.id.content2, fragmentResult);
        fragmentTransaction.remove(fragmentForm);
        fragmentTransaction.commit();
    }

    private void userlist() {
        Toast.makeText(this,"Loading...", Toast.LENGTH_SHORT).show();

        Response.Listener<JSONArray> success_state = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.d("rewstate", response.toString());
                users.clear();
                for(int i=0; i<response.length(); i++){
                    try {
                        Log.d("getUsers","response length"+response.length());
                        JSONObject dataObj = response.getJSONObject(i);
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
                        //if(mydb.getUsers("Select nickname from friends"))
                        mydb.insertUser(nickname,null,city,state,country,latitude,longitude,year);
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
        //for(int i=0;i<page;i++) {
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid="+dbcount;
            Log.d(TAG,"url" + url);
            JsonArrayRequest getRequestState = new JsonArrayRequest(url, success_state, failure);
            VolleyQueue.instance(this).add(getRequestState);
        //}
    }

    public class GetDatabaseTask extends AsyncTask<SQLiteOpenHelper ,Void, Void> {
        @Override
        protected Void doInBackground(SQLiteOpenHelper... params) {
            params[0].getWritableDatabase();
            return null;
        }
    }
}
