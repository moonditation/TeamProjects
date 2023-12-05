package com.example.myapplication.make_prom;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentClickMakePromiseBinding;
import com.example.myapplication.map.NaverMapGeoCodingTest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class click_make_promise extends Fragment {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference promisesCollectionRef = firestore.collection("promisesPractice");
    FragmentClickMakePromiseBinding binding;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private double selectLatitude, selectLongitude;
    private static final int REQUEST_CODE = 1000;
    String myUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClickMakePromiseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        added_friend added_friend = new added_friend();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.added_friend_list, added_friend);
        fragmentTransaction.commit();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myUid = currentUser.getUid();


        Button dateButton = binding.promiseDay;
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button timeButton = binding.promiseTime;
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


        // 프래그먼트 2의 종료 이벤트 처리
        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MGR", "잘 눌림");
                adding_friend adding_friend = new adding_friend();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_top,
                        R.anim.slide_out_bottom
                );
                fragmentTransaction.add(R.id.frame_layout, adding_friend);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        binding.promisePlaceAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NaverMapGeoCodingTest.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        //얘가 클릭되면, db에 약속이 만들어진다.
        view.findViewById(R.id.make_new_promise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFirestore();

                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    selectLatitude = data.getDoubleExtra("latitude", 0);
                    selectLongitude = data.getDoubleExtra("longitude", 0);
                    Log.d("woo2", "onActivityResult: "+selectLatitude+selectLongitude);
                }
            }
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(), // 혹은 getContext()를 사용하세요.
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;

                        binding.promiseDay.setText(year + "-" + (month + 1) + "-" + dayOfMonth);


                    }
                },
                initialYear,
                initialMonth,
                initialDay
        );

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
        int initialMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;

                        binding.promiseTime.setText(hourOfDay + ":" + minute);

                    }
                },
                initialHour,
                initialMinute,
                true // 24시간 형식으로 표시하려면 true, 아니면 false 설정
        );
        timePickerDialog.show();
    }

    private void addToFirestore() {
        // EditText에서 값 가져오기
        EditText promiseName = binding.promiseName; // 여기에 자신의 EditText ID를 넣어주세요

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth - 1); // Calendar의 월은 0부터 시작하므로 -1 처리
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        Timestamp timestamp = new Timestamp(calendar.getTime());


// 이 값을 가져온 변수들
        int promiseAcceptPeople = 0;
        double promiseLatitude = selectLatitude;
        double promiseLongitude = selectLongitude;
        String promiseNameString = promiseName.getText().toString();

// 데이터를 저장할 HashMap 생성
        Map<String, Object> promiseData = new HashMap<>();
        promiseData.put("promiseAcceptPeople", promiseAcceptPeople);
        promiseData.put("promiseDate", timestamp);
        promiseData.put("promiseLatitude", promiseLatitude);
        promiseData.put("promiseLongitude", promiseLongitude);
        promiseData.put("promiseName", promiseNameString);


// Firestore에 데이터 추가
        promisesCollectionRef.add(promiseData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String promiseDocumentId = documentReference.getId();
                        addPromiseToUserCollection(myUid, promiseDocumentId);
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        // 추가 성공 시 처리할 내용
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        // 실패 시 처리할 내용
                    }
                });


    }


    ////////user의 promises에 문서 uid 넣어주기
    private void addPromiseToUserCollection(String myUid, String promiseDocumentId) {
        // 현재 유저의 문서 참조 가져오기
        DocumentReference userDocRef = firestore.collection("users").document(myUid);

        // promises 서브컬렉션에 새로운 문서 추가
        userDocRef.collection("promises").document(promiseDocumentId)
                .set(new HashMap<>()) // 빈 데이터를 추가하거나 필요한 데이터를 넣어주세요
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 문서 추가 성공 시 처리
                        Log.d("Firestore", "Document added successfully to user's promises collection");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 문서 추가 실패 시 처리
                        Log.e("Firestore", "Error adding document to user's promises collection", e);
                    }
                });
    }


    private void withFriend(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String myUid = currentUser.getUid();
            String myName = currentUser.getDisplayName(); // 사용자의 이름 또는 다른 정보 가져오기

            // 자신의 정보를 friendData에 추가
            Map<String, Object> myData = new HashMap<>();
            myData.put("friendAccept", true);
            myData.put("friendArrive", false);
            myData.put("friendArriveTime", Timestamp.now());
            myData.put("friendId", "자신의 ID 또는 다른 고유 식별자");
            myData.put("friendLatitude", "자신의 위도");
            myData.put("friendLongitude", "자신의 경도");
            myData.put("friendName", myName); // 사용자의 이름 또는 다른 정보 추가
            myData.put("friendUid", myUid); // 자신의 UID 추가

            // friendData에 자신의 정보 추가 후, friend 서브컬렉션에 문서 추가
            addFriendToPromise("약속 문서 ID", myData); // 약속 문서 ID를 넣어주세요
        }
    }


    /////////////이제 친구 목록 하는거에서 Map의 값 받아서 하면 됨
    private void addFriendToPromise(String promiseDocumentId, Map<String, Object> friendData) {
        // 약속 문서 참조 가져오기
        DocumentReference promiseDocRef = firestore.collection("promisesPractice").document(promiseDocumentId);

        // friend 서브컬렉션에 새로운 문서 추가
        promiseDocRef.collection("friend").add(friendData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // 문서 추가 성공 시 처리
                        Log.d("Firestore", "Friend document added successfully to promise's friend collection");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 문서 추가 실패 시 처리
                        Log.e("Firestore", "Error adding friend document to promise's friend collection", e);
                    }
                });
    }
}