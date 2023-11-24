package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemLateListBinding;

import java.util.List;

public class Late_list_adapter extends RecyclerView.Adapter<Late_list_adapter.LateListViewHolder>{

    private List<Promise> promiseList;

    public Late_list_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public Late_list_adapter.LateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemLateListBinding binding = ItemLateListBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new Late_list_adapter.LateListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(Late_list_adapter.LateListViewHolder holder, int position) {
        Promise name = promiseList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return promiseList.size();
    }

    public static class LateListViewHolder extends RecyclerView.ViewHolder {
        private ItemLateListBinding binding;

        private LateListViewHolder(ItemLateListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(Promise text){binding.lateProm.setText(text.name);}
    }
}

