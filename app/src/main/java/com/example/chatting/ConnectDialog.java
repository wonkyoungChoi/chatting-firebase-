package com.example.chatting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatting.databinding.ConnectDialogBinding;

public class ConnectDialog extends Dialog {

    private Context context;
    private ConnectDialogClickListener listener;
    ConnectDialogBinding binding;

    public ConnectDialog(@NonNull Context context, ConnectDialogClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ConnectDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.connectCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCheckClick();
            }
        });

        binding.yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveClick();
                dismiss();
            }
        });

        binding.noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeClick();
                dismiss();
            }
        });
    }

}
