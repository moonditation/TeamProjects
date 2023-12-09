package com.example.myapplication.make_prom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Prom_completed_adapter;
import com.example.myapplication.adapter.Prom_make_added_friend;
import com.example.myapplication.adapter.Promise;
import com.example.myapplication.adapter.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class added_friend extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_added_friend, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String documentUid = getArguments().getString("documentUid");


        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_added_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d("woohyuk", "여기까진 ok22222");

        // 어댑터와 데이터 연결
        List<String> dataList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference friendsCollectionRef = db.collection("promisesPractice")
                .document(documentUid)
                .collection("friends");

        friendsCollectionRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String friendUid = document.getString("friendUid");
                            if (friendUid != null) {
                                dataList.add(friendUid);
                            }
                        }
                        // 어댑터와 데이터 연결
                        Prom_make_added_friend adapter = new Prom_make_added_friend(dataList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void generateData() {
        getParentFragmentManager().setFragmentResultListener("bundle", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String documentUid= result.getString("documentUid");
                Log.d("documentID", documentUid);

                FirebaseFirestore.getInstance()
                        .collection("promisesPractice")
                        .document(documentUid)
                        .collection("friends")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<String> dataList = new ArrayList<>(); // 데이터 리스트 생성
                                for (DocumentSnapshot friendSnapshot : task.getResult()) {
                                    String friendName = friendSnapshot.getString("friendUid");
                                    if (friendName != null) {
                                        dataList.add(friendName); // 데이터 추가
                                    }
                                }

                                // RecyclerView 어댑터 설정
                                Prom_make_added_friend adapter = new Prom_make_added_friend(dataList);
                                RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_added_friend);
                                recyclerView.setAdapter(adapter);
                            } else {
                                // 데이터 가져오기 실패 처리
                            }
                        });
            }
        });
    }

}