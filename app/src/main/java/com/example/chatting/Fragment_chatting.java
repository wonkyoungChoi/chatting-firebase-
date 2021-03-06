package com.example.chatting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_chatting#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Fragment_chatting extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef= database.getReference();
    ChatAdapter chatAdapter;
    List<ChatData> chatlist = new ArrayList<>();
    static int start = 1;
    ChatData chatData;
    static String key;
    private EditText EditText_chat;
    private static final String TAG = "Fragment_chatting";
    ChildEventListener childEventListener;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_chatting.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_chatting newInstance(String param1, String param2) {
        Fragment_chatting fragment = new Fragment_chatting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public Fragment_chatting() {
        // Required empty public constructor
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
    public void onPause() {
        Log.d(TAG, "remove");
        myRef.child("Chat").removeEventListener(childEventListener);
        super.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chatting, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        Button Button_send;
        Button_send = v.findViewById(R.id.send);
        EditText_chat = v.findViewById(R.id.EditText_chat);
        chatlist.clear();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("CHATCHAT", dataSnapshot.getValue().toString());
                chatData = dataSnapshot.getValue(ChatData.class);
                chatAdapter.addChat(chatData);
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.child("Chat").addChildEventListener(childEventListener);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(chatlist);

        recyclerView.setAdapter(chatAdapter);

        Button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = myRef.push().getKey();
                String msg = EditText_chat.getText().toString();
                ChatData chat = new ChatData(ChatAdapter.nick, msg, Fragment_profile.uri, key);
                Log.d("SEND", ChatAdapter.nick + "msg" + msg);
                myRef.child("Chat").child(key).setValue(chat);
                if(ChatAdapter.chatData.size()>1) {
                    recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
                }
                EditText_chat.setText(null);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

}