package com.example.chatting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Fragment_profile frag1;
    private Fragment_chatting frag2;
    private Fragment_setting frag3;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef= database.getReference();
    static ChatAdapter chatAdapter;


    private static final String TAG = "MainActivity";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        ChatAdapter.chatData.clear();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("CHATCHAT", snapshot.getValue().toString());
                ChatData chat = snapshot.getValue(ChatData.class);
                chatAdapter.addChat(chat);
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

        // 바텀 네비게이션 뷰
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:
                        setFrag(0);
                        break;
                    case R.id.action_chatting:
                        setFrag(1);
                        break;
                    case R.id.action_setting:
                        setFrag(2);
                        break;

                }
                return true;
            }
        });

        frag1 = new Fragment_profile();
        frag2 = new Fragment_chatting();
        frag3 = new Fragment_setting();


        setFrag(0); // 첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택.


    }
    // 프래그먼트 교체가 일어나는 실행문이다.
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.frame_container, frag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.frame_container, frag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.frame_container, frag3);
                ft.commit();
                break;
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
    }
}

