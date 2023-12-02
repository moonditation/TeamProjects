package com.example.myapplication.adapter;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;
public class friend_request_adapter extends RecyclerView.Adapter<friend_request_adapter.friendRequestViewHolder> {

    private List<User> userList;

    public interface OnItemClickListener {
        void onItemClick(String name, String id);
    }

    OnItemClickListener onItemClickListener;

    public friend_request_adapter(List<User> userList, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public friendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new friendRequestViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(friendRequestViewHolder holder, int position) {
        User name = userList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filterList(ArrayList<User> filteredList){
        userList = filteredList;
        notifyDataSetChanged();
    }
    public class friendRequestViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendBinding binding;

        private friendRequestViewHolder(ItemFriendBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.userInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        private void bind(User text){
            binding.userName.setText(text.name);
            binding.userId.setText(text.id);}
    }
}