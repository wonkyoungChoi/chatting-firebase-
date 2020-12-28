package com.example.chatting;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatData> mDataset;
    private String myNickname;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView TextView_nickname;
        public TextView TextView_msg;
        public View rootView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView_nickname = itemView.findViewById(R.id.TextView_nickname);
            TextView_msg = itemView.findViewById(R.id.TextView_msg);
            rootView = itemView;
        }
    }

    public ChatAdapter(List<ChatData> myDataset, String myNickname) {
        mDataset = myDataset;
        this.myNickname = myNickname;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatitem, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatData chat = mDataset.get(position);

        holder.TextView_nickname.setText(chat.getNickname());
        holder.TextView_msg.setText(chat.getMassage()); //DTO


        if(chat.getNickname() != null && chat.getNickname().equals(ChatActivity.nick)) {
           holder.TextView_msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
           holder.TextView_nickname.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            holder.TextView_msg.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.TextView_nickname.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return mDataset == null ? 0 : mDataset.size();
    }

    public ChatData getChat(int position) {
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(ChatData chat) {
        mDataset.add(chat);
        notifyItemInserted(mDataset.size()-1); //갱신
    }
}
