package com.example.appdev;

public class Message {
    public String sender, receiver, text;
    public String imageUrl, fileUrl;

    public Message() {

    }

    public Message(String sender, String receiver, String text, String imageUrl, String fileUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
    }
}
