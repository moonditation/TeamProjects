package com.example.myapplication.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // FirestoreRecyclerAdapter
    private FirestoreRecyclerAdapter adapter;
    // 채팅방 이름 넣을 TextView 가져오는 변수
    TextView chatRoomNameTextView;
    String chatRoomName;
    ImageButton backBtn;

    // 뷰 타입에 따라서 내 화면일 때 0, 다른 사람일 때 1
    private static final int VIEW_TYPE_MY_MESSAGE = 0;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 채팅방 이름 받을 chatRoomNameTextView
        chatRoomNameTextView = findViewById(R.id.chatRoomName);
        chatRoomName = getIntent().getStringExtra("chatRoomName");
        setChatRoomName(chatRoomName);

        // 파이어스토어 객체 얻어오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 쿼리 연결할 때, 채팅이 들어가는 곳까지 연결해주기 -> 그렇기 때문에 중간에 문서들은 식별할 수 있는 것으로 넣는 것이 좋음
        Query query = FirebaseFirestore.getInstance()
                .collection("ChatRooms")
                .document(chatRoomName)
                .collection("Message")
                .orderBy("timestamp")
                .limit(50);

        // 그 쿼리를 옵션으로 새로운 빌더 객체를 생성
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class) // 쿼리와 채팅할 때 사용할 아이템 클래스를 넘겨줌
                .build();

        // 아답터 생성
        adapter = new FirestoreRecyclerAdapter<Chat, ChatHolder>(options) {

            // 뷰홀더와 바인딩 해줌
            @Override
            protected void onBindViewHolder(ChatHolder holder, int position, Chat model) {
                holder.bind(model);
            }

            // viewType을 getItemViewType으로부터 넘겨받아 viewType에 따라서 다른 레이아웃을 인플레이트 함
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == VIEW_TYPE_OTHER_MESSAGE) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.other_message, parent, false);
                    return new ChatHolder(view);
                }
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_message, parent, false);
                return new ChatHolder(view);
            }

            // 현재 로그인 해있는 사용자일 때에는 viewType을 VIEW_TYPE_MY_MESSAGE로, 아님 VIEW_TYPE_OTHER_MESSAGE로 넘겨줌
            @Override
            public int getItemViewType(int position) {
                Chat chat = getItem(position);
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String messageUid = chat.getUserId();
                // 현제 로그인중인 사용자의 uid와 가장 최근에 올라온 채팅을 보낸 사람의 uid를 비교
                if (currentUserUid.equals(messageUid)) {
                    return VIEW_TYPE_MY_MESSAGE;
                } else {
                    return VIEW_TYPE_OTHER_MESSAGE;
                }
            }
        };

        // 리사이클러뷰와 아답터 연결
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setAdapter(adapter);
        // 메시지 쓰는 messageText, 전송버튼인 sendBtn
        EditText messageText = findViewById(R.id.editMessage);
        Button sendBtn = findViewById(R.id.sendBtn);

        // 메시지 전송하면서 파이어베이스에 정보 올리기
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (!message.isEmpty()) {
                    // 사용자 이름, uid는 임시
                    if (currentUser != null) {
                        String senderUid = currentUser.getUid();
                        // 회원가입할 때, usersExample 컬렉션의 필드에 name을 가져와 보내는 사람의 정보에 넣는다.
                        db.collection("users")
                                .document(senderUid) // usersExample 컬렉션의 document 이름은 사용자의 Uid로 해놓음.
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot != null) {
                                                String senderName = documentSnapshot.getString("name");

                                                Chat chatItem = new Chat(senderName, message, senderUid);

                                                db.collection("ChatRooms").document(chatRoomName).collection("Message")
                                                        .document("MSG_" + System.currentTimeMillis()) // 시간 순서대로 채팅 document들을 나열
                                                        .set(chatItem)
                                                        .addOnSuccessListener(documentReference -> {
                                                            // 추가 성공
                                                            Log.d("LJM", "DocumentSnapshot added");
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // 추가 실패
                                                            Log.w("LJM", "Error adding document", e);
                                                        });

                                                messageText.setText(""); // 채팅 보내고 내서 EditText 초기화
                                            }
                                        } else {
                                            Log.d("LJM", "No such document");
                                        }
                                    }
                                });
                    }
                }
            }
        });


        //뒤로가기 버튼
        backBtn = findViewById(R.id.chat_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    // 뒤로가기
    private void goBack() {
        getSupportFragmentManager().popBackStack();
        finish();
    }

    // 채팅방 이름 설정
    private void setChatRoomName(String newName) {
        if (newName != null) {
            chatRoomNameTextView.setText(newName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}