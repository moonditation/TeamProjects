package com.example.myapplication.initial_screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.friend_list.friend_received;
import com.example.myapplication.friend_list.friend_request;

public class PromListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prom_list, container, false);

        FrameLayout active_frame = view.findViewById(R.id.active_prom_frame);
        FrameLayout complete_frame = view.findViewById(R.id.complete_prom_frame);
        FrameLayout waiting_frame = view.findViewById(R.id.waiting_prom_frame);
        FrameLayout prom_list = view.findViewById(R.id.prom_list);

        // 친구 목록 버튼
        AppCompatButton active_prom_button = view.findViewById(R.id.active_prom);
        active_prom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active_frame.setVisibility(View.VISIBLE);
                complete_frame.setVisibility(View.GONE);
                waiting_frame.setVisibility(View.GONE);
                prom_list.setVisibility(View.GONE);
            }
        });

        // 보낸 요청 목록 버튼
        AppCompatButton complete_prom_button = view.findViewById(R.id.complete_prom);
        complete_prom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active_frame.setVisibility(View.GONE);
                complete_frame.setVisibility(View.VISIBLE);
                waiting_frame.setVisibility(View.GONE);
                prom_list.setVisibility(View.GONE);
            }
        });

        // 받은 요청 목록 버튼
        AppCompatButton waiting_prom_button = view.findViewById(R.id.waiting_prom);
        waiting_prom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active_frame.setVisibility(View.GONE);
                complete_frame.setVisibility(View.GONE);
                waiting_frame.setVisibility(View.VISIBLE);
                prom_list.setVisibility(View.GONE);
            }
        });


        return view;
    }
}