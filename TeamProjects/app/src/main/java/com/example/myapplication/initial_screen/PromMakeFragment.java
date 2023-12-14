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
import com.example.myapplication.friend_list.friend_received_fragment;
import com.example.myapplication.make_prom.added_friend;
import com.example.myapplication.make_prom.click_make_promise;
import com.example.myapplication.make_prom.click_received_promise;

public class PromMakeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prom_make, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageButton cancel_button = view.findViewById(R.id.cancelButton);
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



        AppCompatButton make_prom_button = view.findViewById(R.id.make_promise);
        make_prom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_make_promise click_make_promise = new click_make_promise();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.add(R.id.frame_layout, click_make_promise);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        // 이거와 연결되는 프래그먼트도 없애줘야 함.
//        AppCompatButton received_promise = view.findViewById(R.id.received_promise);
//        received_promise.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                click_received_promise click_received_promise = new click_received_promise();
//                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
//
//                fragmentTransaction.add(R.id.frame_layout, click_received_promise);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//
//            }
//        });

    }
}