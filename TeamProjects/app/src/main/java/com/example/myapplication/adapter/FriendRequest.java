package com.example.myapplication.adapter;

public class FriendRequest {
    private String senderUid;
    private String receiverUid;
    private String status;

    // 기본 생성자 (필수)
    public FriendRequest() {
        // Firestore에서 객체를 읽어올 때 필요
    }

    // 매개변수를 받는 생성자
    public FriendRequest(String senderUid, String receiverUid, String status) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.status = status;
    }

    // Getter 및 Setter 메서드 (필요한 경우)
    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
