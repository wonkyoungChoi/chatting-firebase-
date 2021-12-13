package com.example.chatting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.chatting.databinding.ActivityLoginBinding;
import com.example.chatting.databinding.IntroBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Intro extends AppCompatActivity {

    IntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = IntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loading.setVisibility(View.VISIBLE);
        binding.loading.playAnimation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

        },1800);
    }

}
