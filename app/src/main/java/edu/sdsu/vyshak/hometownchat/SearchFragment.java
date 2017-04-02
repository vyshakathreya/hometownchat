package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Spinner countryList;
    private Spinner stateList;
    private Spinner yearList;
    private String country="Select Country";
    private String state="Select State";
    private String year="Select Year";
    private String  urlSearch = "http://bismarck.sdsu.edu/hometown/users?reverse=true";
    private int startYear=1970;
    ArrayList<String> countries = new ArrayList<String>();
    ArrayList<String> states = new ArrayList<String>();
    ArrayList<String> years = new ArrayList<String>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    OnHeadlineSelectedListener mCallback;
    Calendar cal = Calendar.getInstance();
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        getCountries();
        for(int i=startYear; i <= cal.get(Calendar.YEAR); i++){
            years.add(String.valueOf(i));
        }

        countryList = (Spinner) searchView.findViewById(R.id.spinner_search_country);
        countryList.setOnItemSelectedListener(this);

        stateList = (Spinner) searchView.findViewById(R.id.spinner_search_state);
        stateList.setOnItemSelectedListener(this);

        yearList = (Spinner) searchView.findViewById(R.id.spinner_search_year);
        yearList.setOnItemSelectedListener(this);
        if(!years.isEmpty() && years.get(0) != "Select year") {
            years.add(0, "Select year");
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearList.setAdapter(yearAdapter);

        final Button submit = (Button) searchView.findViewById(R.id.search);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                getSearchResults();
                Bundle bundle = new Bundle();
                bundle.putString("url",urlSearch);
                Log.d("Search Fragment","search result"+urlSearch);
                ResultListFragment nextFrag= new ResultListFragment();
                nextFrag.setArguments(bundle);
                FragmentManager fragments = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragments.beginTransaction();
                fragmentTransaction.replace(R.id.content, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return searchView;
    }

    private void getCountries() {
        countries.clear();
        String url_countries ="http://bismarck.sdsu.edu/hometown/countries";
        Log.d("rew", "Start");
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
                countryList.setAdapter(new ArrayAdapter<String>(SearchFragment.this.getContext(),
                        android.R.layout.simple_list_item_1, countries));
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());

            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( url_countries, success, failure);
        VolleyQueue.instance(this.getContext()).add(getRequest);
    }

    private void stateDetails() {
        states.clear();
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
                stateList.setAdapter(new ArrayAdapter<String>(SearchFragment.this.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, states));
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("statefail", error.toString());
            }
        };
        String url_states ="http://bismarck.sdsu.edu/hometown/states?country="+country;
        if (country != "Select Country") {
            JsonArrayRequest getRequestState = new JsonArrayRequest(url_states, success_state, failure);
            VolleyQueue.instance(this.getContext()).add(getRequestState);
        }
    }

    private void getSearchResults() {
        if(country != "Select Country" && state == "Select State" && year == "Select year"){ //search only for country
            urlSearch = "SELECT nickname,state,country,year FROM friends WHERE country="+country;
        }
        if(state != "Select State" && country != "Select Country" && year == "Select year"){ //search for state and country
            urlSearch = "SELECT nickname,state,country,year FROM friends WHERE country="+country+"AND state="+state;
        }
        if( state == "Select State" && year != "Select year" && country != "Select Country") { //search for year and country
            urlSearch = "SELECT nickname,state,country,year FROM friends WHERE year=" + year + "AND country=" + country;
        }
        if(  country == "Select Country" && year != "Select year" ){ //search only for year
            urlSearch = "SELECT nickname,state,country,year FROM friends WHERE year="+year;
        }
        if(country != "Select Country" && state != "Select State" && year != "Select year"){ //search for particular year , state and country
            urlSearch = "SELECT nickname,state,country,year FROM friends WHERE country="+country+"AND state="+state+"AND year="+year;
        }
        if(state == "Select State" &&  year == "Select year" && country == "Select Country"){ //no filter. select all.
            urlSearch = "SELECT nickname,state,country,year FROM friends";
        }
    }

    public void onArticleSelected(String urlSearch) {
        this.urlSearch = urlSearch;
    }


    // TODO: Rename method, update argument and hook method into UI event
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("in", "onItemSelected: ");
        if(parent.getId()== R.id.spinner_search_country) {
            country = (String) parent.getItemAtPosition(position);
            stateDetails();
        }

        if(parent.getId() == R.id.spinner_search_state){
            state = (String) parent.getItemAtPosition(position);
        }

        if(parent.getId() == R.id.spinner_search_year){
            year = (String) parent.getItemAtPosition(position);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        states.clear();
        state=null;
        year=null;
        country=null;
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
        //mListener.getUrlSearch()
        void onFragmentInteraction(Uri uri);
    }

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String opt_country);
    }
}
