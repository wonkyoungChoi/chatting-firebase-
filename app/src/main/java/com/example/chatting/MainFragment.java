package com.example.chatting;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatting.Board.BoardFragment;
import com.example.chatting.Chatting.ChatAdapter;
import com.example.chatting.Chatting.ChattingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainFragment extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ProfileFragment frag1;
    private BoardFragment frag2;
    private ChattingFragment frag3;
    private SettingFragment frag4;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ConnectDialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Boolean check = false;
    ArrayList<Info> emailList;

    private static final String TAG = "MainActivity";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);

        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserInfo();

        // 바텀 네비게이션 뷰
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(!check) {
                    getUserInfo();
                    return false;
                } else {
                    switch (menuItem.getItemId()) {
                        case R.id.action_profile:
                            setFrag(0);
                            break;
                        case R.id.action_board:
                            setFrag(1);
                            break;
                        case R.id.action_chatting:
                            setFrag(2);
                            break;
                        case R.id.action_setting:
                            setFrag(3);
                            break;

                    }
                    return true;
                }
            }
        });

        frag1 = new ProfileFragment();
        frag2 = new BoardFragment();
        frag3 = new ChattingFragment();
        frag4 = new SettingFragment();


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
            case 3:
                ft.replace(R.id.frame_container, frag4);
                ft.commit();
                break;
        }
    }

    private void getUserInfo() {
        emailList = new ArrayList<>();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String uid = document.getId();
                                Map<String, Object> map = document.getData();
                                emailList.add(new Info(map.get("email").toString(), uid, (Boolean) map.get("check")));
                                if(map.get("email").toString().equals(user.getEmail())) {
                                    if(!(Boolean) map.get("check")) {
                                        OnClickHandler();
                                    } else {
                                        check = true;
                                    }
                                }
                            }
                        }
                    }
                });

    }

    private void OnClickHandler() {
        dialog = new ConnectDialog(MainFragment.this, new ConnectDialogClickListener() {
            String otherEmail;
            @Override
            public void onPositiveClick() {
                otherEmail = dialog.binding.otherEmail.getText().toString();
                if (check) {
                    db.collection("user").document(user.getUid()).update("check", true);
                    db.collection("user").document(user.getUid()).update("chattingRoom", user.getEmail() + "&" + otherEmail);

                    for (int i = 0; i < emailList.size(); i++) {
                        if (emailList.get(i).email.equals(otherEmail)) {
                            db.collection("user").document(emailList.get(i).getUid()).update("check", true);
                            db.collection("user").document(emailList.get(i).getUid()).update("chattingRoom", user.getEmail() + "&" + otherEmail);
                            db.collection("user").document(user.getUid()).update("otherUid", emailList.get(i).getUid());
                            db.collection("user").document(emailList.get(i).getUid()).update("otherUid", user.getUid());

                            Toast.makeText(getApplicationContext(), "연결완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "상대 연결여부 체크를 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNegativeClick() {
                check = false;
            }

            @Override
            public void onCheckClick() {
                otherEmail = dialog.binding.otherEmail.getText().toString();
                for(int i=0; i<emailList.size(); i++) {
                    if(emailList.get(i).email.equals(otherEmail)) {
                        db.collection("user").document(emailList.get(i).getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                Map<String, Object> map = document.getData();
                                                Log.d(TAG, map.get("check").toString());
                                                if(!(Boolean) map.get("check")) {
                                                    Toast.makeText(getApplicationContext(), "연결이 가능한 상대입니다.", Toast.LENGTH_SHORT).show();
                                                    check = true;

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "연결이 불가능한 상대입니다.", Toast.LENGTH_SHORT).show();
                                                    check = false;
                                                }


                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                    }
                }
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
    }
}

