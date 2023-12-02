package com.example.myapplication.initial_screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.example.myapplication.friend_list.friend_list_click_fragment;
import com.example.myapplication.friend_list.friend_received_fragment;
import com.example.myapplication.friend_list.friend_requested_fragment;


public class FriendListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ImageButton cancel_button = view.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstFragment firstFragment = new FirstFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_top,
                        R.anim.slide_out_bottom
                );
                fragmentTransaction.replace(R.id.frame_layout, firstFragment);
                fragmentTransaction.commit();
            }
        });



        // 친구 목록 버튼
        AppCompatButton friendListButton = view.findViewById(R.id.list_of_friend);
        friendListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                friend_list_click_fragment friend_list_click_fragment = new friend_list_click_fragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.add(R.id.frame_layout, friend_list_click_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });


        // 받은 요청 목록 버튼
        AppCompatButton received_list_button = view.findViewById(R.id.received_list);
        received_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_received_fragment received_fragment = new friend_received_fragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.add(R.id.frame_layout, received_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        // 받은 요청 목록 버튼
        AppCompatButton request_list_button = view.findViewById(R.id.request_list);
        request_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend_requested_fragment requested_fragment = new friend_requested_fragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.add(R.id.frame_layout, requested_fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }
}