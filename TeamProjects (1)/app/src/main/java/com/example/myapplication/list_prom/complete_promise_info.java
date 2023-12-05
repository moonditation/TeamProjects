package com.example.myapplication.list_prom;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class complete_promise_info extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_promise_info, container, false);
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

        added_friend added_friend = new added_friend();
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

        //얘를 클릭하면, 약속정보가 수정됨과 동시에, 파티원들에게 noti 가 날아가야함.
        view.findViewById(R.id.edit_promise).setOnClickListener(new View.OnClickListener() {
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

                    // 가져온 promiseName 값을 TextView에 설정
                    if (promiseName != null) {
                        TextView textView = getView().findViewById(R.id.promise_name);
                        textView.setText(promiseName);
                    }
                }
            } else {
                // 가져오기 실패 처리
            }
        });



    }
}