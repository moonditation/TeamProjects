package com.example.myapplication.list_prom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.make_prom.added_friend;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class active_and_time_in_promise_info extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activie_and_time_in_promise_info, container, false);

        getParentFragmentManager().setFragmentResultListener("promiseUidBundle", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String documentUid;

                documentUid = result.getString("promiseUid");
                Log.d("promiceacceptInfo", documentUid);
                getPromiseNameFromFirestore(documentUid);

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }

    private void getPromiseNameFromFirestore(String documentUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("promisesPractice").document(documentUid);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String promiseName = document.getString("promiseName");
                    Timestamp promiseTimestamp = document.getTimestamp("promiseDate");

                    // 가져온 promiseName 값을 TextView에 설정
                    if (promiseName != null) {
                        TextView textView = getView().findViewById(R.id.promise_name);
                        textView.setText(promiseName);
                    }

                    if (promiseTimestamp != null) {
                        Date promiseDate = promiseTimestamp.toDate();
                        Date currentTime = Calendar.getInstance().getTime();
                        long timeDifference = promiseDate.getTime() - currentTime.getTime();

                        if (timeDifference >= 0) {
                            TextView textView = getView().findViewById(R.id.time_exceed);
                            textView.setText("분 남았습니다.");
                        }

                        TextView timeTextView = getView().findViewById(R.id.delay_time);
                        timeTextView.setText(Math.abs(timeDifference) / 60000 + "");
                    }

//
                    docRef.collection("friends")
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    int friendsCount = task2.getResult().size(); // 서브컬렉션의 문서 개수
                                    TextView textView = getView().findViewById(R.id.all_of_members);
                                    textView.setText("" + friendsCount);
                                } else {
                                    // 작업 실패 처리
                                }
                            });

                    docRef.collection("friends")
                            .whereEqualTo("friendArrive", false) // friendsArrive 필드가 false인 문서 필터링
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    int friendsCount = task2.getResult().size(); // 서브컬렉션의 문서 개수
                                    TextView textView = getView().findViewById(R.id.late_members);
                                    textView.setText("" + friendsCount);

                                    if(friendsCount>0){
                                        List<String> friendNames = new ArrayList<>();

                                        for (DocumentSnapshot friendSnapshot : task2.getResult()) {
                                            String friendName = friendSnapshot.getString("friendName");
                                            if (friendName != null) {
                                                friendNames.add(friendName);
                                            }
                                        }

                                        // 친구들의 이름을 가져왔으니 이를 EditText에 설정하여 출력합니다.
                                        StringBuilder namesBuilder = new StringBuilder();
                                        for (String name : friendNames) {
                                            namesBuilder.append(name).append("\n");
                                        }

                                        TextView friendsNameTextView = getView().findViewById(R.id.late_member_name);
                                        friendsNameTextView.setText(namesBuilder.toString());
                                    }


                                } else {
                                    // 작업 실패 처리
                                }
                            });


                }
            }
        });
    }
}
