package com.example.myapplication.initial_screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.friend_list.friend_received;
import com.example.myapplication.friend_list.friend_request;


public class FriendListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        // 친구 목록 버튼
        AppCompatButton friendListButton = view.findViewById(R.id.list_of_friend);
        friendListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 처리할 코드
                // 예: 다른 프래그먼트로 이동하거나 특정 동작 수행
                Toast.makeText(getActivity(), "친구 목록 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        // 보낸 요청 목록 버튼
        AppCompatButton sentRequestsButton = view.findViewById(R.id.request_list);
        sentRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 처리할 코드
                startActivity(new Intent(getActivity(), friend_request.class));
            }
        });

        // 받은 요청 목록 버튼
        AppCompatButton receivedRequestsButton = view.findViewById(R.id.received_list);
        receivedRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 처리할 코드
                startActivity(new Intent(getActivity(), friend_received.class));
            }
        });

        return view;
    }
    }