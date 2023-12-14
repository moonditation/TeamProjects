package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemLateListBinding;
import com.example.myapplication.databinding.ItemLateMemberBinding;
import com.example.myapplication.late_memory.late_list_click;
import com.example.myapplication.make_prom.received_promise_info;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Late_member_adapter extends RecyclerView.Adapter<Late_member_adapter.LateMemberViewHolder>{

    private List<User> userList;
    private Map<String, Date> userLateTimeMap;
    private Map<String, Boolean> userBoolArriveMap;

    private Date promiseDate;
    private String promiseUid;


    public Late_member_adapter(List<User> userList, String promiseUid) {
        loadPromiseDate(promiseUid);
        this.userList = userList;
        this.promiseUid =promiseUid;
        userLateTimeMap = new HashMap<>();
        userBoolArriveMap = new HashMap<>();
        loadUserArrivalStatus(promiseUid);

    }

    private void loadUserArrivalStatus(String promiseUid) {
        FirebaseFirestore.getInstance()
                .collection("promisesPractice")
                .document(promiseUid)
                .collection("friends")
                .get()
                .addOnSuccessListener(collectionSnapshot -> {
                    for (DocumentSnapshot document : collectionSnapshot.getDocuments()) {
                        String friendId = document.getString("friendId");
                        Boolean friendArrive = document.getBoolean("friendArrive");
                        Timestamp promiseTimestamp = document.getTimestamp("friendArriveTime");
                        Date date = promiseTimestamp.toDate();
                        Boolean checkBool = document.getBoolean("friendArrive");
                        userLateTimeMap.put(friendId, date);
                        userBoolArriveMap.put(friendId,checkBool );

                    }
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }

    private void loadPromiseDate(String promiseUid) {
        FirebaseFirestore.getInstance()
                .collection("promisesPractice")
                .document(promiseUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("latePromise","docuemet exist");
                        Timestamp promiseTimestamp = documentSnapshot.getTimestamp("promiseDate");
                        Log.d("latePromise",""+promiseTimestamp);
                        if (promiseTimestamp != null) {
                            this.promiseDate = promiseTimestamp.toDate();
                            Log.d("latePromise22",""+promiseDate);

                        } else {
                        }
                    } else {
                    }
                })
                .addOnFailureListener(e -> {
                });
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
        private void bind(User text){
            binding.userName.setText(text.name);
            String userId = text.getId();
            Date lateTime = userLateTimeMap.get(userId);
            Boolean boolArrive = userBoolArriveMap.get(userId);
            if (lateTime != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//                binding.userLateTime.setText(dateFormat.format(lateTime));
                Log.d("latePromise", lateTime+" "+promiseDate);
                if (lateTime.after(promiseDate) && boolArrive) {
                    binding.booleanLate.setText("지각");
                    long diff = lateTime.getTime() - promiseDate.getTime();
                    long diffMinutes = diff / (60 * 1000);

                    binding.userLateMinute.setText(diffMinutes + "분 늦게 도착");
                } else if(lateTime.after(promiseDate) && !boolArrive){
                    binding.booleanLate.setText("지각");
                    binding.userLateMinute.setText("30분 이상 지각!");

                }
                else {
                    binding.booleanLate.setText("제 시간에 도착");
                    binding.userLateMinute.setText( "제 시간에 도착");

                }




            }







        }
    }
}

