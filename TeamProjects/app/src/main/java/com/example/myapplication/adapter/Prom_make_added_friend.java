package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
public class Prom_make_added_friend extends RecyclerView.Adapter<Prom_make_added_friend.addedFriendViewHolder> {

    private List<String> uidList;
    private FirebaseFirestore db;

    public Prom_make_added_friend(List<String> uidList) {
        this.uidList = uidList;
        this.db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public addedFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new addedFriendViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull addedFriendViewHolder holder, int position) {
        String uid = uidList.get(position);
        getUserDetails(uid, holder);
    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    public static class addedFriendViewHolder extends RecyclerView.ViewHolder {
        private final ItemFriendBinding binding;

        public addedFriendViewHolder(ItemFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String name, String id) {
            binding.userName.setText(name);
            binding.userId.setText(id);
        }
    }

    private void getUserDetails(String uid, addedFriendViewHolder holder) {
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String id = document.getString("id");

                            // ViewHolder에 데이터 바인딩
                            holder.bind(name, id);
                        }
                    }
                });
    }
}