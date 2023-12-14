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
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Prom_completed_adapter;
import com.example.myapplication.adapter.Prom_make_added_friend;
import com.example.myapplication.adapter.Promise;
import com.example.myapplication.adapter.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class added_friend extends Fragment {

    private Prom_make_added_friend adapter; // 이종민 병합 코드

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_added_friend, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 우혁이 코드
//        String documentUid = getArguments().getString("documentUid");
//        // 리사이클러뷰 설정
//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_added_friend);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        Log.d("woohyuk", "여기까진 ok22222");
//
//        // 어댑터와 데이터 연결
//        List<String> dataList = new ArrayList<>();
//        CollectionReference friendsCollectionRef = db.collection("promisesPractice")
//                .document(documentUid)
//                .collection("friends");
//
//        friendsCollectionRef.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String friendUid = document.getString("friendUid");
//                            if (friendUid != null) {
//                                dataList.add(friendUid);
//                            }
//                        }
//                        // 어댑터와 데이터 연결
//                        Prom_make_added_friend adapter = new Prom_make_added_friend(dataList);
//                        recyclerView.setAdapter(adapter);
//                    } else {
//                        Log.e("Firestore", "Error getting documents: ", task.getException());
//                    }
//                });


        // 이종민 병합 코드
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_added_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Bundle bundle = getArguments();
        List<String> dataList = generateData(bundle);
        adapter = new Prom_make_added_friend(dataList);
        recyclerView.setAdapter(adapter);

    }

    // 우혁이 generateData()
//    private void generateData() {
//        getParentFragmentManager().setFragmentResultListener("bundle", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                String documentUid= result.getString("documentUid");
//                Log.d("documentID", documentUid);
//
//                FirebaseFirestore.getInstance()
//                        .collection("promisesPractice")
//                        .document(documentUid)
//                        .collection("friends")
//                        .get()
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                List<String> dataList = new ArrayList<>();
//                                for (DocumentSnapshot friendSnapshot : task.getResult()) {
//                                    String friendName = friendSnapshot.getString("friendUid");
//                                    if (friendName != null) {
//                                        dataList.add(friendName);
//                                    }
//                                }
//
//                                Prom_make_added_friend adapter = new Prom_make_added_friend(dataList);
//                                RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_added_friend);
//                                recyclerView.setAdapter(adapter);
//                            } else {
//                            }
//                        });
//            }
//        });
//    }

    private List<String> generateData(Bundle bundle) {
        List<String> dataList = new ArrayList<>();
        Log.d("bundle", "added_friend 리사이클러뷰의 bundle" + bundle);
        if (bundle != null) {
            ArrayList<String> uidList = bundle.getStringArrayList("uidList");
            Bundle bundle1 = new Bundle();
            bundle1.putStringArrayList("uidList", uidList);
            getParentFragmentManager().setFragmentResult("uidListBundle", bundle1);

            if (uidList != null) {
                Log.d("bundle", "친구 데이터 가져옴");
                for (String uid : uidList) {
                    db.collection("users")
                            .whereEqualTo("uid", uid)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            String addedFriendName = document.getString("name");
                                            String addedFriendId = document.getString("id");
                                            Log.d("bundle", "친구 데이터 넣기 성공:" + addedFriendName + ", " + addedFriendId);
                                            dataList.add(uid);

                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Log.d("bundle", "친구 데이터 넣기 실패");
                                    }
                                }
                            });

                }


            } else {
                Toast.makeText(added_friend.this.getContext(), "추가된 친구가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("MakePromise", "Bundle Null");
        }
        return dataList;
    }

}