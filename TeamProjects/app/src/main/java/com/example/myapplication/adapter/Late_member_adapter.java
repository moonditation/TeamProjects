package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemLateListBinding;
import com.example.myapplication.databinding.ItemLateMemberBinding;
import com.example.myapplication.late_memory.late_list_click;
import com.example.myapplication.make_prom.received_promise_info;

import java.util.List;

public class Late_member_adapter extends RecyclerView.Adapter<Late_member_adapter.LateMemberViewHolder>{

    private List<User> userList;

    public Late_member_adapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public Late_member_adapter.LateMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemLateMemberBinding binding = ItemLateMemberBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new Late_member_adapter.LateMemberViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(Late_member_adapter.LateMemberViewHolder holder, int position) {
        User name = userList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class LateMemberViewHolder extends RecyclerView.ViewHolder {
        private ItemLateMemberBinding binding;

        private LateMemberViewHolder(ItemLateMemberBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(User text){binding.userName.setText(text.name);}
    }
}

