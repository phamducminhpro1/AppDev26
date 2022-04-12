package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/*
This fragment is where the RECRUITER users can change their account information.
They also can switch their account type to student, log out or completely delete their account
 */
public class R_profileFragment extends profileFragment implements fragmentDialog.OnInputCorrect, fragmentDialog.OnInputCancel{

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

        // Get the UI elements from the view.
        editCompany = view.findViewById(R.id.companyName);
        spinnerCompany = view.findViewById(R.id.spinnerSector);

        //If the user selects the student profile, the code dialog will appear
        radioStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentDialog dialog = new fragmentDialog();
                dialog.setTargetFragment(R_profileFragment.this, 1);
                dialog.show(getFragmentManager(), "fragmentDialog");
            }
        });

        initializeFields();
    }


    //Initialize all fields.
    // If the database already contains information, it will be loaded into the correct field
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

        //Get user id
        String userID = mAuth.getUid();
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // If their profile is null, return as we can not read anything from the database.
                if (userProfile == null) {
                    return;
                }

                // Initialize all fields empty.
                initBaseFields(userProfile);
                spinnerCompany.setSelection(0);
                editCompany.setText("");

                // If the user already has existing info, we load that in.
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Save all fields to the correct user instance in the database
    public boolean onSaveChanges() {
        String userID = mAuth.getUid();

        // Store all the fields as strings.
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String postalAddress = editPostalAddress.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        String postalCode = editPostalCode.getText().toString();
        String city = editCity.getText().toString();
        String company = editCompany.getText().toString();

        // Store their accountType based on the radio buttons.
        User.AccountType accountType = User.AccountType.NONE;
        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        // Check all the fields shared between recruiters and students.
        if (!checkBaseFields()) {
            return false;
        }

        // A recruiter should have a company they work for.
        if (company.isEmpty()) {
            editCompany.setError("Company name required!");
            editCompany.requestFocus();
            return false;
        }

        //Save instances to database
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

    //If the user switches to the student account
    // their changed data will be saved before making the account switch
    public void onSaveChangesSwitchToS() {
        if (!onSaveChanges()) {
            radioRecruiter.setChecked(true);
            radioStudent.setChecked(false);
            Toast.makeText(getActivity(), "Successfully Switched account type", Toast.LENGTH_LONG).show();
        }
    }

    //Receive the filled in access code from the dialog
    @Override
    public void sendCode(String code) {
        if(code.equals("12345678")){
        onSaveChangesSwitchToS();
        Toast.makeText(getActivity(), "Successfully Switched account type", Toast.LENGTH_LONG).show();
        }
    }

    //Receive the cancel status from the dialog
    @Override
    public void sendCancel(boolean Cancel) {
        if (Cancel == true){
            radioRecruiter.setChecked(true);
            radioStudent.setChecked(false);
        }
    }
}