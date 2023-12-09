package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;

import java.util.List;
public class Prom_make_added_friend extends RecyclerView.Adapter<Prom_make_added_friend.addedFriendViewHolder> {

    private List<String> userList;

    public Prom_make_added_friend(List<String> userList) {
        this.userList = userList;
    }

    @Override
    public addedFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new addedFriendViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(addedFriendViewHolder holder, int position) {
        String name = userList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class addedFriendViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendBinding binding;

        private addedFriendViewHolder(ItemFriendBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(String text){
            binding.userName.setText(text);
//            binding.userId.setText(text.id);
        }
    }
}