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

import com.example.myapplication.R;
import com.example.myapplication.adapter.Late_list_adapter;
import com.example.myapplication.adapter.Late_member_adapter;
import com.example.myapplication.adapter.Promise;
import com.example.myapplication.adapter.User;
import com.example.myapplication.databinding.FragmentAddingFriendBinding;
import com.example.myapplication.databinding.FragmentLateListClickBinding;
import com.example.myapplication.databinding.ItemLateListBinding;

import java.util.ArrayList;
import java.util.List;

public class late_list_click extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_late_list_click, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_late_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터와 데이터 연결
        List<User> dataList = generateData(); // 데이터 생성
        Late_member_adapter adapter = new Late_member_adapter(dataList);
        recyclerView.setAdapter(adapter);

    }
    private List<User> generateData() {
        List<User> dataList = new ArrayList<>();
        // 데이터를 원하는대로 추가
        dataList.add(new User("이우혁", "7dngur7"));
        dataList.add(new User("문관록","gr1004"));
        dataList.add(new User("이종민","ljm0000"));
        dataList.add(new User("김김김","0107"));
        dataList.add(new User("육육육","0531"));
        dataList.add(new User("민민민","0202"));
        // ...
        return dataList;
    }
}