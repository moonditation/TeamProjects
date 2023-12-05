package com.example.myapplication.list_prom;

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
import com.example.myapplication.adapter.Prom_activied_adapter;
import com.example.myapplication.adapter.Prom_completed_adapter;
import com.example.myapplication.adapter.Promise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class completed_promise extends Fragment {
    private FirebaseFirestore db;
    private String myUid;
    private AtomicInteger activePromisesCount = new AtomicInteger(0);
    private int promisesCount = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_promise, container, false);
        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_complete_prom);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUid = user.getUid();

        db.collection("users")
                .document(myUid)
                .collection("promises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            promisesCount = task.getResult().size();
                            List<Promise> dataList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String promiseDocumentUid = document.getString("promiseDocument");
                                String promiseName = document.getString("promiseName");

                                checkCompleted(promiseDocumentUid, new activied_promise.CompletionCallback() {
                                    @Override
                                    public void onComplete(boolean result) {
                                        if (result) {
                                            dataList.add(new Promise(promiseName, promiseDocumentUid));
                                        }

                                        int count = activePromisesCount.incrementAndGet();
                                        // 모든 비동기 호출이 완료되면 어댑터 설정
                                        if (count == promisesCount) {
                                            Prom_completed_adapter adapter = new Prom_completed_adapter(dataList);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    }
                                });
                            }

                            // 어댑터와 데이터 연결
                            Prom_completed_adapter adapter = new Prom_completed_adapter(dataList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            // 에러 처리
                        }
                    }
                });

    }

    private void checkCompleted(String promiseDocumentUid, activied_promise.CompletionCallback callback) {
        // Firestore에서 해당 문서 가져오기
        DocumentReference docRef = db.collection("promisesPractice").document(promiseDocumentUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        long promiseAcceptPeople = document.getLong("promiseAcceptPeople");

                        // 해당 문서의 friends 서브컬렉션의 문서 수 가져오기
                        db.collection("promisesPractice")
                                .document(promiseDocumentUid)
                                .collection("friends")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int friendsCount = task.getResult().size(); // friends 서브컬렉션의 문서 수

                                            // promiseAcceptPeople 값과 friendsCount 값 비교하여 조건 확인
                                            callback.onComplete(promiseAcceptPeople == friendsCount);
                                        } else {
                                            // friends 서브컬렉션의 문서 가져오기 실패 시 처리
                                            callback.onComplete(false);
                                        }
                                    }
                                });
                    } else {
                        // 문서가 존재하지 않을 때 처리
                        callback.onComplete(false);
                    }
                } else {
                    // 문서 가져오기 실패 시 처리
                    callback.onComplete(false);
                }
            }
        });
    }

}