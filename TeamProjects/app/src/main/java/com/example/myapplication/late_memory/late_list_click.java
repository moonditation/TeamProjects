package com.example.myapplication.late_memory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Late_list_adapter;
import com.example.myapplication.adapter.Late_member_adapter;
import com.example.myapplication.adapter.Promise;
import com.example.myapplication.adapter.User;
import com.example.myapplication.databinding.FragmentAddingFriendBinding;
import com.example.myapplication.databinding.FragmentLateListClickBinding;
import com.example.myapplication.databinding.ItemLateListBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class late_list_click extends Fragment {
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_late_list_click, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_late_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (bundle != null) {
            String promiseDocumentUid = bundle.getString("promiseUid");
            db
                    .collection("promisesPractice")
                    .document(promiseDocumentUid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String promiseName = documentSnapshot.getString("promiseName");
                            // 가져온 promiseName 값을 TextView에 설정

                            TextView promiseNameTextView = view.findViewById(R.id.promise_name);
                            promiseNameTextView.setText(promiseName);

                            Timestamp promiseTimestamp = documentSnapshot.getTimestamp("promiseDate");
                            if (promiseTimestamp != null) {
                                // 가져온 타임스탬프를 Date 객체로 변환
                                Date date = promiseTimestamp.toDate();

                                // 날짜와 시간 형식 지정
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                                // 형식에 맞게 문자열로 변환하여 TextView에 설정
                                TextView promiseDateTimeTextView = view.findViewById(R.id.promise_date_time);
                                promiseDateTimeTextView.setText(dateFormat.format(date));
                            }

                            long peopleNum = documentSnapshot.getLong("promiseAcceptPeople");
                            TextView promisePeopleNumTextView = view.findViewById(R.id.promise_people_number);
                            promisePeopleNumTextView.setText(peopleNum + "");


                        } else {
                            // 문서가 존재하지 않을 때 처리
                        }
                    });

            db
                    .collection("promisesPractice")
                    .document(promiseDocumentUid)
                    .collection("friends")
                    .get()
                    .addOnSuccessListener(collectionSnapshot -> {
                        //모든 friends 컬렉션 안에 있는 문서들에서 friendName필드랑 friendId 필드의 값을 받아와 밑에 형식대로 만들어줘
                        List<User> dataList = new ArrayList<>();

                        for (DocumentSnapshot document : collectionSnapshot.getDocuments()) {
                            // 친구 이름
                            String friendName = document.getString("friendName");

                            // 친구 ID
                            String friendId = document.getString("friendId");

                            // 지각한 친구인지 여부
                            String friendUid = document.getString("friendUid");


                            User user = new User(friendName, friendId, friendUid);

                            // 리스트 추가
                            dataList.add(user);

                        }

                        Late_member_adapter adapter = new Late_member_adapter(dataList, promiseDocumentUid);
                        recyclerView.setAdapter(adapter);

                    });


        }

        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }
}