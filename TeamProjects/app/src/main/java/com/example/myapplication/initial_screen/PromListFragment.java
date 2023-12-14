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
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.myapplication.R;
import com.example.myapplication.friend_list.friend_received_fragment;
import com.example.myapplication.list_prom.activied_promise;
import com.example.myapplication.list_prom.completed_promise;
import com.example.myapplication.list_prom.none_click_promise_list;
import com.example.myapplication.list_prom.waiting_promise;

public class PromListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prom_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        none_click_promise_list none_click_promise_list = new none_click_promise_list();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.prom_list, none_click_promise_list);
        fragmentTransaction.commit();

        AppCompatButton active_prom_button_click = view.findViewById(R.id.active_prom);
        active_prom_button_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activied_promise fragment_activied_promise = new activied_promise();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.prom_list, fragment_activied_promise);
                fragmentTransaction.commit();

            }
        });

        AppCompatButton completed_prom_button_click = view.findViewById(R.id.complete_prom);
        completed_prom_button_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completed_promise fragment_completed_promise = new completed_promise();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.prom_list, fragment_completed_promise);
                fragmentTransaction.commit();

            }
        });

        AppCompatButton waiting_prom_button_click = view.findViewById(R.id.waiting_prom);
        waiting_prom_button_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting_promise fragment_waiting_promise = new waiting_promise();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.prom_list, fragment_waiting_promise);
                fragmentTransaction.commit();

            }
        });

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


    }
}