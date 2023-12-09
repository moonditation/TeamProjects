package com.example.myapplication.initial_screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Late_list_adapter;
import com.example.myapplication.adapter.Prom_activied_adapter;
import com.example.myapplication.adapter.Promise;
import com.example.myapplication.list_prom.activied_promise;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LateMemoryFragment extends Fragment {
    private FirebaseFirestore db;
    private String myUid;
    private AtomicInteger activePromisesCount = new AtomicInteger(0);

    private int promisesCount = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_late_memory, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_late_list);
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

                                checkLate(promiseDocumentUid, new activied_promise.CompletionCallback() {
                                    @Override
                                    public void onComplete(boolean result) {
                                        if (result) {
                                            dataList.add(new Promise(promiseName, promiseDocumentUid));
                                        }

                                        int count = activePromisesCount.incrementAndGet();
                                        // 모든 비동기 호출이 완료되면 어댑터 설정
                                        if (count == promisesCount) {
                                            Late_list_adapter adapter = new Late_list_adapter(dataList);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    }
                                });
                            }

                            // 어댑터와 데이터 연결
                            Late_list_adapter adapter = new Late_list_adapter(dataList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            // 에러 처리
                        }
                    }
                });

        ImageButton cancel_button = view.findViewById(R.id.cancelButton);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstFragment firstFragment = new FirstFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_top,
                        R.anim.slide_out_bottom
                );
                fragmentTransaction.replace(R.id.frame_layout, firstFragment);
                fragmentTransaction.commit();
            }
        });


    }



    private void checkLate(String promiseDocumentUid, activied_promise.CompletionCallback callback) {
        // Firestore에서 해당 문서 가져오기
        DocumentReference docRef = db.collection("promisesPractice").document(promiseDocumentUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Timestamp promiseTimestamp = document.getTimestamp("promiseDate");

                        // 현재 시간과 약속시간 비교하여 30분이 지났는지 확인
                        if (promiseTimestamp != null) {
                            long currentTime = System.currentTimeMillis();
                            long promiseTime = promiseTimestamp.toDate().getTime();
                            long differenceInMinutes = (currentTime - promiseTime) / (1000 * 60);

                            // 30분 이상 지났고, friends 컬렉션의 문서 수와 promiseAcceptPeople 값이 같은지 확인
                            if (differenceInMinutes >= 30) {
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
                                                    if (promiseAcceptPeople == friendsCount) {
                                                        callback.onComplete(true);
                                                    } else {
                                                        callback.onComplete(false);
                                                    }
                                                } else {
                                                    // friends 서브컬렉션의 문서 가져오기 실패 시 처리
                                                    callback.onComplete(false);
                                                }
                                            }
                                        });
                            } else {
                                callback.onComplete(false); // 약속시간이 30분 미만인 경우
                            }
                        } else {
                            callback.onComplete(false); // 약속시간 정보가 없는 경우
                        }
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