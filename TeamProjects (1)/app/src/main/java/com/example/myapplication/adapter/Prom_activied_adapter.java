package com.example.myapplication.adapter;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
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
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.list_prom.active_and_time_in_promise_info;
import com.example.myapplication.map.NaverMapRealNew;

import java.util.List;
public class Prom_activied_adapter extends RecyclerView.Adapter<Prom_activied_adapter.PromiseViewHolder> {

    private List<Promise> promiseList;
    private ItemActivePromBinding binding;
    private Context context;

    public Prom_activied_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public PromiseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemActivePromBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false);
        return new PromiseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(PromiseViewHolder holder, int position) {
        Promise name = promiseList.get(position);
        holder.bind(name);

        binding.activePromChatBtn.setOnClickListener(new View.OnClickListener() {
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

        binding.friendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // 현재 위치(position)를 사용할 수 있음
                    Intent intent = new Intent(v.getContext(), NaverMapRealNew.class);

                    String promiseUid = promiseList.get(position).getPromiseUid();
                    intent.putExtra("promiseUid", promiseUid);

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
        private ItemActivePromBinding binding;

        private PromiseViewHolder(ItemActivePromBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.activeEditPromise.setOnClickListener(view -> {
                // 얘는 분기점을 통해서, active_and_time_in_promise_info 자료 중 하나가 떠오를 것.
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                String promiseUid = promiseList.get(position).getPromiseUid();
                Log.d("promiceaccept", promiseUid);
                Bundle bundle = new Bundle();
                bundle.putString("promiseUid", promiseUid);//종료할 수 있는 약속
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();

                fragmentManager.setFragmentResult("promiseUidBundle", bundle);
                active_and_time_in_promise_info active_and_time_in_promise_info = new active_and_time_in_promise_info();
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, active_and_time_in_promise_info)
                        .addToBackStack(null)
                        .commit();
//                // 시간이 다 되지 않은 약속.
//                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
//                active_promise_info active_promise_info = new active_promise_info();
//                fragmentManager.beginTransaction()
//                        .add(R.id.frame_layout, active_promise_info)
//                        .addToBackStack(null)
//                        .commit();
            });
        }
        private void bind(Promise text){binding.promiseName.setText(text.getPromiseName());}
    }
}