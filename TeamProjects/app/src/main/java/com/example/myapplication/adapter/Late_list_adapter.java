package com.example.myapplication.adapter;

import android.os.Bundle;
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

public class Late_list_adapter extends RecyclerView.Adapter<Late_list_adapter.LateListViewHolder>{

    private List<Promise> promiseList;

    public Late_list_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;
    }

    @Override
    public Late_list_adapter.LateListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemLateListBinding binding = ItemLateListBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false);
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

    public class LateListViewHolder extends RecyclerView.ViewHolder {
        private ItemLateListBinding binding;
        private ItemLateMemberBinding memberBinding;
        private LateListViewHolder(ItemLateListBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.lateProm.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                Promise promise = promiseList.get(position);

                String promiseDocumentUid = promise.getPromiseUid();

                Bundle bundle = new Bundle();
                bundle.putString("promiseUid", promiseDocumentUid);

                //처리해야하는 클릭이벤트.
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                late_list_click late_list_click = new late_list_click();
                late_list_click.setArguments(bundle); // 데이터 전달

                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, late_list_click)
                        .addToBackStack(null)
                        .commit();
            });
        }
        private void bind(Promise text){
            binding.lateProm.setText(text.getPromiseName());
//            memberBinding.booleanLate.setText("t");
//            memberBinding.userLateTime.setText("시간");
//            memberBinding.userLateMinute.setText("분");
        }
    }
}

