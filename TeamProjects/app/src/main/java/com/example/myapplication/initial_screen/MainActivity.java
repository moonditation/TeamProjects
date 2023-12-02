package com.example.myapplication.initial_screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    private long initTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 프래그먼트 생성 및 데이터 전달
        FirstFragment fragment = new FirstFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        binding.bottomNavigationView.getMenu().getItem(0).setChecked(false);
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
            } else if (itemId == R.id.home) {
                replaceFragment(new FirstFragment());
            }

            return true;
        });
        binding.bottomNavigationView.setOnItemReselectedListener(item -> {

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if(currentFragment instanceof FirstFragment){
                if(System.currentTimeMillis() - initTime > 3000){
                    Log.d("MGR", "앱을 종료하려면, 한번 더 눌러주세요");
                    Toast.makeText(this, "한번 더 눌러야함", Toast.LENGTH_SHORT).show();
                    initTime = System.currentTimeMillis();
                }
                else{
                    finish();
                }
            }
            else{
                replaceFragment(new FirstFragment());
            }
            return true;

        }

        return super.onKeyDown(keyCode, event);
    }
}