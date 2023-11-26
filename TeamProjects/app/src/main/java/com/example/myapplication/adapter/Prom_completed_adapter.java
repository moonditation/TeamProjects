package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemCompletePromBinding;
import com.example.myapplication.list_prom.active_promise_info;
import com.example.myapplication.list_prom.complete_promise_info;

import java.util.List;
public class Prom_completed_adapter extends RecyclerView.Adapter<Prom_completed_adapter.PromiseViewHolder> {

    private List<Promise> promiseList;

    public Prom_completed_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public PromiseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCompletePromBinding binding = ItemCompletePromBinding.inflate(LayoutInflater.from(
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
        private ItemCompletePromBinding binding;

        private PromiseViewHolder(ItemCompletePromBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.completeEditPromise.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                Promise promise = promiseList.get(position);
                //처리해야하는 클릭이벤트.
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                complete_promise_info complete_promise_info = new complete_promise_info();
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, complete_promise_info)
                        .addToBackStack(null)
                        .commit();
            });
        }
        private void bind(Promise text){binding.promiseName.setText(text.name);}
    }
}