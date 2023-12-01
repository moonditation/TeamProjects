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
import com.example.myapplication.adapter.Prom_completed_adapter;
import com.example.myapplication.adapter.Prom_waiting_adapter;
import com.example.myapplication.adapter.Promise;

import java.util.ArrayList;
import java.util.List;

public class waiting_promise extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiting_promise, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_wait_prom);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터와 데이터 연결
        List<Promise> dataList = generateData(); // 데이터 생성
        Prom_waiting_adapter adapter = new Prom_waiting_adapter(dataList);
        recyclerView.setAdapter(adapter);
    }

    //다른 약속도 똑같이 정리하긴 할텐데, 리스트에 담는 과정을 정리하면 된다.
    //사실 추가되더라고 크게 바뀔 것은 없다.
    //특정 조건들을 통해서 도큐먼트들을 얻어와서, promise 에 넣어준다.
    //promise class 의 생성자는 바꿔줘야겠지.
    //그리고 게터 세터만 바꿔주면 될듯.
    private List<Promise> generateData() {
        List<Promise> dataList = new ArrayList<>();
        // 데이터를 원하는대로 추가
        dataList.add(new Promise("이종민 1"));
        dataList.add(new Promise("이종민 2"));
        dataList.add(new Promise("이종민 3"));
        dataList.add(new Promise("이종민 4"));
        dataList.add(new Promise("이종민 5"));
        dataList.add(new Promise("이종민 6"));
        // ...
        return dataList;
    }
}