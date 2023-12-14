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

    private FirestoreRecyclerAdapter adapter;
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

        chatRoomNameTextView = findViewById(R.id.chatRoomName);
        chatRoomName = getIntent().getStringExtra("chatRoomName");
        setChatRoomName(chatRoomName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = FirebaseFirestore.getInstance()
                .collection("ChatRooms")
                .document(chatRoomName)
                .collection("Message")
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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (!message.isEmpty()) {
                    if (currentUser != null) {
                        String senderUid = currentUser.getUid();
                        db.collection("users")
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

                                                db.collection("ChatRooms").document(chatRoomName).collection("Message")
                                                        .document("MSG_" + System.currentTimeMillis())
                                                        .set(chatItem)
                                                        .addOnSuccessListener(documentReference -> {
                                                            Log.d("LJM", "DocumentSnapshot added");
                                                        })
                                                        .addOnFailureListener(e -> {
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


        backBtn = findViewById(R.id.chat_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack() {
        getSupportFragmentManager().popBackStack();
        finish();
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