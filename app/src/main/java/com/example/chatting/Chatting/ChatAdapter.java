package com.example.chatting.Chatting;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatting.databinding.ChatitemBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    public static String nick;
    private static final String TAG = "ChatAdapter";
    private ChatitemBinding mBinding;

    String key = ChattingFragment.key;
    public ChatData chat;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ChatitemBinding bind;
        public MyViewHolder(ChatitemBinding binding) {
            super(binding.getRoot());
            bind = binding;
        }
    }

    public List<ChatData> chatData = new ArrayList<>();

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = ChatitemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(mBinding);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        chat = chatData.get(position);
        //Log.d(TAG + "chat1", chat.getProfilePic());
        key = chat.getKey();

        String time = changeDate((long) chat.getTimestamp());

        Log.d("position", String.valueOf(position));
        if(chat.getNickname()!=null && chat.getNickname().equals(nick)) {
            mBinding.otherTime.setVisibility(View.GONE);
            mBinding.otherchatprofile.setVisibility(View.GONE);
            mBinding.TextViewOthernickname.setVisibility(View.GONE);
            mBinding.TextViewOthermsg.setVisibility(View.GONE);
            mBinding.TextViewMymsg.setVisibility(View.VISIBLE);
            mBinding.TextViewMymsg.setText(chat.getMessage());

            if(position < getItemCount()-1) {
                if (changeDate((long) chatData.get(position + 1).getTimestamp()).equals(time) && chatData.get(position + 1).getNickname().equals(nick)) {
                    mBinding.myTime.setVisibility(View.GONE);
                } else {
                    mBinding.myTime.setVisibility(View.VISIBLE);
                    mBinding.myTime.setText(time);
                }
            } else if(position == getItemCount()-1) {
                mBinding.myTime.setVisibility(View.VISIBLE);
                mBinding.myTime.setText(time);
            }

        } else {
            Picasso.get().load(Uri.parse(chat.getProfilePic())).into(mBinding.otherImage);
            mBinding.myTime.setVisibility(View.GONE);
            mBinding.TextViewMymsg.setVisibility(View.GONE);
            mBinding.TextViewOthernickname.setText(chat.getNickname());


            if(position != 0 && chat.getNickname().equals(chatData.get(position-1).getNickname())) {
                mBinding.otherchatprofile.setVisibility(View.INVISIBLE);
                mBinding.TextViewOthernickname.setVisibility(View.GONE);
            }

            mBinding.TextViewOthermsg.setVisibility(View.VISIBLE);
            mBinding.TextViewOthermsg.setText(chat.getMessage());

            if(position < getItemCount()-1) {
                if (changeDate((long) chatData.get(position + 1).getTimestamp()).equals(time)) {
                    mBinding.otherTime.setVisibility(View.GONE);
                } else {
                    mBinding.otherTime.setVisibility(View.VISIBLE);
                    mBinding.otherTime.setText(time);
                }
            } else if(position == getItemCount()-1) {
                mBinding.otherTime.setVisibility(View.VISIBLE);
                mBinding.otherTime.setText(time);
            }

        }


    }

    private String changeDate(long timestamp) {
        Date date = new Date(timestamp);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return simpleDateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public void addChat(ChatData chat) {
        chatData.add(chat);
        notifyDataSetChanged();
    }

}
