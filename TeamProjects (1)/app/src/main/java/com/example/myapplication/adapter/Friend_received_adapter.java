package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.example.myapplication.databinding.ItemFriendRequestBinding;

import java.util.List;
public class Friend_received_adapter extends RecyclerView.Adapter<Friend_received_adapter.friendReceiveViewHolder> {

    private List<User> userList;

    public Friend_received_adapter(List<User> userList) {
        this.userList = userList;
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class friendReceiveViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendRequestBinding binding;

        private friendReceiveViewHolder(ItemFriendRequestBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(User text){
            binding.userNameRequest.setText(text.name);
            binding.userIdRequest.setText(text.id);}
    }

    public void addUser(User user) {
        // 클릭한 사용자 정보를 받아와서 뷰바인딩 및 인플레이트하는 작업 수행
        // 예시: 새로운 레이아웃을 인플레이트하고 사용자 정보를 설정하는 작업
        // 인플레이트한 뷰를 리사이클러뷰에 추가
        userList.add(user);
        notifyDataSetChanged();
    }
}