package com.example.appdev;

public class User implements Comparable<User> {

    public enum AccountType {
        NONE, STUDENT, RECRUITER, ADMIN
    }

    public String id, emailAddress, firstName, lastName, phoneNumber, postalAddress, studyProgram, studyYear, postalCode, city, company;
    public String imageUrl;
    public AccountType accountType;

    public User() {
        this.accountType = AccountType.NONE;
    }

    public User(String id, String emailAddress, String firstName, String lastName, AccountType accountType) {
        this.id = id;
        this.accountType = accountType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

    @Override
    public int compareTo(User u) {
        if (firstName.toLowerCase().equals(u.firstName.toLowerCase())) {
            return lastName.toLowerCase().compareTo(u.lastName.toLowerCase());
        }

        return firstName.toLowerCase().compareTo(u.firstName.toLowerCase());
    }
}
