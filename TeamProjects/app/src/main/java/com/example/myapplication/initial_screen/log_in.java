package com.example.myapplication.initial_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication.R;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.databinding.ActivityLogInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class log_in extends AppCompatActivity {

    ActivityLogInBinding binding;
    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseAuth();

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.PW);
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(log_in.this, sign_in.class));
//                String email = emailEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//
//                signUp(email, password);
            }
        });

        binding.logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String email = emailEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//
//                log_in(email, password);
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
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            updateUI(currentUser);
//        }
    }
//
//    private void signUp(String id, String password) {
//        mAuth.createUserWithEmailAndPassword(id, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // 회원가입 성공, 사용자 정보 가져오기
//                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                            if (firebaseUser != null) {
//                                String userId = firebaseUser.getUid();
//
//                                // 사용자 닉네임을 Firestore에 추가
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                DocumentReference userRef = db.collection("usersExample").document(userId);
//
//                                // 닉네임 추가
//                                Map<String, Object> userInfo = new HashMap<>();
//                                userInfo.put("nickname", nickname); // 여기서 nickname은 사용자가 입력한 닉네임
//
//                                // Firestore에 닉네임 추가
//                                userRef.set(userInfo, SetOptions.merge())
//                                        .addOnSuccessListener(aVoid -> {
//                                            // 닉네임이 Firestore에 성공적으로 추가됨
//                                            // 이후 작업 수행
//                                            Log.d("signUp", "User signed Up Success");
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            // 닉네임 추가 실패
//                                            Log.d("signUp", "User signed Up Failed");
//                                        });
//                            }
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Log.d(TAG, "createUserWithEmail:failure");
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
//
//    private void log_in(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Log.d(TAG, "signInWithEmail:failure");
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
//
//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            Intent intent = new Intent(this, ChattingActivity.class);
//            intent.putExtra("USER_PROFILE", "email: " + user.getEmail() + "\n" + "uid: " + user.getUid());
//
//            startActivity(intent);
//        }
//    }
}