package com.example.myapplication.make_prom;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Prom_make_added_friend;
import com.example.myapplication.adapter.User;
import com.example.myapplication.databinding.FragmentClickMakePromiseBinding;
import com.example.myapplication.map.NaverMapGeoCodingTest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class click_make_promise extends Fragment {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference promisesCollectionRef = firestore.collection("promisesPractice");
    FragmentClickMakePromiseBinding binding;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private double selectLatitude = -100000, selectLongitude=-100000;
    private static final int REQUEST_CODE = 1000;
    String myUid;

    boolean checkError = false;

    ArrayList<String> uidList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClickMakePromiseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getParentFragmentManager().setFragmentResultListener("uidListBundle", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                uidList = result.getStringArrayList("uidList");
                Log.d("uidListListener", "" + uidList);
            }
        });

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

        AppCompatButton dateButton = binding.promiseDay;
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        AppCompatButton timeButton = binding.promiseTime;
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        view.findViewById(R.id.make_new_promise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BeforeAdd", "" + uidList);
                addToFirestore(uidList, new OnFirestoreCompleteListener() {
                            @Override
                            public void onComplete(boolean isSuccess) {
                                if(!checkError){
                                    Log.d("checkErrorhere", "kadjflfsjklflajks");
                                    Toast.makeText(view.getContext(), "대기중인 약속을 생성하였습니다", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                }else{
                                    checkError = !checkError;
                                }
                            }
                        });





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
                    Log.d("woo2", "onActivityResult: " + selectLatitude + selectLongitude);
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
                requireContext(),
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
                true
        );
        timePickerDialog.show();
    }

    private void addToFirestore(ArrayList<String> uidList, OnFirestoreCompleteListener listener) {

        EditText promiseName = binding.promiseName;

        String promiseNameString = promiseName.getText().toString();
        if (promiseNameString.isEmpty()) {
            Toast.makeText(requireContext(), "약속 이름을 입력하세요", Toast.LENGTH_SHORT).show();
            checkError = true;
            return;
        }

        if (uidList == null || uidList.isEmpty()) {
            Toast.makeText(requireContext(), "친구를 선택하세요", Toast.LENGTH_SHORT).show();
            checkError = true;
            return;
        }

        if (selectLatitude == -100000 && selectLongitude == -100000) {
            Toast.makeText(requireContext(), "위치를 입력하세요", Toast.LENGTH_SHORT).show();
            checkError = true;
            return;
        }


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);
        if (selectedYear == 0 || selectedMonth == 0 || selectedDay == 0 || selectedHour == 0 || selectedMinute == 0) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            checkError = true;
            return;
        }

        Timestamp timestamp = new Timestamp(calendar.getTime());







        int promiseAcceptPeople = 1; // uidList의 개수 넣어주면 될 듯
        double promiseLatitude = selectLatitude;
        double promiseLongitude = selectLongitude;

        Map<String, Object> promiseData = new HashMap<>();
        promiseData.put("promiseAcceptPeople", promiseAcceptPeople);
        promiseData.put("promiseDate", timestamp);
        promiseData.put("promiseLatitude", promiseLatitude);
        promiseData.put("promiseLongitude", promiseLongitude);
        promiseData.put("promiseName", promiseNameString);


        promisesCollectionRef
                .whereEqualTo("promiseName", promiseNameString)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LJM", "task.isSuccessful()은 됨");
                        boolean isDuplicate = !task.getResult().isEmpty();
                        if (isDuplicate) {
                            Log.d("LJM", "중복일 때 토스트처리");
//                            Toast.makeText(getActivity(), "기존에 생성된 약속존 이름입니다.", Toast.LENGTH_SHORT).show();
                            listener.onComplete(true);

                        } else {
                            Log.d("LJM", "중복이 아닐 때 처리");

                            promisesCollectionRef.add(promiseData)
                                    .addOnSuccessListener(documentReference -> {
                                        String promiseDocumentId = documentReference.getId();
                                        withFriend(promiseDocumentId, uidList);
                                        addPromiseToUserCollection(promiseDocumentId, promiseNameString, uidList);

                                        Log.d("LJM", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        listener.onComplete(false); // 작업 성공을 listener에 알림

                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("uidList", "Firebase에 데이터를 추가하기 위한 참조 실패");
                                    });

                        }
                    } else {
                        Log.d("LJM", "중복 검사 실패");
                        listener.onComplete(true); // 작업 실패를 listener에 알림

                    }
                });

    }


    ////////user의 promises에 문서 uid 넣어주기
    private void addPromiseToUserCollection(String promiseDocumentId, String promiseName, ArrayList<String> uidList) {
        Map<String, Object> promiseDocumentData = new HashMap<>();
        promiseDocumentData.put("promiseDocument", promiseDocumentId);
        promiseDocumentData.put("promiseName", promiseName);

        for (String uid : uidList) {
            firestore.collection("users")
                    .document(uid)
                    .collection("promises")
                    .document(promiseDocumentId)
                    .set(promiseDocumentData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "문서 추가 성공");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "문서 추가 실패", e);
                        }
                    });
        }
    }


    // 번들로 정보를 받아서

    private void withFriend(String promiseDocumentId, ArrayList<String> uidList) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (uidList != null) {
            uidList.add(currentUser.getUid());
            for (String uid : uidList) {
                firestore.collection("users")
                        .whereEqualTo("uid", uid)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String friendName = document.getString("name");
                                    String friendID = document.getString("id");
                                    String friendUid = document.getString("uid");
                                    if (currentUser != null) {
                                        String myUid = currentUser.getUid();
                                        String myName = currentUser.getDisplayName();

                                        // 자신의 정보를 friendData에 추가
                                        Map<String, Object> myData = new HashMap<>();
                                        if (myUid == uid) {
                                            myData.put("friendAccept", true);
                                        } else {
                                            myData.put("friendAccept", false);
                                        }
//                                        myData.put("friendAccept", false);
                                        myData.put("friendArrive", false);
                                        myData.put("friendArriveTime", Timestamp.now());
                                        myData.put("friendId", friendID);
                                        myData.put("friendLatitude", 37.495861);
                                        myData.put("friendLongitude", 126.953991);
                                        myData.put("friendName", friendName);
                                        myData.put("friendUid", friendUid);

                                        addFriendToPromise(promiseDocumentId, friendUid, myData);
                                    }
                                }
                            }

                        });

            }
        } else {
            Log.d("uidList2", "uidList2 null");
        }
    }


    /////////////이제 친구 목록 하는거에서 Map의 값 받아서 하면 됨
    private void addFriendToPromise(String promiseDocumentId, String friendUid, Map<String, Object> friendData) {
        DocumentReference promiseDocRef = firestore.collection("promisesPractice").document(promiseDocumentId);

        promiseDocRef.collection("friends")
                .document(friendUid)
                .set(friendData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("PromiseFriends", "Add Friends to Firesore Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PromiseFriends", "Add Friends to Firesore Success");
                    }
                });
    }

}
