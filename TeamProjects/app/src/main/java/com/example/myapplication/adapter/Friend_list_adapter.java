package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.example.myapplication.databinding.ItemFriendRequestBinding;

import java.util.List;
public class Friend_list_adapter extends RecyclerView.Adapter<Friend_list_adapter.friendListViewHolder> {

    private List<User> userList;

    public Friend_list_adapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public friendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new friendListViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(friendListViewHolder holder, int position) {
        User name = userList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class friendListViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendBinding binding;

        private friendListViewHolder(ItemFriendBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(User text){
            binding.userName.setText(text.name);
            binding.userId.setText(text.id);}
    }
}