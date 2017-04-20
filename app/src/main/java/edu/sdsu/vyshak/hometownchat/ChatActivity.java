package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "Chat Activity";
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private String recieverid;
    private String sender;
    private String recieverName;
    private String senderid;
    public String CHAT_ROOMS = "chat_rooms";
    private Timestamp dateTime;
    private Date date;
    private FirebaseListAdapter<ChatMessage> adapter;
    ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    private ListView listOfMessages;
    private ListView listofUsers;
    CustomChatAdapter customChatAdapter;
    UsersAdapter userAdapter;

    private ArrayList<ChatUser> chatUsers;
    ChatUser chatUser = new ChatUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        chatUsers = new ArrayList<>();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + user.getDisplayName());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        recieverid = getIntent().getStringExtra("reciever");
        recieverName = getIntent().getStringExtra("recievername");
        Log.d(TAG,"recieved"+recieverid + recieverName);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user.getDisplayName() != null)
            sender = user.getDisplayName();
        else
            sender=user.getUid();
        senderid=user.getUid();
        showMesaages(senderid,recieverid);

        getAllUsersFromFirebase();
        listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        customChatAdapter = new CustomChatAdapter(chatMessages,getApplicationContext());
        listOfMessages.setAdapter(customChatAdapter);

        listofUsers = (ListView) findViewById(R.id.usersview);
        userAdapter = new UsersAdapter(this,chatUsers);
        listofUsers.setAdapter(userAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                ChatMessage chatMessage = new ChatMessage(senderid,sender, recieverid, input.getText().toString(),recieverName);
                input.setText("");
                sendMessageToFirebaseUser(chatMessage);
            }
        });
        listofUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                               @Override
                                               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                   ChatUser chatUser1 =chatUsers.get(position);
                                                   Log.d(TAG,"reciever"+chatUser1.getUid() + chatUser1.getNickname());
                                                   recieverid = chatUser1.getUid();
                                                   showMesaages(senderid,chatUser1.getUid());
                                               }
                                           }
        );
    }

    public void getAllUsersFromFirebase() {
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
                            Log.d(TAG, "nick" + chatUser.getNickname());
                            if (!TextUtils.equals(chatUser.uid,
                                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                chatUsers.add(chatUser);
                                userAdapter.notifyDataSetChanged();
                                Log.d(TAG, "getAllusers" + chatUser.getNickname());
                                Log.d(TAG, "getAllusers" + chatUser.getUid());
                                Log.d(TAG, "" + chatUsers.toString());
                                Log.d(TAG, "" + chatUser.toString());
                            } else {

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });}

    private void showMesaages(final String sender, final String reciever) {
        Log.d("path",""+sender+"_"+reciever);
        DatabaseReference people = FirebaseDatabase.getInstance().getReference();
        people.child(CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(sender+"_"+reciever)){
                    Log.d("rew","listening");
                    FirebaseDatabase.getInstance().getReference().child(CHAT_ROOMS).child(sender+"_"+reciever).getRef().addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.d(TAG,"onchildAdded"+dataSnapshot.getChildrenCount());
                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                            chatMessages.add(chatMessage);
                            Log.d(TAG,"listfromadap"+customChatAdapter.getData().toString());
                            customChatAdapter.notifyDataSetChanged();
                            Log.d("rew",""+chatMessages);
                            Log.d("rew","onchildAdded"+chatMessage.toString() + dataSnapshot.getValue());
                            Log.d("rew","onchildAdded"+ dataSnapshot.getValue());
                        }


                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if(dataSnapshot.hasChild(reciever+"_"+sender)){
                    Log.d("rew","listening");
                    FirebaseDatabase.getInstance().getReference().child(CHAT_ROOMS).child(reciever+"_"+sender).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                            Log.d("rew",""+chatMessages);
                            Log.d("rew","onchildAdded"+dataSnapshot.getChildrenCount());

                            chatMessages.add(chatMessage);
                            customChatAdapter.notifyDataSetChanged();
                            Log.d("rew","onchildAdded"+chatMessage.toString() + dataSnapshot.getValue());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    customChatAdapter.getData().clear();
                    customChatAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendMessageToFirebaseUser(final ChatMessage chat) { //chat.senderUid
        final String room_type_1 =  senderid+ "_" + recieverid;
        final String room_type_2 = recieverid + "_" + senderid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child(CHAT_ROOMS)
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                            databaseReference.child(CHAT_ROOMS)
                                    .child(room_type_1)
                                    .child(String.valueOf(new Date().getTime()))
                                    .setValue(chat);
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                            databaseReference.child(CHAT_ROOMS)
                                    .child(room_type_2)
                                    .child(String.valueOf(new Date().getTime()))
                                    .setValue(chat);
                        } else {
                            Log.e(TAG, "sendMessageToFirebaseUser: success");
                            databaseReference.child(CHAT_ROOMS)
                                    .child(room_type_1)
                                    .child(String.valueOf(new Date().getTime()))
                                    .setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onStop(){
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}