package com.example.appdev;

public class Message {
    public String sender, receiver, text;

    public Message() {

    }

    public Message(String sender, String receiver, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
    }
}
