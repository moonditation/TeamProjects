package com.example.myapplication.list_prom;

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
import com.example.myapplication.adapter.Prom_activied_adapter;
import com.example.myapplication.adapter.Prom_completed_adapter;
import com.example.myapplication.adapter.Promise;

import java.util.ArrayList;
import java.util.List;

public class completed_promise extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_promise, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_complete_prom);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터와 데이터 연결
        List<Promise> dataList = generateData(); // 데이터 생성
        Prom_completed_adapter adapter = new Prom_completed_adapter(dataList);
        recyclerView.setAdapter(adapter);

    }

    private List<Promise> generateData() {
        List<Promise> dataList = new ArrayList<>();
        // 데이터를 원하는대로 추가
        dataList.add(new Promise("동네 약속", "fewfwf"));
//        dataList.add(new Promise("이우혁 2"));
//        dataList.add(new Promise("이우혁 3"));
//        dataList.add(new Promise("이우혁 4"));
//        dataList.add(new Promise("이우혁 5"));
//        dataList.add(new Promise("이우혁 6"));
        // ...
        return dataList;
    }
}