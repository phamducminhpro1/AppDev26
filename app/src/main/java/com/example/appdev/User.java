package com.example.appdev;

public class User {

    public enum AccountType {
        NONE, STUDENT, RECRUITER, ADMIN
    }

    public String emailAddress, firstName, lastName, phoneNumber, postalAddress, studyProgram;
    public AccountType accountType;

    public User() {
        this.accountType = AccountType.NONE;
    }

    public User(String emailAddress, String firstName, String lastName, AccountType accountType) {
        this.accountType = accountType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }
}
