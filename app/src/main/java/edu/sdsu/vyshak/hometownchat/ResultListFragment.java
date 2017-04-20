package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultListFragment extends ListFragment implements AdapterView.OnItemClickListener,AbsListView.OnScrollListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG="ResultListFragment";
    private OnFragmentInteractionListener mListener;
    private String query="SELECT nickname,state,country,year,latitude,longitude FROM users";
    private String recievedQuery="SELECT nickname,state,country,year,latitude,longitude FROM users";
    private String url;
    private static final int REQ_FORM=123;
    private String category="all";
    private String country;
    private String state;
    private String name;
    private String nickname;
    private String city;
    private int userId;
    private int nextID=0;
    private int serverCount;
    private int dbrCount;
    private int year;
    private double longitude;
    private double latitude;

    DBHelper mydb;
    Geocoder locator;
    CustomAdapter customAdapter;

    ArrayList<String> users = new ArrayList<String>();
    ArrayList<User> usersList = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<ChatUser> chatUsers = new ArrayList<>();
    private ArrayList<String> chatNickNames;

    public ResultListFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
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
        View resultList = inflater.inflate(R.layout.fragment_result_list, container, false);
        final CustomAdapter customAdapter = new CustomAdapter(usersList,getContext());
        Bundle bundle = this.getArguments();
        locator= new Geocoder(this.getActivity());
        if (bundle != null) {
            recievedQuery = bundle.getString("url", " ");
            category = bundle.getString("cat");
            Log.d(TAG, "onCreateView: query " + recievedQuery + category);
        }
        mydb = new DBHelper(this.getContext());
        final Button load = (Button) resultList.findViewById(R.id.loadMoreData);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                userList(0);
                usersList.clear();
                usersList.addAll(mydb.getUsers(query));
                customAdapter.notifyDataSetChanged();
                getListView().setAdapter(new CustomAdapter(usersList, getContext()));
            }
        });
        return resultList;
    }

    private void userList(int page) {
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
        url = "http://bismarck.sdsu.edu/hometown/users?&page="+page+"&reverse=true";
        Log.d(TAG,"url" + url);
        JsonArrayRequest getRequestState = new JsonArrayRequest(url, success_state, failure);
        VolleyQueue.instance(getActivity()).add(getRequestState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userListDesc(0);
        getListView().setOnItemClickListener(this);
        getListView().setOnScrollListener(this);
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

    //get next 25 users from db
    private void userListDesc(int start) {
        query=recievedQuery;
        if(category.equals("country")){
            query = query +" ORDER BY id DESC LIMIT "+start+",25";
        }
        if(category.equals("countryState")){
            query = query+" ORDER BY id DESC LIMIT "+start+",25";
        }
        if(category.equals("countryYear")) {
            query = query+" ORDER BY id DESC LIMIT "+start+",25";
        }
        if(category.equals("year") ){
            query = query+" ORDER BY id DESC LIMIT "+start+",25";
        }
        if(category.equals("countryStateYear")){
            query = query+" ORDER BY id DESC LIMIT "+start+",25";
        }
        if(category.equals("all")){
            query = query+" ORDER BY id DESC LIMIT "+start+",25";
        }
        if(mydb.getUserCount(query) >= 20)
            usersList.addAll(mydb.getUsers(query));
        else {
            dbrCount = mydb.getminUserCount();
            Log.d(TAG,"minUserCount"+dbrCount);
            userNextList(0);
            usersList.addAll(mydb.getUsers(query));
        }
        for(User user:usersList)
            names.add(user.getNickname());
        Log.d(TAG, "userlist: "+usersList);

        getListView().setAdapter(new CustomAdapter(usersList, getContext()));
    }

    //get 25 users below the current db minimum
    private void userNextList(int page) {
        Toast.makeText(this.getActivity(),"Loading...", Toast.LENGTH_SHORT).show();

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
        url = "http://bismarck.sdsu.edu/hometown/users?beforeid="+dbrCount+"&page="+page+"&reverse=true";
        Log.d(TAG,"url " + url);
        JsonArrayRequest getRequestState = new JsonArrayRequest(url, success_state, failure);
        VolleyQueue.instance(this.getActivity()).add(getRequestState);
    }

    public void getAllUsersFromFirebase(final int position) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            ChatUser chatUser = dataSnapshotChild.getValue(ChatUser.class);
                            Log.d(TAG,"nick"+chatUser.getNickname());
                            Log.d(TAG,"nicklist"+names.get(position));
                            if(chatUser.getNickname().equals(names.get(position)) ){
                                if (!TextUtils.equals(chatUser.uid,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    chatUsers.add(chatUser);
                                    //chatNickNames.add(chatUser.getUid());
                                    Log.d(TAG,"getAllusers"+chatUser.getNickname());
                                    Log.d(TAG,"getAllusers"+chatUser.getUid());
                                    Log.d(TAG,""+chatUsers.toString());
                                    Log.d(TAG,""+chatUser.toString());
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("recievername",chatUser.getNickname());
                                    intent.putExtra("reciever",chatUser.getUid() );
                                    startActivity(intent);
                                }
                            }else{
                                Toast.makeText(getActivity(),"User Not available for chat",Toast.LENGTH_LONG);
                            }


                        }
                        // All users are retrieved except the one who is currently logged
                        // in device.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        name = names.get(position);
        getAllUsersFromFirebase(position);
        if(chatUsers.contains(name)) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("reciever", name);
            startActivity(intent);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG,"on Scroll");
        if(totalItemCount <= firstVisibleItem+visibleItemCount && totalItemCount!=0){
            Log.d(TAG,"onScrollfirst"+firstVisibleItem);
            Log.d(TAG,"onScrollvisible"+visibleItemCount);
            Log.d(TAG,"onScrolltotal"+totalItemCount);
            nextID+=25;
            userListDesc(nextID);
        }
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
