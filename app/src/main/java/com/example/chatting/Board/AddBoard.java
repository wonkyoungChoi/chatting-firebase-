package com.example.chatting.Board;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatting.MainFragment;
import com.example.chatting.R;
import com.example.chatting.databinding.ActivityAddboardBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddBoard extends AppCompatActivity {
    private ActivityAddboardBinding binding;
    Uri uri, downloadUri;
    String downuri;
    Bitmap bitmap;
    ProgressDialog mProgressDialog;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int PICK_IMAGE_REQUEST = 1;

    long now;

    FirebaseFirestore db;

    String key, in_title, in_text, in_time, in_picture, in_timeKey;
    String title, text, time;

    Boolean update = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        key = getIntent().getStringExtra("key");

        boardUpdateSet();



        now = System.currentTimeMillis();

        binding.addImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        binding.dateMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }


    public void setDate(View view){
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                // Date Picker에서 선택한 날짜를 TextView에 설정
                binding.date.setText(String.format("%d-%d-%d", yy,mm+1,dd));
                binding.date.setTextColor(getResources().getColor(R.color.black));
            }
        };
        // DATE Picker가 처음 떴을 때, 오늘 날짜가 보이도록 설정.
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, mDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
    }

    private void addPictureInDatabse(){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] data = bytes.toByteArray();

        StorageReference storageRef = storage.getReference();
        StorageReference ImagesRef = storageRef.child("board/" + now + "/image.jpg");

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

    private void boardUpdateSet() {
        if(getIntent().getStringExtra("title") != null) {
            in_title = getIntent().getStringExtra("title");
            in_text = getIntent().getStringExtra("text");
            in_picture = getIntent().getStringExtra("picture");
            in_time = getIntent().getStringExtra("time");
            in_timeKey = getIntent().getStringExtra("timeKey");

            binding.addboardTitle.setText(in_title);
            binding.addboardText.setText(in_text);
            binding.date.setText(in_time);
            binding.date.setTextColor(getResources().getColor(R.color.black));
            Picasso.get().load(Uri.parse(in_picture)).into(binding.addImageView);
            downuri = getIntent().getStringExtra("picture");

            update = true;

            Log.d("===key", in_timeKey);
            Log.d("===picture", in_picture);
        }
    }



    private void uploadData() {
        title = binding.addboardTitle.getText().toString();
        text = binding.addboardText.getText().toString();
        time = binding.date.getText().toString();

        Log.d("title", title);
        Log.d("text", text);
        Log.d("time", time);
        Log.d("downuri", downuri);

        if(title.length() > 0 && text.length() > 0 && time.length() > 8 && downuri != null) {

            db = FirebaseFirestore.getInstance();

            if(update) {
                boardUpdate();
            } else {
                boardUpload();
            }

        } else {
            startToast("스토리를 정확하게 입력해주세요." );
        }
    }


    private void boardUpdate() {
        Board board = new Board(key, title, time, text, downuri, in_timeKey);
        db.collection(key).document(in_timeKey).set(board)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("스토리 수정을 성공하였습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("스토리 등록에 실패하였습니다.");
                    }
                });
    }


    private void boardUpload() {
        Board board = new Board(key, title, time, text, downuri, String.valueOf(now));
        db.collection(key).document(String.valueOf(now)).set(board)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("스토리 등록을 성공하였습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("스토리 등록에 실패하였습니다.");
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
                Toast.makeText(this, "스토리 이미지 선택" , Toast.LENGTH_SHORT).show();
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("업로드 중...");
                mProgressDialog.show();
                binding.addImageView.setImageBitmap(bitmap);
                addPictureInDatabse();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}