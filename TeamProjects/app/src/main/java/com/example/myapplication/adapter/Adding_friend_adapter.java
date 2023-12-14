package com.example.myapplication.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentClickMakePromiseBinding;
import com.example.myapplication.databinding.ItemFriendAddBinding;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.example.myapplication.databinding.ItemFriendRequestBinding;
import com.example.myapplication.initial_screen.MainActivity;
import com.example.myapplication.make_prom.added_friend;
import com.example.myapplication.make_prom.click_make_promise;
import com.example.myapplication.make_prom.received_promise_info;

import java.util.ArrayList;
import java.util.List;


// 내 페이지 코드로 바꿈
public class Adding_friend_adapter extends RecyclerView.Adapter<Adding_friend_adapter.AddingFriendViewHolder> {

    private List<User> userList;
    private ArrayList<String> uidList = new ArrayList<>();
    private ItemFriendAddBinding binding;

    public Adding_friend_adapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public AddingFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemFriendAddBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new AddingFriendViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(AddingFriendViewHolder holder, int position) {
        User name = userList.get(position);
        holder.bind(name);

        binding.friendAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 띄워진 리싸이클러뷰 item이다.
                // 클릭을 통해서, added_friend 즉, 함꼐할 친구 목록에 넣어준다.
                // 만약, 이미 담긴 파티원이라면, 토스트문구와 함께 담기지 않게 된다.

                if (!isAlreadyAdded(name.getUid())) {
                    uidList.add(name.getUid());
                    Bundle bundle = new Bundle();

                    bundle.putStringArrayList("uidList", uidList);

                    added_friend fragment = new added_friend();
                    fragment.setArguments(bundle);

                    ((MainActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.added_friend_list, fragment)
                            .commit();

                    Log.d("LJM", "친구추가됨");
                    Toast.makeText(view.getContext(), "추가", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("LJM", "이미 추가됨");
                    Toast.makeText(view.getContext(), "이미 추가된 친구입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isAlreadyAdded(String userId) {
        return uidList.contains(userId);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filterList(ArrayList<User> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }

    public class AddingFriendViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendAddBinding binding;

        private AddingFriendViewHolder(ItemFriendAddBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        private void bind(User text) {
            binding.userName.setText(text.name);
            binding.userId.setText(text.id);
        }
    }
}

// 이종민 합병 이전 코드

//package com.example.myapplication.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.R;
//import com.example.myapplication.databinding.ItemFriendAddBinding;
//import com.example.myapplication.databinding.ItemFriendBinding;
//import com.example.myapplication.databinding.ItemFriendRequestBinding;
//import com.example.myapplication.make_prom.received_promise_info;
//
//import java.util.ArrayList;
//import java.util.List;
//public class Adding_friend_adapter extends RecyclerView.Adapter<Adding_friend_adapter.AddingFriendViewHolder> {
//
//    private List<User> userList;
//
//    public Adding_friend_adapter(List<User> userList) {
//        this.userList = userList;
//    }
//
//    @Override
//    public AddingFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(
//                        parent.getContext()),
//                        parent,
//                        false);
//        return new AddingFriendViewHolder(binding);
//    }
//
//
//    @Override
//    public void onBindViewHolder(AddingFriendViewHolder holder, int position) {
//        User name = userList.get(position);
//        holder.bind(name);
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList.size();
//    }
//
//    public void filterList(ArrayList<User> filteredList){
//        userList = filteredList;
//        notifyDataSetChanged();
//    }
//    public class AddingFriendViewHolder extends RecyclerView.ViewHolder {
//        private ItemFriendBinding binding;
//
//        private AddingFriendViewHolder(ItemFriendBinding binding){
//            super(binding.getRoot());
//            this.binding = binding;
//
//            binding.userInfo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // 띄워진 리싸이클러뷰 item이다.
//                    // 클릭을 통해서, added_friend 즉, 함꼐할 친구 목록에 넣어준다.
//                    // 만약, 이미 담긴 파티원이라면, 토스트문구와 함께 담기지 않게 된다.
//                    Toast.makeText(view.getContext(), "추가", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//        private void bind(User text){
//            binding.userName.setText(text.name);
//            binding.userId.setText(text.id);}
//    }
//}