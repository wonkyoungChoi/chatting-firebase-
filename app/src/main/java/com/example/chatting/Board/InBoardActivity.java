package com.example.chatting.Board;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatting.MainFragment;
import com.example.chatting.R;
import com.example.chatting.databinding.ActivityInboardBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class InBoardActivity extends AppCompatActivity {

    Intent intent;
    String title, time, picture, text, key, timeKey;

    ActivityInboardBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActionBar();

        intent = getIntent();

        timeKey = getIntent().getStringExtra("timeKey");
        title = getIntent().getStringExtra("title");
        text = getIntent().getStringExtra("text");
        time = getIntent().getStringExtra("time");
        picture = getIntent().getStringExtra("picture");
        key = getIntent().getStringExtra("key");

        binding.title.setText(title);
        binding.text.setText(text);
        binding.time.setText(time);

        Picasso.get().load(Uri.parse(picture)).into(binding.imageHeader);


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initActionBar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inboard_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.update:
                Intent intent = new Intent(getApplicationContext(), AddBoard.class);
                intent.putExtra("key", key);
                intent.putExtra("title", title);
                intent.putExtra("text", text);
                intent.putExtra("time", time);
                intent.putExtra("picture", picture);
                intent.putExtra("timeKey", timeKey);
                startActivity(intent);
                finish();
                break;
            case R.id.delete:
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(key).document(timeKey).delete();
                Toast.makeText(getApplicationContext(), "게시글 삭제완료", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}