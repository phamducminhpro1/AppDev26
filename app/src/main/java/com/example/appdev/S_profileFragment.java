package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link S_profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class S_profileFragment extends Fragment {

    private EditText editFirstName, editLastName, editPostalAddres, editPhoneNumber;
    private RadioGroup radioGroupType;
    private RadioButton radioStudent, radioRecruiter;
    private Spinner spinnerProgram;
    private DatabaseReference reference;
    private  FirebaseAuth mAuth;

    public S_profileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        editFirstName = view.findViewById(R.id.editTextFirstName);
        editLastName = view.findViewById(R.id.editTextLastName);
        editPostalAddres = view.findViewById(R.id.editTextPostalAddress);
        editPhoneNumber = view.findViewById(R.id.editTextPhone);

        radioStudent = view.findViewById(R.id.radioButtonStudent);
        radioRecruiter = view.findViewById(R.id.radioButtonRecruiter);
        radioGroupType = view.findViewById(R.id.radioGroupType);

        spinnerProgram = view.findViewById(R.id.spinnerProgram);

        Button saveButton = view.findViewById(R.id.buttonSaveChanges);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveChanges();
            }
        });

        Button discardButton = view.findViewById(R.id.buttonDiscard);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeFields();
            }
        });

        Button logoutButton = view.findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        initializeFields();

        return view;
    }

    public void initializeFields() {
        // Add the options for the program.
        // TODO: Maybe we complete this with all programs.
        String[] programs = new String[]{
                "None",
                "Bachelor Applied Mathematics",
                "Bachelor Computer Science and Engineering",
                "Bachelor Data Science",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, programs);
        spinnerProgram.setAdapter(adapter);

        String userID = mAuth.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // Initialize all fields empty.
                editFirstName.setText("");
                editLastName.setText("");
                editPhoneNumber.setText("");
                editPostalAddres.setText("");
                spinnerProgram.setSelection(0);

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

    public void onSaveChanges() {
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
    }
}