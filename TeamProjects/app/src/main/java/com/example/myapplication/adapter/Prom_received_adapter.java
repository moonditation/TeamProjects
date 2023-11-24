package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemLateListBinding;
import com.example.myapplication.databinding.ItemPromiseRequestBinding;

import java.util.List;

public class Prom_received_adapter extends RecyclerView.Adapter<Prom_received_adapter.ReceivedPromViewHolder>{

    private List<Promise> promiseList;

    public Prom_received_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public Prom_received_adapter.ReceivedPromViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPromiseRequestBinding binding = ItemPromiseRequestBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);
        return new Prom_received_adapter.ReceivedPromViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(Prom_received_adapter.ReceivedPromViewHolder holder, int position) {
        Promise name = promiseList.get(position);
        holder.bind(name);
    }

    @Override
    public int getItemCount() {
        return promiseList.size();
    }

    public static class ReceivedPromViewHolder extends RecyclerView.ViewHolder {
        private ItemPromiseRequestBinding binding;

        private ReceivedPromViewHolder(ItemPromiseRequestBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(Promise text){binding.promiseName.setText(text.name);}
    }
}

