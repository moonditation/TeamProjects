package com.example.myapplication.make_prom;

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
import com.example.myapplication.adapter.Prom_completed_adapter;
import com.example.myapplication.adapter.Prom_make_added_friend;
import com.example.myapplication.adapter.Promise;
import com.example.myapplication.adapter.User;

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

        // 어댑터와 데이터 연결
        List<User> dataList = generateData(); // 데이터 생성
        Prom_make_added_friend adapter = new Prom_make_added_friend(dataList);
        recyclerView.setAdapter(adapter);
    }

    private List<User> generateData() {
        List<User> dataList = new ArrayList<>();
        // 데이터를 원하는대로 추가
        dataList.add(new User("이우혁", "7dngur7"));
        dataList.add(new User("이우혁", "7dngur7"));
        dataList.add(new User("이우혁", "7dngur7"));
        dataList.add(new User("이우혁", "7dngur7"));
        // ...
        return dataList;
    }
}