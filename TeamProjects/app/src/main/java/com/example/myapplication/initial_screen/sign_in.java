package com.example.myapplication.initial_screen;

import android.os.Bundle;

import com.example.myapplication.databinding.ActivitySignInBinding;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.example.myapplication.R;

public class sign_in extends AppCompatActivity {


    ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

}