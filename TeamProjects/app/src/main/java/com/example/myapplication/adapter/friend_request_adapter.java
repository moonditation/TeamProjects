package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.example.myapplication.databinding.ItemFriendRequestBinding;
import com.example.myapplication.make_prom.received_promise_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class friend_request_adapter extends RecyclerView.Adapter<friend_request_adapter.friendRequestViewHolder> {

    private List<User> userList;

    private ItemFriendBinding binding;

    public friend_request_adapter(List<User> userList) {
        this.userList = userList;
    }

    FirebaseFirestore db;

    @Override
    public friendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemFriendBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new friendRequestViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(friendRequestViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User name = userList.get(position);
        holder.bind(name);

        binding.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String senderUid = currentUser.getUid();
                    String receiverUid = userList.get(position).getUid();
                    checkIfFriendsEachOther(senderUid, receiverUid, position);
                }
            }
        });

    }

    // 상대방에게서 이미 요청이 와있는 상태인지 확인
    private void checkIfFriendRequestAlreadyArrived(String senderUid, String receiverUid, int position) {
        db.collection("friendRequests")
                .whereEqualTo("senderUid", receiverUid)
                .whereEqualTo("receiverUid", senderUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // 이미 친구 요청을 보냈음
                            Toast.makeText(binding.getRoot().getContext(), userList.get(position).getName() + "님에게 친구 요청이 와있습니다.", Toast.LENGTH_LONG).show();
                            Log.d("FriendRequest", "이미 친구 요청을 받음");
                        } else {
                            // 친구 요청을 아직 보내지 않음
                            checkIfFriendRequestExists(senderUid, receiverUid, position);
                        }
                    } else {
                        // 쿼리 실패
                        Toast.makeText(binding.getRoot().getContext(), "친구 요청을 확인하는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // 친구 요청을 보내는 메서드
    private void sendFriendRequest(String senderUid, String receiverUid, int position) {
        FriendRequest friendRequest = new FriendRequest(senderUid, receiverUid, "pending");
        db.collection("friendRequests")
                .add(friendRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(binding.getRoot().getContext(), userList.get(position).getName() + "님에게 친구 요청을 보냈습니다.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(binding.getRoot().getContext(), "친구 요청 보내기를 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }
    // 내가 이미 보낸 요청인지 확인하는 코드
    private void checkIfFriendRequestExists(String senderUid, String receiverUid, int position) {
        db.collection("friendRequests")
                .whereEqualTo("senderUid", senderUid)
                .whereEqualTo("receiverUid", receiverUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // 이미 친구 요청을 보냈음
                            Toast.makeText(binding.getRoot().getContext(), "이미 친구 요청을 보냈습니다.", Toast.LENGTH_LONG).show();
                            Log.d("FriendRequest", "이미 친구 요청을 보냄");
                        } else {
                            // 친구 요청을 아직 보내지 않음
                            sendFriendRequest(senderUid, receiverUid, position);
                        }
                    } else {
                        // 쿼리 실패
                        Toast.makeText(binding.getRoot().getContext(), "친구 요청을 확인하는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // senderUid가 나, receiverUid가 상대방
    private void checkIfFriendsEachOther(String senderUid, String receiverUid, int position) {
        Log.d("CheckFriends", senderUid);
        db.collection("users")
                .document(senderUid)
                .collection("friends")
                .whereEqualTo("uid", receiverUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // 이미 친구인 경우
                        Log.d("FriendRequest", "이미 친구입니다.");
                        Toast.makeText(binding.getRoot().getContext(), "이미 친구입니다.", Toast.LENGTH_LONG).show();
                    } else {
                        // 친구가 아닌 경우
                        // 여기서 추가적으로 친구 요청을 보내거나 다른 동작 수행 가능
                        Log.d("FriendRequest", "친구가 아닙니다.");
                        checkIfFriendRequestAlreadyArrived(senderUid, receiverUid, position);
                    }
                });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filterList(ArrayList<User> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }

    public class friendRequestViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendBinding binding;

        private friendRequestViewHolder(ItemFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        private void bind(User text) {
            binding.userName.setText(text.name);
            binding.userId.setText(text.id);
        }
    }
}