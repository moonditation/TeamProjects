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
import com.example.myapplication.adapter.Late_list_adapter;
import com.example.myapplication.adapter.Prom_received_adapter;
import com.example.myapplication.adapter.Promise;

import java.util.ArrayList;
import java.util.List;

public class click_received_promise extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_click_received_promise, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_received_promise);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Promise> dataList = generateData();
        Prom_received_adapter adapter = new Prom_received_adapter(dataList);
        recyclerView.setAdapter(adapter);

    }

    private List<Promise> generateData() {
        List<Promise> dataList = new ArrayList<>();
        // 데이터를 원하는대로 추가
//        dataList.add(new Promise("문관록 1"));
//        dataList.add(new Promise("문관록 2"));
//        dataList.add(new Promise("문관록 3"));
//        dataList.add(new Promise("문관록 4"));
//        dataList.add(new Promise("문관록 5"));
//        dataList.add(new Promise("문관록 6"));
        // ...
        return dataList;
    }
}