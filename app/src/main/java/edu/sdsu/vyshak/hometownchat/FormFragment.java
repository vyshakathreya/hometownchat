package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RequestQueue queue;
    ArrayList<String> countries = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<String> years = new ArrayList<>();


    private OnFragmentInteractionListener mListener;
    private String nickName;
    private String country;
    private String state;
    private String city;
    private String passwordChosen;
    private String TAG="Form Fragment";
    private String latitudetext;
    private String longitudetext;
    private int startYear=1970;
    private int year;
    private Double latitude;
    private Double longitude;
    private Spinner spinnerCountry;
    private Spinner spinnerState;
    private Spinner spinnerYear;
    private EditText nickNameField;
    private EditText passwordField;
    private EditText cityEdit;
    private EditText latitudeEdit;
    private EditText longitudeEdit;
    private static final int REQ_FORM=123;

    public FormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Form.
     */
    // TODO: Rename and change types and number of parameters
    public static FormFragment newInstance(String param1, String param2) {
        FormFragment fragment = new FormFragment();
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
        View formView = inflater.inflate(R.layout.fragment_form, container, false);
        getCountries();
        Calendar cal = Calendar.getInstance();
        for(int i=startYear; i <= cal.get(Calendar.YEAR); i++){
            years.add(String.valueOf(i));
        }
        spinnerCountry = (Spinner) formView.findViewById(R.id.spinner_country);
        spinnerCountry.setOnItemSelectedListener(this);
        spinnerState = (Spinner) formView.findViewById(R.id.spinner_state);
        spinnerState.setOnItemSelectedListener(this);
        spinnerYear = (Spinner) formView.findViewById(R.id.spinner_year);
        spinnerYear.setOnItemSelectedListener(this);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        nickNameField = (EditText) formView.findViewById(R.id.textnickname);
        passwordField = (EditText) formView.findViewById(R.id.password);
        cityEdit = (EditText) formView.findViewById(R.id.city);
        latitudeEdit = (EditText) formView.findViewById(R.id.lat);
        longitudeEdit = (EditText) formView.findViewById(R.id.lon);

        final Button locate = (Button) formView.findViewById(R.id.locate);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intent,REQ_FORM);
            }
        });



        final Button submit = (Button) formView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                nickName = nickNameField.getText().toString();
                passwordChosen = passwordField.getText().toString();
                city= cityEdit.getText().toString();
                latitudetext = latitudeEdit.getText().toString();
                longitudetext = longitudeEdit.getText().toString();
                if(nicknameTest()) nickNameField.setError("Please Choose another name");
                if(nickNameField == null) passwordField.setError("Please choose a nickname");
                if(passwordField == null) passwordField.setError("Please choose a password");
                if(passwordField.length() < 3) passwordField.setError("Password should be a minimum of 3 characters");
                if(city == null) cityEdit.setError("Please enter your city");
                if(latitude == null) latitudeEdit.setError("Please enter latitude or find them through maps");
                if(longitude == null) longitudeEdit.setError("Please enter longitude or find them through maps");
                if(city!=null && latitude != null && longitude != null && country != null && state != null &&
                        !nicknameTest() && passwordChosen!=null && nickName!=null) {
                    postData();
                }
                else Toast.makeText(getActivity(), "Please correct errors and try again",
                        Toast.LENGTH_LONG).show();
            }
        });
        return formView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_FORM) {
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                latitude = Double.valueOf(data.getStringExtra("latitude"));
                setLatitude(latitude);
                latitudeEdit.setText(latitude.toString());
                Log.d("formfragment","latitude"+latitude);
                longitude = Double.valueOf(data.getStringExtra("longitude"));
                longitudeEdit.setText(longitude.toString());
                setLongitude(longitude);
                Log.d("formfragment","longitude"+longitude);
                break;
            case RESULT_CANCELED:
                break;
        }
    }
    private void getCountries() {
        if(!haveNetworkAccess()){
            Toast.makeText(getActivity(), "Please enable network access",
                    Toast.LENGTH_LONG).show();
        }
        String url_countries ="http://bismarck.sdsu.edu/hometown/countries";
        Log.d("rew", "Start");
        countries.clear();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        if(!countries.isEmpty() && countries.get(0) != "Select Country"){
                            countries.add(0,"Select Country");
                        }
                        countries.add(response.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                spinnerCountry.setAdapter(new ArrayAdapter<String>(FormFragment.this.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, countries));
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rewCountryError", error.toString());
                Toast.makeText(getActivity(), "Not able to fetch countries",
                        Toast.LENGTH_LONG).show();
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( url_countries, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);
    }

    private void stateDetails() {
        states.clear();
        if(!haveNetworkAccess()){
            Toast.makeText(getActivity(), "Please enable network access",
                    Toast.LENGTH_LONG).show();
        }
        Response.Listener<JSONArray> success_state = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.d("rewstate", response.toString());
                for(int i=0; i<response.length(); i++){
                    try {
                        if(!states.isEmpty() && states.get(0) != "Select State") {
                            states.add(0, "Select State");
                        }
                        states.add(response.getString(i));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                spinnerState.setAdapter(new ArrayAdapter<String>(FormFragment.this.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, states));
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("statefail", error.toString());}
        };
        String url_states ="http://bismarck.sdsu.edu/hometown/states?country="+country;
        if(country != "Select Country"){
            JsonArrayRequest getRequestState = new JsonArrayRequest(url_states, success_state, failure);
            VolleyQueue.instance(this.getContext()).add(getRequestState);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d("Listener", "onItemSelected: ");
        if(parent.getId()== R.id.spinner_country) {
            country = (String) parent.getItemAtPosition(position);
            stateDetails();
        }

        if(parent.getId() == R.id.spinner_state){
            state = (String) parent.getItemAtPosition(position);
        }

        if(parent.getId() == R.id.spinner_year){
            year =  Integer.parseInt(parent.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void postData(){

        JSONObject data = new JSONObject();
        try {
            data.put("nickname",nickName);
            data.put("password",passwordChosen);
            data.put("city",city);
            data.put("longitude", getLongitude());
            data.put("state",state);
            data.put("year",year);
            data.put("latitude", getLatitude());
            data.put("country",country);
        } catch (JSONException error) {
            Log.e("rew", "JSON error", error);
            return;
        }

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Process response here
                if ( response.has("message")  ) {
                    // handle error here
                    Toast.makeText(getActivity(), "Data saved successfully!",
                            Toast.LENGTH_LONG).show();
                }
                Log.i("rewPost", response.toString());
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
                Toast.makeText(getActivity(), "Please correct the errors",
                        Toast.LENGTH_LONG).show();
            }
        };
        String urlPost="http://bismarck.sdsu.edu/hometown/adduser";
        JsonObjectRequest postRequest = new JsonObjectRequest(urlPost, data, success, failure);
        VolleyQueue.instance(this.getContext()).add(postRequest);
    }

    public boolean nicknameTest(){
        final boolean[] result = new boolean[1];
        String urlCheckName="http://bismarck.sdsu.edu/hometown/nicknameexists?name="+nickName;

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                result[0] = Boolean.parseBoolean(response.toString());
                Log.d("rew", "Start"+ result);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( urlCheckName, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);
        return result[0];
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public boolean haveNetworkAccess() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
            return false;
        }
        return true;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
