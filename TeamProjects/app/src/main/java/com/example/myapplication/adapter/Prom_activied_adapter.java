package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemActivePromBinding;

import java.util.List;
public class Prom_activied_adapter extends RecyclerView.Adapter<Prom_activied_adapter.PromiseViewHolder> {

    private List<Promise> promiseList;

    public Prom_activied_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public PromiseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemActivePromBinding binding = ItemActivePromBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false);
        return new PromiseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PromiseViewHolder holder, int position) {
        Promise name = promiseList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return promiseList.size();
    }

    public static class PromiseViewHolder extends RecyclerView.ViewHolder {
        private ItemActivePromBinding binding;

        private PromiseViewHolder(ItemActivePromBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(Promise text){binding.promiseName.setText(text.name);}
    }
}