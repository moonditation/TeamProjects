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
import com.example.myapplication.databinding.ItemPromiseRequestBinding;
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
                        // 문서 가져오기 실패 처리
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
                                            // friendAccept를 true로 업데이트한 경우
                                            Log.d("Firestore", "Friend accept updated successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                            // 업데이트 실패 시 처리
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
                                            // 업데이트가 성공한 경우
                                            Log.d("Firestore", "Document updated successfully");

                                            // 버튼을 비활성화하려면 해당 버튼 객체를 사용해서 setEnabled(false)를 호출하세요.
                                            binding.acceptWaitingButton.setEnabled(false);
                                            binding.acceptWaitingButton.setText("수락완료");
                                        })
                                        .addOnFailureListener(e -> {
                                            // 업데이트가 실패한 경우
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
                bundle.putString("promiseUid", promiseUid);//종료할 수 있는 약속
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();

                // 대기 중인 프라그먼트에 번들 전달

                //처리해야하는 클릭이벤트.
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

    public class PromiseFalseViewHolder extends RecyclerView.ViewHolder {
        private ItemPromiseRequestBinding binding;

        private PromiseFalseViewHolder(ItemPromiseRequestBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.requestPromise.setOnClickListener(view -> {
                int position = getAdapterPosition();
                //이 position으로 id 파악해서 자료 뽑을거임
                String promiseUid = promiseList.get(position).getPromiseUid();
                Log.d("promiceaccept", promiseUid);
                Bundle bundle = new Bundle();
                bundle.putString("promiseUid", promiseUid);//종료할 수 있는 약속
                FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();

                fragmentManager.setFragmentResult("promiseUidBundle", bundle);
                //처리해야하는 클릭이벤트.
                waiting_promise_info waiting_promise_info = new waiting_promise_info();
                fragmentManager.beginTransaction()
                        .add(R.id.frame_layout, waiting_promise_info)
                        .addToBackStack(null)
                        .commit();
            });

            binding.requestAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            binding.requestDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
        private void bind(Promise text){binding.promiseName.setText(text.getPromiseName());}
    }

}