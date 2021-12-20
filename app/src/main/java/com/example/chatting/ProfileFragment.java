package com.example.chatting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.chatting.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "Fragment_profile";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public static String uri;
    int PICK_IMAGE_REQUEST = 1;

    Uri uri1, downloadUri;

    String downuri;
    Bitmap bitmap;
    ProgressDialog mProgressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Boolean checkProfile = true;

    FragmentProfileBinding binding;

    public static String otherUid;

    String myProfileUrl, otherProfileUrl;


    // 현재 날짜를 알기 위해 사용
    Calendar calendar;
    int currentYear, currentMonth, currentDay;

    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_profile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        binding.loading.playAnimation();

        setCalendar();
        setInfo(user.getUid());
        initTabLayout();

        //datePicker : 디데이 날짜 입력하는 버튼, 클릭시 DatePickerDialog 띄우기
        binding.dDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), endDateSetListener, (currentYear), (currentMonth), currentDay).show();
            }
        });


        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkProfile) {
                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                } else {
                    Toast.makeText(getContext(), "상대방의 프로필은 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return v;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            uri1 = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), uri1);
                // Log.d(TAG, String.valueOf(bitmap));
                Toast.makeText(getActivity().getApplicationContext() , "프로필 이미지 변경" , Toast.LENGTH_SHORT).show();
                mProgressDialog = new ProgressDialog(getContext());

                mProgressDialog.setMessage("업로드 중...");
                mProgressDialog.show();

                addUserInDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addUserInDatabase(){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] data = bytes.toByteArray();

        StorageReference storageRef = storage.getReference();
        StorageReference ImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

        UploadTask uploadTask = ImagesRef.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d("실패1", "실패");
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    downuri = downloadUri.toString();
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("profilePic", downuri);
                    uri = downuri;

                    if(user != null) {
                        db.collection("user").document(user.getUid()).update(docData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        mProgressDialog.dismiss();
                                        binding.profileImage.setImageBitmap(bitmap);
                                        binding.myProfile.setImageBitmap(bitmap);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity().getApplicationContext() , "프로필 이미지 변경실패" , Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                    Log.d("성공", "성공" + downloadUri);
                } else {
                    Log.d("실패2", "실패");
                }
            }
        });
    }

    private void initTabLayout() {
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        Log.d("Position", "0");
                        setInfo(user.getUid());
                        checkProfile = true;
                        break;
                    case 1:
                        Log.d("Position", "1");
                        setInfo(otherUid);
                        checkProfile = false;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setCalendar() {
        //시작일, 종료일 데이터 저장
        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = (calendar.get(Calendar.MONTH));
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    }


    private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            binding.dDay.setText(getDday(year, monthOfYear, dayOfMonth));
        }
    };

    private String getDday(int mYear, int mMonthOfYear, int mDayOfMonth) {

        // D-day 설정
        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(mYear, mMonthOfYear, mDayOfMonth);

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        long result = dday - today;

        setDay(dday);

        return formatDay(result);
    }

    private String formatDay(long result) {
        String strFormat;
        if (result > 0) {
            strFormat = "D-%d";
        } else if (result == 0) {
            strFormat = "Today";
        } else {
            result *= -1;
            strFormat = "D+%d";
        }

        return (String.format(strFormat, result));
    }



    private void setDay (long dday) {
        db.collection("user").document(user.getUid()).update("dDay", dday);
        db.collection("user").document(otherUid).update("dDay", dday);
    }



    private void setInfo(String uid) {
        DocumentReference docRef = db.collection("user").document(uid);
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
                        uri = map.get("profilePic").toString();

                        Picasso.get().load(Uri.parse(uri)).into(binding.profileImage);
                        binding.name.setText(map.get("name").toString());
                        binding.nickname.setText("닉네임 : " + map.get("nickname").toString());
                        binding.phoneNumber.setText("휴대폰 번호 : " + map.get("phone").toString());
                        binding.birth.setText("생일 : " + map.get("birth").toString());
                        if(map.get("dDay") != null) {
                            final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
                            long result = Long.parseLong(map.get("dDay").toString())  - today;
                            binding.dDay.setText(formatDay(result));
                        }

                        if(uid.equals(user.getUid())) {
                            if(!map.get("otherUid").toString().equals("")) {
                                Log.d("Check", "Check");
                                otherUid = map.get("otherUid").toString();
                                myProfileUrl = uri;
                                Picasso.get().load(Uri.parse(myProfileUrl)).into(binding.myProfile);
                                setOtherProfile();
                            }
                        }
                        Log.d(TAG, map.get("check").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setOtherProfile() {
        DocumentReference doc = db.collection("user").document(otherUid);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Map<String, Object> map = document.getData();
                        otherProfileUrl = map.get("profilePic").toString();
                        Picasso.get().load(Uri.parse(otherProfileUrl)).into(binding.otherProfile);
                    }
                }
            }
        });
    }

}