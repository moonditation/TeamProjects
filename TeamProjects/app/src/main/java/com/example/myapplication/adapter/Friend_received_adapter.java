package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.example.myapplication.databinding.ItemFriendRequestBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class Friend_received_adapter extends RecyclerView.Adapter<Friend_received_adapter.friendReceiveViewHolder> {

    private List<User> userList;

    private List<DocumentSnapshot> friendRequestList;
    private String currentUserUid;
    private FirebaseFirestore db;
    ItemFriendRequestBinding binding;

    public Friend_received_adapter(List<User> userList) {
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserUid = FirebaseAuth.getInstance().getUid();
    }


    @Override
    public friendReceiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new friendReceiveViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(friendReceiveViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User name = userList.get(position);
        holder.bind(name);

        binding.requestAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. db에서 요청 필드의 status="accepted"로 업데이트
                acceptFriendRequest(position);
            }
        });

        binding.requestRejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderUid = userList.get(position).getUid();
                rejectFriendRequest(senderUid, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class friendReceiveViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendRequestBinding binding;

        private friendReceiveViewHolder(ItemFriendRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(User text) {
            binding.userNameRequest.setText(text.name);
            binding.userIdRequest.setText(text.id);
        }
    }

    private void acceptFriendRequest(int position) {
        String senderUid = userList.get(position).getUid();

        // 1. DB에서 요청 필드의 status="accepted"로 업데이트
        db.collection("friendRequests")
                .whereEqualTo("senderUid", senderUid)
                .whereEqualTo("receiverUid", currentUserUid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference requestRef = document.getReference();
                            requestRef.update("status", "accepted")
                                    .addOnSuccessListener(aVoid -> {
                                        // 2. status="accepted" 일 때, users 컬렉션의 내 friends에 추가
                                        Log.d("FriendRequest", "status=accepted로 업데이트 완료");
                                        addSenderToMyFriendsCollection(senderUid, position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FriendRequest", "Error accepting friend request", e);
                                    });
                        }
                    } else {
                        Log.e("FriendRequest", "Error getting friend request document", task.getException());
                    }
                });
    }

    private void addSenderToMyFriendsCollection(String senderUid, int position) {
        db.collection("users")
                .document(senderUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDoc = task.getResult();
                        if (userDoc.exists()) {
                            String senderName = userDoc.getString("name");
                            String senderId = userDoc.getString("id");
                            double senderReliabilitydouble = userDoc.getDouble("reliability");
                            float senderReliability = (float)senderReliabilitydouble;

                            Log.d("sender", senderName); // 문관록 잘 들어옴

                            User userFriend = new User(senderName, senderId, senderReliability, senderUid);

                            db.collection("users")
                                    .document(currentUserUid)
                                    .collection("friends")
                                    .document(senderUid)
                                    .set(userFriend)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FriendRequest", "내 friends 컬렉션에 상대방 정보 추가 성공");
                                        addMyInfoToSenderFriendsCollection(senderUid, position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("FriendRequest", "friends 컬렉션에 정보 추가 실패");
                                    });
                        }
                    }
                });
    }

    private void addMyInfoToSenderFriendsCollection(String senderUId, int position) {
        db.collection("users")
                .document(currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot userDoc = task.getResult();
                        if (userDoc.exists()) {
                            String currentUserName = userDoc.getString("name");
                            String currentUserId = userDoc.getString("id");
                            double currentUserReliabilitydouble = userDoc.getDouble("reliability");
                            float currentUserReliability = (float)currentUserReliabilitydouble;

                            User userFriend = new User(currentUserName, currentUserId, currentUserReliability, currentUserUid);

                            db.collection("users")
                                    .document(senderUId)
                                    .collection("friends")
                                    .document(currentUserUid)
                                    .set(userFriend)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FriendRequest", "상대방 friends 컬렉션에 내 정보 추가 성공");
                                        Toast.makeText(binding.getRoot().getContext(), "친구 요청을 수락했습니다." ,Toast.LENGTH_SHORT).show();
                                        deleteFriendRequest(senderUId, position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("FriendRequest", "friends 컬렉션에 정보 추가 실패");
                                    });
                        }
                    }
                });
    }

    private void deleteFriendRequest(String senderUid, int position) {
        // 3. 요청 DB에서 문서를 삭제 -> 리사이클러뷰에서 삭제
        db.collection("friendRequests")
                .whereEqualTo("senderUid", senderUid)
                .whereEqualTo("receiverUid", currentUserUid)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // 리사이클러뷰에서 해당 아이템 삭제
                                        userList.remove(position);
                                        notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> Log.e("FriendRequest", "Error deleting friend request", e));
                        }
                    } else {
                        Log.e("FriendRequest", "Error getting friend request document", task.getException());
                    }
                });
    }

    private void rejectFriendRequest(String senderUid, int position) {
        // 3. 요청 DB에서 문서를 삭제 -> 리사이클러뷰에서 삭제
        db.collection("friendRequests")
                .whereEqualTo("senderUid", senderUid)
                .whereEqualTo("receiverUid", currentUserUid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // 리사이클러뷰에서 해당 아이템 삭제
                                        Toast.makeText(binding.getRoot().getContext(), "요청을 거절했습니다.", Toast.LENGTH_SHORT).show();
                                        userList.remove(position);
                                        notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> Log.e("FriendRequest", "Error deleting friend request", e));
                        }
                    } else {
                        Log.e("FriendRequest", "Error getting friend request document", task.getException());
                    }
                });
    }

}