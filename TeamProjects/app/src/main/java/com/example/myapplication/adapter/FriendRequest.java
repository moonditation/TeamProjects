package com.example.myapplication.adapter;

public class FriendRequest {
    private String senderUid;
    private String receiverUid;
    private String status;

    public FriendRequest() {
    }

    public FriendRequest(String senderUid, String receiverUid, String status) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.status = status;
    }

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