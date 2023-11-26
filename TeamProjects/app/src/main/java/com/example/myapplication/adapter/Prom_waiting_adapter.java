package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemWaitingPromBinding;
import com.example.myapplication.list_prom.complete_promise_info;
import com.example.myapplication.list_prom.waiting_promise_info;

import java.util.List;
public class Prom_waiting_adapter extends RecyclerView.Adapter<Prom_waiting_adapter.PromiseViewHolder> {

    private List<Promise> promiseList;

    public Prom_waiting_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public PromiseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemWaitingPromBinding binding = ItemWaitingPromBinding.inflate(LayoutInflater.from(
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

    public class PromiseViewHolder extends RecyclerView.ViewHolder {
        private ItemWaitingPromBinding binding;

        private PromiseViewHolder(ItemWaitingPromBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.waitingEditPromise.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                Promise promise = promiseList.get(position);
                //처리해야하는 클릭이벤트.
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                waiting_promise_info waiting_promise_info = new waiting_promise_info();
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, waiting_promise_info)
                        .addToBackStack(null)
                        .commit();
            });
        }
        private void bind(Promise text){binding.promiseName.setText(text.name);}
    }
}