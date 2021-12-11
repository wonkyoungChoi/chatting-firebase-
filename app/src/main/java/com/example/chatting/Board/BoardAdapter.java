package com.example.chatting.Board;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatting.databinding.BoardListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.MyViewHolder> {
    public List<Board> items = new ArrayList<>();
    private BoardListItemBinding mBinding;



    public class MyViewHolder extends RecyclerView.ViewHolder{
        BoardListItemBinding bind;
        public MyViewHolder(BoardListItemBinding binding) {
            super(binding.getRoot());
            bind = binding;
        }
    }

    @NonNull
    @Override
    public BoardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = BoardListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(mBinding);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("position", String.valueOf(position));

        holder.setIsRecyclable(false);
        Board board = items.get(position);


        mBinding.time.setText(board.getTime());
        mBinding.title.setText(board.getTitle());
        mBinding.text.setText(board.getText());
        Picasso.get().load(Uri.parse(board.getPicture())).into(mBinding.picture);

        mBinding.layoutBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("===Click", "CLICK");
                Intent intent = new Intent(mBinding.layoutBoard.getContext(), InBoardActivity.class);

                String title = board.getTitle();
                String text = board.getText();
                String time = board.getTime();
                String picture = board.getPicture();

                intent.putExtra("title", title);
                intent.putExtra("text", text);
                intent.putExtra("time", time);
                intent.putExtra("picture", picture);


                mBinding.layoutBoard.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addBoard(Board board) {
        items.add(board);
    }



}

