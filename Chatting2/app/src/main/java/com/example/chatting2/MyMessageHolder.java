//package com.example.chatting2;
//
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class MyMessageHolder extends RecyclerView.ViewHolder{
//    private final TextView tvName;
//    private final TextView tvMsg;
//    private final TextView tvTime;
//
//    public MyMessageHolder(@NonNull View itemView) {
//        super(itemView);
//        tvName = itemView.findViewById(R.id.tv_name);
//        tvMsg = itemView.findViewById(R.id.tv_msg);
//        tvTime = itemView.findViewById(R.id.tv_time);
//    }
//
//    public void bind(MessageItem model) {
//        tvName.setText(model.getName());
//        tvMsg.setText(model.getMessage());
//        tvTime.setText(model.getTime().toString());
//    }
//}
