package com.example.myapplication.friend_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Adding_friend_adapter;
import com.example.myapplication.adapter.User;
import com.example.myapplication.adapter.friend_request_adapter;
import com.example.myapplication.databinding.FragmentAddingFriendBinding;
import com.example.myapplication.databinding.FragmentFriendRequestedFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class friend_requested_fragment extends Fragment {

    private FragmentFriendRequestedFragmentBinding binding;
    private List<User> dataList;
    private friend_request_adapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendRequestedFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 리사이클러뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_adding_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 어댑터와 데이터 연결
        dataList = generateData(); // 데이터 생성
        adapter = new friend_request_adapter(dataList);
        recyclerView.setAdapter(adapter);


        wordInput();


        // 프래그먼트 2의 종료 이벤트 처리
        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프래그먼트 2 종료 후 프래그먼트 1로 돌아가기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }
    private List<User> generateData() {
        dataList = new ArrayList<>();
        // 데이터를 원하는대로 추가
        dataList.add(new User("이우혁", "7dngur7"));
        dataList.add(new User("문관록","gr1004"));
        dataList.add(new User("이종민","ljm0000"));
        dataList.add(new User("김김김","0107"));
        dataList.add(new User("육육육","0531"));
        dataList.add(new User("민민민","0202"));
        // ...
        return dataList;
    }


    private void wordInput(){
        binding.edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }
    private void filter(String text){
        List<User> filteredList = new ArrayList<>();

        for(User item : dataList){
            if(item.getId().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList((ArrayList<User>) filteredList);
    }

}