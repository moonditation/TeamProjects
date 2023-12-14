package com.example.myapplication.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.databinding.ItemWaitingPromBinding;
import com.example.myapplication.list_prom.complete_promise_info;
import com.example.myapplication.list_prom.waiting_promise_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Prom_waiting_adapter extends RecyclerView.Adapter<Prom_waiting_adapter.PromiseViewHolder> {

    private List<Promise> promiseList;
    FirebaseFirestore db;
    String myUid;

    public Prom_waiting_adapter(List<Promise> promiseList) {
        this.promiseList = promiseList;

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myUid = currentUser.getUid();

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
        String promiseUid = promiseList.get(position).getPromiseUid();
        holder.bind(name);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("promisesPractice").document(promiseUid).collection("friends");
        docRef.whereEqualTo("friendUid", myUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            boolean friendAccept = document.getBoolean("friendAccept");
                            // friendAccept 필드가 true인 경우
                            if (friendAccept) {
                                holder.binding.acceptWaitingButton.setEnabled(false);
                                holder.binding.acceptWaitingButton.setText("수락완료");
                            }
                            else{

                            }
                        }
                    } else {
                        Log.e("Firestore", "Error getting friend documents", task.getException());
                    }
                });
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

            binding.acceptWaitingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //이 position으로 id 파악해서 자료 뽑을거임
                    String promiseUid = promiseList.get(position).getPromiseUid();
                    DocumentReference docRef = db.collection("promisesPractice").document(promiseUid);
                    CollectionReference friendsCollectionRef = docRef.collection("friends");


                    friendsCollectionRef.whereEqualTo("friendUid", myUid).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            for (QueryDocumentSnapshot friendDocument : task2.getResult()) {
                                friendsCollectionRef.document(friendDocument.getId()).update("friendAccept", true)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "Friend accept updated successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error updating friend accept", e);
                                        });
                            }
                        } else {
                            Log.e("Firestore", "Error getting friend documents", task2.getException());
                        }
                    });

                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                int currentAcceptCount = document.getLong("promiseAcceptPeople") != null ?
                                        Objects.requireNonNull(document.getLong("promiseAcceptPeople")).intValue() : 0;

                                int updatedAcceptCount = currentAcceptCount + 1;

                                docRef.update("promiseAcceptPeople", updatedAcceptCount)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "Document updated successfully");

                                            binding.acceptWaitingButton.setEnabled(false);
                                            binding.acceptWaitingButton.setText("수락완료");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error updating document", e);
                                        });
                            } else {
                                Log.d("Firestore", "Document does not exist");
                            }
                        } else {
                            Log.e("Firestore", "Error getting document", task.getException());
                        }
                    });


                }
            });



            binding.waitingEditPromise.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                String promiseUid = promiseList.get(position).getPromiseUid();
                Log.d("promiceaccept", promiseUid);
                Bundle bundle = new Bundle();
                bundle.putString("promiseUid", promiseUid);
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();


                waiting_promise_info waiting_promise_info = new waiting_promise_info();
                waiting_promise_info.setArguments(bundle);


                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, waiting_promise_info)
                        .addToBackStack(null)
                        .commit();
            });
        }
        private void bind(Promise text){binding.promiseName.setText(text.getPromiseName());}
    }
}