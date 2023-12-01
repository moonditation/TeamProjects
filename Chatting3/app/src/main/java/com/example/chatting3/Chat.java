package com.example.chatting3;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.Timestamp;

public class Chat {
    private String senderName;
    private String sendMessage;
    private String userId;

    @ServerTimestamp
    private Timestamp timestamp;

    public Chat() { }

    public Chat(String name, String message, String uid) {
        senderName = name;
        sendMessage = message;
        userId = uid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @ServerTimestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
