package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editPostalAddres, editPhoneNumber;
    private RadioGroup radioGroupType;
    private RadioButton radioStudent, radioRecruiter;
    private Spinner spinnerProgram;
    private DatabaseReference reference;
    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        editFirstName = findViewById(R.id.editTextFirstName);
        editLastName = findViewById(R.id.editTextLastName);
        editPostalAddres = findViewById(R.id.editTextPostalAddress);
        editPhoneNumber = findViewById(R.id.editTextPhone);

        radioStudent = findViewById(R.id.radioButtonStudent);
        radioRecruiter = findViewById(R.id.radioButtonRecruiter);
        radioGroupType = findViewById(R.id.radioGroupType);

        // Add the options for the program.
        // TODO: Maybe we complete this with all programs.
        spinnerProgram = findViewById(R.id.spinnerProgram);
        String[] programs = new String[]{
                "None",
                "Bachelor Applied Mathematics",
                "Bachelor Computer Science and Engineering",
                "Bachelor Data Science",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, programs);
        spinnerProgram.setAdapter(adapter);

        String userID = mAuth.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // If the user already has existing info, we load that in.
                if (userProfile != null) {
                    if (userProfile.firstName != null) {
                        editFirstName.setText(userProfile.firstName);
                    }

                    if (userProfile.lastName != null) {
                        editLastName.setText(userProfile.lastName);
                    }

                    if (userProfile.phoneNumber != null) {
                        editPhoneNumber.setText(userProfile.phoneNumber);
                    }

                    if (userProfile.postalAddress != null) {
                        editPostalAddres.setText(userProfile.postalAddress);
                    }

                    if (userProfile.accountType != null) {
                        if (userProfile.accountType == User.AccountType.STUDENT) {
                            radioGroupType.check(radioStudent.getId());
                        } else if (userProfile.accountType == User.AccountType.RECRUITER) {
                            radioGroupType.check(radioRecruiter.getId());
                        }
                    }

                    if (userProfile.studyProgram != null) {
                        int index = adapter.getPosition(userProfile.studyProgram);
                        spinnerProgram.setSelection(index);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onDiscardChanges(View view) {
        startActivity(new Intent(this, JobsActivity.class));
        finish();
    }

    public void onSaveChanges(View view) {
        String userID = mAuth.getUid();
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String postalAddress = editPostalAddres.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();

        User.AccountType accountType = User.AccountType.NONE;

        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        if (firstName.isEmpty()) {
            editFirstName.setError("First name required!");
            editFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            editLastName.setError("Last name required!");
            editLastName.requestFocus();
            return;
        }

        if (accountType == User.AccountType.NONE) {
            radioStudent.setError("Select an account type!");
            radioRecruiter.setError("Select an account type!");
            radioGroupType.requestFocus();
            return;
        }

        if (!phoneNumber.isEmpty()) {
            if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                editPhoneNumber.setError("Invalid phone number!");
                editPhoneNumber.requestFocus();
                return;
            }
        }

        reference.child(userID).child("firstName").setValue(firstName);
        reference.child(userID).child("lastName").setValue(lastName);
        reference.child(userID).child("accountType").setValue(accountType);
        reference.child(userID).child("studyProgram").setValue(spinnerProgram.getSelectedItem());
        reference.child(userID).child("phoneNumber").setValue(phoneNumber);
        reference.child(userID).child("postalAddress").setValue(postalAddress);

        startActivity(new Intent(this, JobsActivity.class));
        finish();
    }
}