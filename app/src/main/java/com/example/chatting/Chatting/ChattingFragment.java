package com.example.chatting.Chatting;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatting.ProfileFragment;
import com.example.chatting.databinding.FragmentChattingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChattingFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ChattingFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef= database.getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ChatAdapter chatAdapter;
    ChatData chatData;
    static String key;
    String msg;
    String chattingRoom;
    private static final String TAG = "Fragment_chatting";
    ChildEventListener childEventListener;

    FragmentChattingBinding binding;

    String otherToken;

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA31ZQ2Q8:APA91bH34l5MkyFv8K9cK3s2__Rjjm42JvOUqLkuhUv3T0-uTU0DVR2yEaezJAt_WoUhXci3eaCoiZc5qE7tU6Z_ioc9wDAKqih4PLi8pQ5c6wdrVkRUoc4Yp8unzHWSRsOmae75JtaM";


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
    public static ChattingFragment newInstance(String param1, String param2) {
        ChattingFragment fragment = new ChattingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public ChattingFragment() {
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
        myRef.child(chattingRoom).removeEventListener(childEventListener);
        super.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChattingBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        initRecyclerview();
        setMyInfo();


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
                sendPostToFCM(msg);

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    private void initRecyclerview() {
        chatAdapter = new ChatAdapter();
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(chatAdapter);
    }

    private void sendMsg() {
        key = myRef.push().getKey();
        msg = binding.EditTextChat.getText().toString();
        ChatData chat = new ChatData(ChatAdapter.nick, msg, ProfileFragment.uri, key, ServerValue.TIMESTAMP);
        Log.d("SEND", ChatAdapter.nick + "msg" + msg);
        myRef.child(chattingRoom).child(key).setValue(chat);
        if(chatAdapter.getItemCount()>1) {
            binding.recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        }
        binding.EditTextChat.setText(null);
    }

    private void setMyInfo() {

        DocumentReference docRef = db.collection("user").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> map = document.getData();
                        assert map != null;
                        chattingRoom = map.get("chattingRoom").toString().replace(".", ",");

                        childEventListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                chatData = dataSnapshot.getValue(ChatData.class);
                                chatAdapter.addChat(chatData);
                                binding.recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
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
                        myRef.child(chattingRoom).addChildEventListener(childEventListener);

                        Log.d(TAG, map.get("chattingRoom").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    private void sendPostToFCM(final String message) {
        db.collection("user")
                .document(ProfileFragment.otherUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                Map<String, Object> map = document.getData();
                                assert map != null;

                                otherToken = map.get("fcmToken").toString();

                                try {
                                    // FMC 메시지 생성 start

                                    JSONObject root = new JSONObject();
                                    JSONObject notification = new JSONObject();
                                    notification.put("body", message);
                                    notification.put("title", ChatAdapter.nick);
                                    root.put("notification", notification);
                                    root.put("to", otherToken);

                                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), root.toString());
                                    Request request = new Request.Builder()
                                            .header("Content-Type", "application/json")
                                            .addHeader("Authorization", "key=" + SERVER_KEY)
                                            .url(FCM_MESSAGE_URL)
                                            .post(requestBody)
                                            .build();
                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    okHttpClient.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {

                                        }
                                    });
                                    // FMC 메시지 생성 end

                                } catch (Exception e) {
                                    e.printStackTrace();
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