package com.example.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatting.Chatting.ChatAdapter;
import com.example.chatting.databinding.ActivityAddboardBinding;
import com.example.chatting.databinding.ActivityMemberInitBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MemberActivity extends AppCompatActivity {

    Button checkButton;
    ImageView showUserProfile;
    private static final String TAG = "MemberActivity";
    int PICK_IMAGE_REQUEST = 1;
    Uri uri, downloadUri;
    String downuri;
    Bitmap bitmap;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog mProgressDialog;
    ActivityMemberInitBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemberInitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.showUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });



        binding.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUpdate();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                Toast.makeText(this, "프로필 이미지 선택" , Toast.LENGTH_SHORT).show();
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("업로드 중...");
                mProgressDialog.show();
                binding.showUserProfile.setImageBitmap(bitmap);
                addUserInDatabse();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void addUserInDatabse(){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] data = bytes.toByteArray();

        StorageReference storageRef = storage.getReference();
        StorageReference ImagesRef = storageRef.child("users/" + user.getEmail() + "/profileImage.jpg");

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
                    mProgressDialog.dismiss();
                    Log.d("성공", "성공" + downloadUri);
                } else {
                    Log.d("실패2", "실패");
                }
            }
        });
    }


    private void profileUpdate() {
        String name = binding.name.getText().toString();
        String nickname = binding.nickname.getText().toString();
        String phone = binding.phone.getText().toString();
        String birth = binding.birthDay.getText().toString();

        if(name.length() > 0 && nickname.length() > 0 && phone.length() > 9  && birth.length() > 5 && downuri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    Member member = new Member(user.getEmail(), name, nickname, phone, birth, downuri, false, "", task.getResult(), "");

                    if(user != null) {
                        db.collection("user").document(user.getUid()).set(member)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startToast("프로필 등록을 성공하였습니다.");
                                        ChatAdapter.nick = nickname;
                                        myStartActivity(MainFragment.class);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                        startToast("프로필 등록에 실패하였습니다.");
                                    }
                                });
                    }
                }
            });

        } else {
            startToast("회원정보를 정확하게 입력해주세요." );
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        finish();
    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
