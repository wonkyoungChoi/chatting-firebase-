package com.example.chatting;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ChatData> chatlist;
    static String nick = null;
    private DatabaseReference myRef;
    private EditText EditText_chat;
    private Button Button_send;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button_send = findViewById(R.id.send);
        EditText_chat = findViewById(R.id.EditText_chat);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            myStartActivity(LoginActivity.class);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("user").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.get("nickname"));
                                nick = document.get("nickname").toString();
                            } else {
                                myStartActivity(MemberActivity.class);
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                }
            });
        }


            Button_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = EditText_chat.getText().toString();
                    ChatData chat = new ChatData();
                    chat.setNickname(nick);
                    chat.setMassage(msg);
                    myRef.push().setValue(chat);
                }
            });

            mRecyclerview = findViewById(R.id.recycler_view);
            mRecyclerview.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerview.setLayoutManager(mLayoutManager);

            chatlist = new ArrayList<>();
            mAdapter = new ChatAdapter(chatlist, nick);
            mRecyclerview.setAdapter(mAdapter);

            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference();

            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("CHATCHAT", snapshot.getValue().toString());
                    ChatData chat = snapshot.getValue(ChatData.class);
                    ((ChatAdapter) mAdapter).addChat(chat);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //1. recyclerView - 반복

        }

    private void myStartActivity(Class c){
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
    }


}