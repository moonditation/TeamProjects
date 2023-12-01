package com.example.myapplication.initial_screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication.R;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityLogInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class log_in extends AppCompatActivity {

    ActivityLogInBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();

        EditText idEditText = findViewById(R.id.ID);
        EditText passwordEditText = findViewById(R.id.PW);
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(log_in.this, sign_in.class));
            }
        });

        binding.logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                log_in(id, password);
            }
        });
    }

    private void initFirebaseAuth() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
            updateUI(currentUser);
        }
    }



    private void log_in(String id, String password) {

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
            Toast.makeText(log_in.this, "id, 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(id+"@example.com", password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(log_in.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            finish();
                            // 사용자 정보를 메인 엑티비티로 전달
                            sendUserDataToMainActivity(mAuth.getCurrentUser());


                        } else {
                            // 로그인 실패
                            Toast.makeText(log_in.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendUserDataToMainActivity(FirebaseUser user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", user);

            startActivity(intent);
        }
    }

    private long initTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis() - initTime > 3000){
                Log.d("MGR", "앱을 종료하려면, 한번 더 눌러주세요");
                Toast.makeText(this, "앱을 종료하시려면, 한번 더 클릭해주세요.", Toast.LENGTH_SHORT).show();
                initTime = System.currentTimeMillis();
            }
            else{
                finish();
            }
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}