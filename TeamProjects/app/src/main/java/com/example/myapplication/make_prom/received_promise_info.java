package com.example.myapplication.make_prom;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class received_promise_info extends Fragment {
//    String documentUid;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getChildFragmentManager().setFragmentResultListener("promiseUidBundle", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                documentUid = result.getString("promiseUid");
//                Log.d("promiceacceptInfo", documentUid);
//                getPromiseNameFromFirestore(documentUid);
//
//            }
//        });
//
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_promise_info, container, false);
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
                        TextView textView = getView().findViewById(R.id.promise_name); // 이 부분은 실제로 해당 TextView의 ID로 변경해야 합니다.
                        textView.setText(promiseName);
                    }
                }
            } else {
                // 가져오기 실패 처리
            }
        });



    }

}