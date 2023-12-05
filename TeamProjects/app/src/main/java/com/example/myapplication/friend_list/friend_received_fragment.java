package com.example.myapplication.friend_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Friend_received_adapter;
import com.example.myapplication.adapter.Prom_make_added_friend;
import com.example.myapplication.adapter.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class friend_received_fragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Friend_received_adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_received_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // 프래그먼트 2의 종료 이벤트 처리
        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_received_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터와 데이터 연결
        List<User> dataList = generateData(); // 데이터 생성

        adapter = new Friend_received_adapter(dataList);

        recyclerView.setAdapter(adapter);

    }

    private List<User> generateData() {
        List<User> dataList = new ArrayList<>();
        getSenderUid(dataList);
        return dataList;
    }
    private void getSenderUid(List<User> dataList) {
        db.collection("friendRequests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 각 문서에서 senderUid 필드 값을 가져오기
                            String senderUid = document.getString("senderUid");
                            String receiverUid = document.getString("receiverUid");
                            String pending =  document.getString("status");

                            db.collection("users")
                                    .document(senderUid)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            if (document2.exists()) {
                                                // 가져온 정보를 User 객체에 매핑
                                                if (Objects.equals(receiverUid, mAuth.getUid())) {


                                                String userName = document2.getString("name");
                                                String userId = document2.getString("id");
                                                String userUid = document2.getString("uid");

                                                User user = new User(
                                                        userName,
                                                        userId,
                                                        userUid
                                                );

                                                Log.d("MGR", userName + userId);
                                                dataList.add(user);
                                                adapter.notifyDataSetChanged();
                                                ;
                                            }

                                            } else {
                                                // 문서가 존재하지 않을 경우 처리
                                                Log.d("LJM", "문서가 존재하지 않음");
                                            }
                                        } else {
                                            // 작업 실패 시 처리
                                            Log.d("LJM", "작업 실패");
                                        }
                                    });

                        }
                    }
                });
    }
}