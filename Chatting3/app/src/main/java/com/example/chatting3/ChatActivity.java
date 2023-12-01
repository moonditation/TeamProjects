package com.example.chatting3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter adapter;

    TextView chatRoomNameTextView;

    private static final int VIEW_TYPE_MY_MESSAGE = 0;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setTitle("Using FirestoreRecyclerAdapter");

        Query query = FirebaseFirestore.getInstance()
                .collection("promises")
                .document("chatDoc")
                .collection("chats")
                .orderBy("timestamp")
                .limit(50);

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Chat, ChatHolder>(options) {
            @Override
            protected void onBindViewHolder(ChatHolder holder, int position, Chat model) {
                holder.bind(model);
            }

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

            @Override
            public int getItemViewType(int position) {
                Chat chat = getItem(position);

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String messageUid = chat.getUserId();

                if (currentUserUid.equals(messageUid)) {
                    return VIEW_TYPE_MY_MESSAGE;
                } else {
                    return VIEW_TYPE_OTHER_MESSAGE;
                }
            }
        };

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setAdapter(adapter);

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
                        db.collection("usersExample")
                                .document(senderUid)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot != null) {
                                                String senderName = documentSnapshot.getString("name");

                                                Chat chatItem = new Chat(senderName, message, senderUid);

                                                db.collection("promises").document("chatDoc").collection("chats")
                                                        .document("MSG_" + System.currentTimeMillis())
                                                        .set(chatItem)
                                                        .addOnSuccessListener(documentReference -> {
                                                            // 추가 성공
                                                            Log.d("LJM", "DocumentSnapshot added");
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // 추가 실패
                                                            Log.w("LJM", "Error adding document", e);
                                                        });

                                                messageText.setText("");
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

        chatRoomNameTextView = findViewById(R.id.chatRoomName);

        // 채팅방 이름 바꾸려고 예시로 promises - promiseInfo 에 필드로 name 하나 넣어놓음
        addData();
        // promiseInfo에 필드 name을 가져와서 채팅방 이름으로 설정하는 메서드
        fetchChatRoomNameFromFirestore();

    }

    // 예시 데이터
    public void addData() {
        Map<String, Object> Info = new HashMap<>();
        Info.put("name", "약속 1");
        FirebaseFirestore.getInstance().collection("promises").document("promiseInfo")
                .set(Info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("LJM", "데이터 저장 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LJM", "데이터 저장 실패");
                    }
                });
    }

    private void fetchChatRoomNameFromFirestore() {
        // 'promises' 컬렉션에서 문서 가져오기 (예: 문서 ID는 'yourDocumentId'로 대체)
        FirebaseFirestore.getInstance().collection("promises")
                .document("promiseInfo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null) {
                                String chatRoomName = documentSnapshot.getString("name");
                                setChatRoomName(chatRoomName);
                            }
                        }
                    }
                });
    }

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