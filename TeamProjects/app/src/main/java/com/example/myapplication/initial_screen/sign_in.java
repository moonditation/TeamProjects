package com.example.myapplication.initial_screen;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.adapter.User;
import com.example.myapplication.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sign_in extends AppCompatActivity {


    ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.name.getText().toString();
                String id = binding.id.getText().toString();
                String password = binding.pw.getText().toString();
                String pass_check = binding.pwCheck.getText().toString();

                signIn(name, id, password, pass_check);
            }
        });
    }


    private void signIn(String name, String id, String password, String pass_check) {


        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(pass_check)) {
            Toast.makeText(sign_in.this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(pass_check)) {
            Toast.makeText(sign_in.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        checkDuplicateId(id, name, password);
    }

    private void checkDuplicateId(final String id, final String name, final String password) {
        DocumentReference userRef = db.collection("users").document();
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(sign_in.this, "이미 사용 중인 ID입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        signUpWithFirebase(id, name, password);
                    }
                } else {
                    Toast.makeText(sign_in.this, "중복 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void signUpWithFirebase(String id, String name, String password) {
        mAuth.createUserWithEmailAndPassword(id + "@example.com", password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(sign_in.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                            saveUserDataToFirestore(id, name);
                        } else {
                            Toast.makeText(sign_in.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveUserDataToFirestore(String id, String name) {
        User user = new User(name, id, mAuth.getUid());

        db.collection("users").document(mAuth.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendUserDataToMainActivity(mAuth.getCurrentUser());

                    finish();
//                    addFriendsToFirestore(id, null);
                } else {
                }
            }
        });;
    }


    private void sendUserDataToMainActivity(FirebaseUser user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void addFriendsToFirestore(String userId, List<User> friends) {
        CollectionReference friendsRef = db.collection("users").document().collection("friends");

        for (User friend : friends) {
            friendsRef.document(friend.getId())
                    .set(friend)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            } else {
                            }
                        }
                    });
        }
    }

}

