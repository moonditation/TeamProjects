//package com.example.chatting2;
//
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class OtherMessageHolder extends RecyclerView.ViewHolder {
//    private final TextView tvName;
//    private final TextView tvMsg;
//    private final TextView tvTime;
//
//    public OtherMessageHolder(@NonNull View itemView) {
//        super(itemView);
//        // 상대방 메시지에 해당하는 UI 구성 요소 초기화
//        tvName = itemView.findViewById(R.id.tv_name);
//        tvMsg = itemView.findViewById(R.id.tv_msg);
//        tvTime = itemView.findViewById(R.id.tv_time);
//    }
//
//    // 데이터를 바인딩하는 메서드
//    public void bind(MessageItem model) {
//        // 각 UI 구성 요소에 모델의 데이터를 설정하는 로직 작성
//        tvName.setText(model.getName());
//        tvMsg.setText(model.getMessage());
//        tvTime.setText(model.getTime().toString());
//    }
//}
