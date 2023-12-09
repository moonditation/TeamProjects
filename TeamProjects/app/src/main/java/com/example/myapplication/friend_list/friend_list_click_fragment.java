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
import com.example.myapplication.adapter.Friend_list_adapter;
import com.example.myapplication.adapter.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class friend_list_click_fragment extends Fragment {
    private FirebaseFirestore db;
    private String currentUserId;
    private Friend_list_adapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list_click, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_friend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터와 데이터 연결
        List<User> dataList = generateData(); // 데이터 생성
        adapter = new Friend_list_adapter(dataList);
        recyclerView.setAdapter(adapter);
    }


    // 수정
    private List<User> generateData() {
        List<User> dataList = new ArrayList<>();
        getFriendInfo(dataList);

        return dataList;
    }

    private void getFriendInfo(List<User> dataList) {
        db.collection("users")
                .document(currentUserId)
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                String friendName = document.getString("name");
                                String friendId = document.getString("id");
                                String friendUid = document.getString("uid");

                                User user = new User(friendName, friendId, friendUid);

                                Log.d("FriendList", "친구 정보 추가");
                                dataList.add(user);
                                adapter.notifyDataSetChanged();

                            }
                        } else {
                            Log.d("FriendList", "친구 정보를 불러오기에 실패했습니다.");
                        }
                    }
                });
    }
}