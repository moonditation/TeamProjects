package com.example.chatting2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvName;
    private final TextView tvMsg;
//    final TextView tvTime;
    public MyViewHolder(@NonNull View itemView) {
        super (itemView);
        tvName = itemView.findViewById(android.R.id.text1);
        tvMsg = itemView.findViewById(android.R.id.text2);
//        tvTime = itemView.findViewById(R.id.tv_time);
    }

    public void bind(@NonNull MessageItem model) {
        tvName.setText(model.getName());
        tvMsg.setText(model.getMessage());
//        tvTime.setText(model.getTime().toString());
    }

}
