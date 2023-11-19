package com.example.myapplication.initial_screen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PromMakeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prom_make, container, false);

        ImageButton cancel_button = view.findViewById(R.id.cancelButton);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstFragment firstFragment = new FirstFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_bottom
                );
                fragmentTransaction.replace(R.id.frame_layout, firstFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}