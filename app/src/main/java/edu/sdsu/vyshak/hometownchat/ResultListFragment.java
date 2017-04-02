package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultListFragment extends ListFragment implements AdapterView.OnItemClickListener,SearchFragment.OnHeadlineSelectedListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG="ResultListFragment";
    private OnFragmentInteractionListener mListener;
    private String url;
    private static final int REQ_FORM=123;
    private String longitude;
    private String latitude;
    private String name;
    private String country;
    private String state;
    private ProgressBar progressBar;
    DBHelper mydb;

    ArrayList<String> users = new ArrayList<String>();
    ArrayList<String> longitudes = new ArrayList<>();
    ArrayList<String> latitudes = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<String> countries = new ArrayList<>();



    public ResultListFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultListFragment newInstance(String param1, String param2) {
        ResultListFragment fragment = new ResultListFragment();
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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            url = bundle.getString("url", " ");
        }
        mydb = new DBHelper(this.getContext());
        View resultlist = inflater.inflate(R.layout.fragment_result_list, container, false);

        final Button locate = (Button) resultlist.findViewById(R.id.mark);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                Boolean action = false;
                Log.d("sending","multiple"+names+longitudes);
                Intent intentlist = new Intent(getActivity(), MapsActivityList.class);
                intentlist.putExtra("long",longitude);
                intentlist.putExtra("lat",latitude);
                intentlist.putExtra("name",name);
                intentlist.putExtra("state",state);
                intentlist.putExtra("country",country);
                intentlist.putStringArrayListExtra("longlist",longitudes);
                intentlist.putStringArrayListExtra("latlist",latitudes);
                intentlist.putStringArrayListExtra("namelist",names);
                intentlist.putStringArrayListExtra("countrylist",countries);
                intentlist.putStringArrayListExtra("stateslist",states);
                intentlist.putExtra("action",action);
                startActivity(intentlist);
            }
        });
        return resultlist;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userlist();
        getListView().setOnItemClickListener(this);
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

    private void userlist() {
        Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
        users = mydb.getUsers(url);
        Log.d(TAG, "userlist: "+users);
        getListView().setAdapter(new CustomAdapter(usersList, getContext()));
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean action = true;
        longitude = longitudes.get(position);
        latitude = latitudes.get(position);
        name = names.get(position);
        country = countries.get(position);
        state = states.get(position);
        /*Intent intent = new Intent(getActivity(), MapsActivityList.class);
        intent.putExtra("long",longitude);
        intent.putExtra("lat",latitude);
        intent.putExtra("name",name);
        intent.putExtra("action",action);
        intent.putExtra("state",state);
        intent.putExtra("country",country);
        intent.putStringArrayListExtra("longlist",longitudes);
        intent.putStringArrayListExtra("latlist",latitudes);
        intent.putStringArrayListExtra("countrylist",countries);
        intent.putStringArrayListExtra("stateslist",states);
        intent.putStringArrayListExtra("namelist",names);
        startActivity(intent);*/
    }

    @Override
    public void onArticleSelected(String opt_country) {
        this.url=opt_country;
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
