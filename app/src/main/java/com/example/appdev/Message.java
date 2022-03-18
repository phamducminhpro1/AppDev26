package com.example.appdev;

public class Message {
    public String sender, receiver, text;
    public String imageUrl, fileUrl;
    public String date, time;

    public Message() {

    }

    public Message(String sender, String receiver, String text, String imageUrl, String fileUrl, String date, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.date = date;
        this.time = time;
    }
}
