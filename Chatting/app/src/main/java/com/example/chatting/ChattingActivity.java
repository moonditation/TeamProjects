package com.example.chatting;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatting.databinding.ActivityChattingBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {

    ActivityChattingBinding binding;

    //채팅방 시작하자마자 아이템 하나 붙잡을 테니 전역변수로
    FirebaseFirestore firestore;
    CollectionReference chatRef;

    //채팅방 이름
    String chatName ="myChat";

    //리사이클러뷰에 넣어줄 아이템
    ArrayList<MessageItem> messageItems = new ArrayList<>();
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //제목줄에 채팅방 이름 표시
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(chatName);
            actionBar.setSubtitle("상대방 이름");
        }


//        //아답터 연결
        messageAdapter = new MessageAdapter(this, messageItems);
        binding.recycler.setAdapter(messageAdapter);
//
//
//        //파리어베이스 파이어 스토어 관리객체 및 (채팅방 이름)참조 객체 소환
//        //채팅기록 남기기 위한 DB 생성
//        //collection = 채팅방명
//        //도큐먼트 = 시간
//        //필드 : 칭팅정보 (사진, 이름, 메세지 ,시간)
        firestore = FirebaseFirestore.getInstance();
        chatRef = firestore.collection(chatName);
//
//
//        //채팅방이름으로 된 컬렉션에 저장되어 있는 데이터들 읽어오기
//        //chatRef의 데이터가 변경될때마다 반응하는 리스너 달기 : get()은 일회용
//        chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() { //데이터가 바뀔떄마다 찍음
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                //데이터가 바뀔때마다 그냥 add하면 그 순간의 모든것을 찍어 가져오기 때문에 중복되어버림
//                //따라서 변경된 Document만 찾아달라고 해야함
//                //1. 바뀐 애들 찾온다 - 왜 리스트인가? 처음 시작할 때 문제가 됨 그래서 여러개라고 생각함
//                List<DocumentChange> documentChanges =value.getDocumentChanges();
//
//                for(DocumentChange documentChange:documentChanges){
//                    //2.변경된 문서내역의 데이터를 촬영한 DocumentSnapshot얻어오기
//                    DocumentSnapshot snapshot = documentChange.getDocument();
//
//                    //3.Document에 있는 필드값 가져오기
//                    Map<String, Object> msg = snapshot.getData();
//                    String name = msg.get("name").toString();
//                    String message = msg.get("message").toString();
////                    String profileUrl = msg.get("profileUrl").toString();
//                    String time = msg.get("time").toString();
//
//                    //4.읽어온 메세지를 리스트에 추가
//                    messageItems.add(new MessageItem(name,message,time));
//
//                    //5.아답터에게 데이터가 추가 되었다고 공지 -> 해야 화면 갱신됨
//                    messageAdapter.notifyItemInserted(messageItems.size()-1);
//                    // notifyDataSetChanged() : 여러개가 한번에 여러개 바뀌었을 때
//                    //notifyItemRangeInserted() : 현재 for문 안에서 하나 바뀔때 마다 알려주면 됨
//                    //notifyItemRangeInserted(바뀐위치)
//                    //바뀐위치는 마지막 번호 = messageItems.size()-1
//
//                    //리사이클러뷰의 스크롤위치 가장 아래로 이동
//                    binding.recycler.scrollToPosition(messageItems.size()-1);
//
//                }
//
//                //Toast.makeText(ChattingActivity.this, ""+messageItems.size(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
    }
//        binding.btn.setOnClickListener(v-> clickSend());
//
//    }
//
//    private void clickSend() {
//
//        //파이어베이스 디비에 저장할 데이터들 준비 (사진, 이름 메세지 시간)
//        String nickName = G.nicname;
//        String message = binding.et.getText().toString();
////        String profileUrl = G.profileUrl;
//        //메세지를 작성 시간을 문자열 [시:분]
//        Calendar calendar = Calendar.getInstance();
//        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":"+calendar.get(Calendar.MINUTE);
//
//        //필드에 넣을 값을 MessageItem 객체로 만들어서 한방에 입력
//        //필드값을 객체로 만들어 저장하자 : 리사이클러뷰에 넣기 위해
//        MessageItem item = new MessageItem(nickName,message,time);
//
//        //'채팅방이름' 컬렉션에 채팅 메세지들을 저장
//        // 단 시간 순으로 정렬되도록 도큐먼트의 이름은 현재시간(밀리세컨드)로 지정
//        chatRef.document("MSG_"+ System.currentTimeMillis()).set(item);
//
//        //다음 메세지를 입력이 수월하도록 EditText에 있는 글씨 삭제
//        binding.et.setText("");
//
//        //키보드 내리기
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
//        //hideSoftInputFromWindow(포커스 받은 editText, 0 : 바로 종료 )
//
//    }
}
