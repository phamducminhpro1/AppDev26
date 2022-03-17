package com.example.appdev;

public class Job {
    public String title, company, description, imageUrl, street, city;
    private int mImageResource;

    public Job() {

    }

    public Job(String title, String company, String description,
               String imageUrl, String street, String city) {
        this.title = title;
        this.company = company;
        this.description = description;
        this.imageUrl = imageUrl;
        this.street = street;
        this.city = city;
    }

    public Job(int mImageResource, String title, String description) {
        this.mImageResource = mImageResource;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
         return title;
    }

    public String getDescription() {
        return description;
    }

    public int getmImageResource() {
        return mImageResource;
    }
}
