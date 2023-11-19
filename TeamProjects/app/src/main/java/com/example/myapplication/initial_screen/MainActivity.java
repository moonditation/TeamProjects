package com.example.myapplication.initial_screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new FirstFragment());

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.check_promise) {
                replaceFragment(new PromListFragment());
            } else if (itemId == R.id.make_promise) {
                replaceFragment(new PromMakeFragment());
            } else if (itemId == R.id.friend_list) {
                replaceFragment(new FriendListFragment());
            } else if (itemId == R.id.late_memory) {
                replaceFragment(new LateMemoryFragment());
            }
            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
        );
        fragmentTransaction.replace(R.id.frame_layout, fragment);

        fragmentTransaction.commit();
    }
    private void replaceFragmentWithStack(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
        );
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



}