package com.example.appdev;

/*
    This class is used for storing a message in the database.
    Depending on the type of message, either text, imageUrl or fileUrl will be set.
 */
public class Message {
    public String sender, receiver, text;
    public String imageUrl, fileUrl;
    public String date, time;

    public Message() {
        // Leave this here for the database.
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
