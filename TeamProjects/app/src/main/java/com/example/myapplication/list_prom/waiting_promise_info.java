package com.example.myapplication.list_prom;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Friend_list_adapter;
import com.example.myapplication.adapter.User;
import com.example.myapplication.databinding.ItemActivePromBinding;
import com.example.myapplication.make_prom.added_friend;
import com.example.myapplication.map.NaverMapShowPromisePlace;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class waiting_promise_info extends Fragment {
    String documentUid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiting_promise_info, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            documentUid = bundle.getString("promiseUid");
            if (documentUid != null && !documentUid.isEmpty()) {
                getPromiseNameFromFirestore(documentUid);
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = new Bundle();
        bundle.putString("documentUid", documentUid);
        added_friend added_friend = new added_friend();
        added_friend.setArguments(bundle);
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.added_friend_list, added_friend);
        fragmentTransaction.commit();
        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.promise_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("promisesPractice").document(documentUid);

                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Double promiseLatitude = document.getDouble("promiseLatitude");
                            Double promiseLongitude = document.getDouble("promiseLongitude");

                            // promiseLatitude와 promiseLongitude를 다음 액티비티로 전달하는 Intent 생성
                            Intent intent = new Intent(getActivity(), NaverMapShowPromisePlace.class);
                            intent.putExtra("latitude", promiseLatitude);
                            intent.putExtra("longitude", promiseLongitude);
                            startActivity(intent);
                        } else {
                            Log.d("Firestore", "Document does not exist");
                        }
                    } else {
                        Log.e("Firestore", "Error getting document", task.getException());
                    }
                });
            }
        });


    }

    private void getPromiseNameFromFirestore(String documentUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("promisesPractice").document(documentUid);

        Log.d("docId", documentUid);

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
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        String formattedDate = formatter.format(promiseDate);

                        TextView promiseDayTextView = getView().findViewById(R.id.promise_day);
                        promiseDayTextView.setText(formattedDate);
                    }


                } else {
                    Log.d("Firestore", "Document does not exist"); // 문서가 존재하지 않는 경우 로그 출력
                }
            } else {
                Log.e("Firestore", "Error getting document", task.getException()); // 문서 가져오기 실패 시 에러 로그 출력
            }
        });
    }




}