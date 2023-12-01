package com.example.chatting2;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageItem {
    //파이어베이스에서 건드릴 수 있도록 반드시 퍼블릭이어야한다
    private String mName;
    private String mMessage;
    private String mUid;
//    private Date mTimestamp;

    public MessageItem() { } // Needed for Firebase

    public MessageItem(String name, String message, String uid, Date time) {
        mName = name;
        mMessage = message;
        mUid = uid;
//        mTimestamp = time;
    }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

    public String getMessage() { return mMessage; }

    public void setMessage(String message) { mMessage = message; }

    public String getUid() { return mUid; }

    public void setUid(String uid) { mUid = uid; }

//    @ServerTimestamp
//    public Date getTime() { return mTimestamp; }
//
//    public void setTimestamp(Date timestamp) { mTimestamp = timestamp; }


}
