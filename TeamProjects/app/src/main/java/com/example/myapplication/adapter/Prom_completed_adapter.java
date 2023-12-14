package com.example.myapplication.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.chat.ChatActivity;
import com.example.myapplication.databinding.ItemCompletePromBinding;
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

        holder.binding.completePromChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // 현재 위치(position)를 사용할 수 있음
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);

                    String chatRoomName = promiseList.get(position).getPromiseName();
                    intent.putExtra("chatRoomName", chatRoomName);

                    v.getContext().startActivity(intent);

                }

            }
        });



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
                String promiseUid = promiseList.get(position).getPromiseUid();
                Log.d("promiceaccept", promiseUid);
                Bundle bundle = new Bundle();
                bundle.putString("promiseUid", promiseUid);//종료할 수 있는 약속
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();

                //처리해야하는 클릭이벤트.
                complete_promise_info complete_promise_info = new complete_promise_info();
                complete_promise_info.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, complete_promise_info)
                        .addToBackStack(null)
                        .commit();
            });

        }
        private void bind(Promise text){binding.promiseName.setText(text.getPromiseName());}
    }
}