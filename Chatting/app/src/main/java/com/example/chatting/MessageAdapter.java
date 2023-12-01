package com.example.chatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    Context context;
    ArrayList<MessageItem> messageItems;

    final int TYPE_MY=0; // 내 채팅일 때
    final int TYPE_OTHER=1; // 상대방 채팅일 때

    //생성자를 이용해 context와 메시지 아이템들을 넣음
    public MessageAdapter(Context context, ArrayList<MessageItem> messageItems) {
        this.context = context;
        this.messageItems = messageItems;
    }

    //리사이클러뷰의 아이템뷰가 경우에 따라 다른 모양으로 보여야 할 때 사용하는 콜백 메소드가 있다 : getItemViewType
    //이 메소드에서 해당 position에 따른 식별값(ViewType 번호)를 정하여 리턴하면
    //그 값이 onCreateViewHolder() 메소드의 두번째 파라미터에 전달됨
    //onCreateViewHolder() 메소드 안에서 그 값에 따라 다른 xml 문서를 inflate 하면된다
    @Override
    public int getItemViewType(int position) {
        if(messageItems.get(position).name.equals(G.nicname)) {
            //내가 쓴 글
            return TYPE_MY;
        } else {
            return TYPE_OTHER;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //두 레이아웃 중 뭘 넣어야할지 몰라 우선 null 참조
        //파이어베이스에 저장된 name이 내 static name에 있는 것과 같으면 내거 아님 상대방거임
        //두번째 파라미터 int viewType을 사용해서 분기처리 해보자
        //타입은 낸 맘대로 정할 수 있음
        View itemView = null;
        //내 채팅일 때
        if(viewType == TYPE_MY) itemView = LayoutInflater.from(context).inflate(R.layout.my_messagebox,parent,false);
        //상대방 채팅일 때
        else itemView = LayoutInflater.from(context).inflate(R.layout.other_messagebox,parent,false);

        //카톡 날짜 구분선도 이 타입으로 구분한것임

        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MessageItem item = messageItems.get(position);

        holder.tvName.setText(item.name);
        holder.tvMsg.setText(item.message);
        holder.tvTime.setText(item.time);
//        Glide.with(context).load(item.profileUrl).into(holder.civ);
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    class VH extends RecyclerView.ViewHolder {

        //메세지 타입에 따라 뷰가 다름 바인딩 클래스를 고정 할 수 없다 (뷰가 두개라 누굴 써야할지 모르것다,,)
        //MyMessageboxBinding binding;
        //OtherMessageboxBinding binding2;

        //ViewHolder를 2개 만들어 사용하기도함 [MyVH, OtherVH]
        //홀더를 두개 만들면 onBinding할때도 분기 처리해야해서 이번에는 뷰 바인드 안쓰고 제작
//        CircleImageView civ;
        TextView tvName;
        TextView tvMsg;
        TextView tvTime;

        public VH(@NonNull View itemView) {
            super(itemView);
            //xml 의 id가 같아야 함
//            civ = itemView.findViewById(R.id.civ);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMsg = itemView.findViewById(R.id.tv_msg);
            tvTime = itemView.findViewById(R.id.tv_time);

        }
    }
}