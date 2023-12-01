package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemLateListBinding;
import com.example.myapplication.databinding.ItemPromiseRequestBinding;
import com.example.myapplication.list_prom.complete_promise_info;
import com.example.myapplication.make_prom.received_promise_info;

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

    public class ReceivedPromViewHolder extends RecyclerView.ViewHolder {
        private ItemPromiseRequestBinding binding;

        private ReceivedPromViewHolder(ItemPromiseRequestBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.requestPromise.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                Promise promise = promiseList.get(position);
                //처리해야하는 클릭이벤트.
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                received_promise_info received_promise_info = new received_promise_info();
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, received_promise_info)
                        .addToBackStack(null)
                        .commit();
            });
        }
        private void bind(Promise text){binding.promiseName.setText(text.name);}
    }
}

