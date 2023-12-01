package com.example.chatting;

public class MessageItem {
    //파이어베이스에서 건드릴 수 있도록 반드시 퍼블릭이어야한다
    public String name;
    public String message;
//    public String profileUrl;
    public String time;

    //파이어베어스에선 무조건 빈 생성자 하나 모두 다 받는 거 하나 2개 만들어야함
    public MessageItem() {
    }

    public MessageItem(String name, String message, String time) {
        this.name = name;
        this.message = message;
//        this.profileUrl = profileUrl;
        this.time = time;
    }
}
