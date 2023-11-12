package com.example.myapplication.friend_list;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;

import com.example.myapplication.R;

public class friend_received extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 타이틀을 없애기 위해 사용
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 파일 이름 없이 화면만 띄우는 레이아웃을 설정
        setContentView(R.layout.activity_friend_received);

        // 다이얼로그의 배경을 투명하게 하려면 아래 코드를 추가할 수 있습니다.
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}