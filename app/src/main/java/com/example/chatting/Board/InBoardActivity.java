package com.example.chatting.Board;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatting.databinding.ActivityInboardBinding;
import com.squareup.picasso.Picasso;

public class InBoardActivity extends AppCompatActivity {

    Intent intent;
    String title, time, picture, text;

    ActivityInboardBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();

        title = getIntent().getStringExtra("title");
        text = getIntent().getStringExtra("text");
        time = getIntent().getStringExtra("time");
        picture = getIntent().getStringExtra("picture");

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


}