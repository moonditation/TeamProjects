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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_added_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d("woohyuk", "여기까진 ok22222");

        // 어댑터와 데이터 연결
        List<String> dataList = generateData(); // 데이터 생성
        Prom_make_added_friend adapter = new Prom_make_added_friend(dataList);
        recyclerView.setAdapter(adapter);
    }

    private List<String> generateData() {
        List<String> dataList = new ArrayList<>();
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
                                for (DocumentSnapshot friendSnapshot : task.getResult()) {
                                    String friendName = friendSnapshot.getString("friendName");
//                                    if (friendName != null) {
//                                        User user = new User(friendName); // User 객체 생성
//                                        // 다른 필드들도 필요하다면 여기서 설정
//
//                                        dataList.add(user); // 리스트에 추가
//                                    }
                                    dataList.add(friendName); // 리스트에 추가

                                }
                                // 리스트에 데이터가 모두 추가되었음을 확인할 수 있습니다.

                            } else {
                                // 데이터 가져오기 실패 처리
                            }
                        });
            }
        });
        return dataList;
    }
}