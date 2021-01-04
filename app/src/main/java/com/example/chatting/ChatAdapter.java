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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    static String nick;

    private static final String TAG = "ChatAdapter";

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView TextView_mynickname;
        public TextView TextView_mymsg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView_mynickname = itemView.findViewById(R.id.TextView_nickname);
            TextView_mymsg = itemView.findViewById(R.id.TextView_msg);

        }
    }

    static List<ChatData> chatData;

    public ChatAdapter(List<ChatData> items) {
        chatData = items;
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
        Log.d("position", String.valueOf(position));

        holder.setIsRecyclable(false);

        ChatData chat = chatData.get(position);

        holder.TextView_mynickname.setText(chat.getNickname());
        holder.TextView_mymsg.setText(chat.getMassage()); //DTO

        if(chat.getNickname()!=null && chat.getNickname().equals(nick)) {
            Log.d("chatNick", chat.getNickname());
            Log.d("myNick", nick);
            holder.TextView_mymsg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.TextView_mynickname.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            Log.d("WrongchatNick", chat.getNickname());
            Log.d("WrongmyNick", nick);
            holder.TextView_mymsg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.TextView_mynickname.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        return chatData.size();
    }


    public ChatData getChat(int position) {
        return chatData != null ? chatData.get(position) : null;
    }

    public void addChat(ChatData chat) {
        chatData.add(chat);
        notifyItemInserted(chatData.size()-1);
    }

}
