    package com.example.chatting2;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.chatting2.databinding.ActivityChattingBinding;
    import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
    import com.firebase.ui.firestore.FirestoreRecyclerOptions;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.Firebase;
    import com.google.firebase.Timestamp;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentChange;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.EventListener;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.FirebaseFirestoreException;
    import com.google.firebase.firestore.Query;
    import com.google.firebase.firestore.QuerySnapshot;

    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Map;

    public class ChattingActivity extends AppCompatActivity {

        private static final int VIEW_TYPE_MY_MESSAGE = 0;
        private static final int VIEW_TYPE_OTHER_MESSAGE = 1;
        private FirestoreRecyclerAdapter adapter;
        private RecyclerView recyclerView;
        private EditText messageInput;
        private Button btnSendMessage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chatting);

            setTitle("Chat Application");


            Query query = FirebaseFirestore.getInstance()

                    .collection("chats")
                    .orderBy("timestamp")
                    .limit(50);

            FirestoreRecyclerOptions<MessageItem> options = new FirestoreRecyclerOptions.Builder<MessageItem>()
                    .setQuery(query, MessageItem.class)
                    .build();


            adapter = new FirestoreRecyclerAdapter<MessageItem, MyViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MessageItem model) {
                    holder.bind(model);
                }

                @NonNull
                @Override
                public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(android.R.layout.simple_list_item_2, parent, false);

                    return new MyViewHolder(view);
                }

            };
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

//            btnSendMessage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    sendMessage();
//                }
//            });

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


//        private void sendMessage() {
//            messageInput = findViewById(R.id.et);
//            String messageText = messageInput.getText().toString().trim();
//
//            if (!messageText.isEmpty()) {
//                sendMessageToFirebase(messageText);
//
//                etMessageInput.setText("");
//
//
//            }
//        }
//
//        // 메시지를 전송할 때 파이어베이스로 데이터를 전달하는 메서드
//        private void sendMessageToFirebase(String messageText) {
//            //uid를 얻기 위한 auth
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = auth.getCurrentUser();
//
//            if (currentUser != null) {
//                String userId = currentUser.getUid();
//                // 내 nickname(name) 정보를 얻기 위한 firestore 연결
//                FirebaseFirestore.getInstance()
//                        // *컬렉션 이름 로그인할 때랑 일치하도록 만들기!!!!!!!!!*
//                        .collection("usersExample")
//                        .document(userId)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @SuppressLint("NotifyDataSetChanged")
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                if (documentSnapshot.exists()) {
//                                    String userName = documentSnapshot.getString("name");
//                                    Timestamp timestamp = new Timestamp(new Date());
//
//                                    MessageItem messageItem = new MessageItem(userName, messageText, userId, timestamp.toDate());
//
//                                    FirebaseFirestore.getInstance()
//                                            .collection("promises")
//                                            .document("chatDoc")
//                                            .collection("chats")
//                                            .add(messageItem)
//                                            .addOnSuccessListener(documentReference -> {
//                                                // 메시지 전송 성공
//                                                adapter.notifyDataSetChanged();
//                                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
////                                                if (userId.equals(messageItem.getUid())) {
////                                                    View view = LayoutInflater.from(ChattingActivity.this)
////                                                            .inflate(R.layout.my_messagebox, null);
////                                                    MyMessageHolder myMessageHolder = new MyMessageHolder(view);
////                                                    myMessageHolder.bind(messageItem);
////                                                } else {
////                                                    View view = LayoutInflater.from(ChattingActivity.this)
////                                                            .inflate(R.layout.other_messagebox, null);
////                                                    MyMessageHolder myMessageHolder = new MyMessageHolder(view);
////                                                    myMessageHolder.bind(messageItem);
////                                                }
//                                            })
//                                            .addOnFailureListener(e -> {
//                                                // 메시지 전송 실패
//                                                Log.e("chat", "Error sending data to Firestore", e);
//
//                                                Toast.makeText(ChattingActivity.this, "Failed to send data", Toast.LENGTH_SHORT).show();
//                                            });
//                                } else {
//                                    // 사용자 정보가 없을 때 로그인 화면으로 돌려보내기(필요한가?)
//                                    Intent loginIntent = new Intent(ChattingActivity.this, MainActivity.class);
//                                    startActivity(loginIntent);
//                                }
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            // Firestore에서 데이터를 가져오지 못한 경우
//                            Log.e("chat", "Error fetching data from Firestore", e);
//
//                            Toast.makeText(ChattingActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//                        });
//
//            }

    }
