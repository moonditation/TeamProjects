package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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

    public Friend_received_adapter(List<User> userList) {
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserUid = FirebaseAuth.getInstance().getUid();
    }


    @Override
    public friendReceiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        ItemFriendRequestBinding binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new friendReceiveViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(friendReceiveViewHolder holder, int position) {
        User name = userList.get(position);
        holder.bind(name);

        // 해당 유저의 정보를 가져와서 뷰홀더에 바인드
//        getSenderUid(holder);
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

}