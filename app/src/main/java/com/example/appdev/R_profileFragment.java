package com.example.appdev;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class R_profileFragment extends profileFragment {

    private EditText editCompany;
    private Spinner spinnerCompany;

    public R_profileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editCompany = view.findViewById(R.id.companyName);
        spinnerCompany = view.findViewById(R.id.spinnerSector);

        radioStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveChangesSwitchToS();
            }
        });

        initializeFields();
    }

    @Override
    public void initializeFields() {
        // Add the options for the sectors.
        String[] Sectors = new String[]{
                "None",
                "Healthcare",
                "Logistics",
                "IT",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, Sectors);
        spinnerCompany.setAdapter(adapter);

        String userID = mAuth.getUid();
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // Initialize all fields empty.
                initBaseFields(userProfile);
                spinnerCompany.setSelection(0);
                editCompany.setText("");

                // If the user already has existing info, we load that in.
                if (userProfile != null) {
                    if (userProfile.accountType == User.AccountType.STUDENT
                            && getActivity() != null) {
                        Intent intent = new Intent(getActivity(), StudentActivity.class);
                        intent.putExtra("toProfileS", "go");
                        startActivity(intent);
                        getActivity().finish();
                    }

                    if (userProfile.sector != null) {
                        int index = adapter.getPosition(userProfile.sector);
                        spinnerCompany.setSelection(index);
                    }

                    if (userProfile.company != null) {
                        editCompany.setText(userProfile.company);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onSaveChanges() {
        String userID = mAuth.getUid();
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String postalAddress = editPostalAddress.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        String postalCode = editPostalCode.getText().toString();
        String city = editCity.getText().toString();
        String company = editCompany.getText().toString();

        User.AccountType accountType = User.AccountType.NONE;
        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        if (!checkBaseFields()) {
            return false;
        }

        if (company.isEmpty()) {
            editCompany.setError("Company name required!");
            editCompany.requestFocus();
            return false;
        }

        reference.child(userID).child("firstName").setValue(firstName);
        reference.child(userID).child("lastName").setValue(lastName);
        reference.child(userID).child("accountType").setValue(accountType);
        reference.child(userID).child("phoneNumber").setValue(phoneNumber);
        reference.child(userID).child("postalAddress").setValue(postalAddress);
        reference.child(userID).child("postalCode").setValue(postalCode);
        reference.child(userID).child("city").setValue(city);
        reference.child(userID).child("sector").setValue(spinnerCompany.getSelectedItem());
        reference.child(userID).child("company").setValue(company);

        return true;
    }

    public void onSaveChangesSwitchToS() {
        if (!onSaveChanges()) {
            radioRecruiter.setChecked(true);
            radioStudent.setChecked(false);
        }
    }
}