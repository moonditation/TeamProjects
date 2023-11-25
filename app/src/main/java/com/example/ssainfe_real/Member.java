package com.example.ssainfe_real;


import com.naver.maps.geometry.LatLng;

public class Member {
    private String name;
    private LatLng latLng;

    // 생성자(Constructor)
    public Member(String name, double latitude, double longitude) {
        this.name = name;
        this.latLng = new LatLng(latitude, longitude);
    }

    // Getter 메서드
    public String getName() {
        return name;
    }

    public LatLng getLatlang() {
        return latLng;
    }


}
