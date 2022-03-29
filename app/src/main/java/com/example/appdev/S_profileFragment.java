package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class S_profileFragment extends profileFragment implements fragmentDialog.OnInputCorrect, fragmentDialog.OnInputCancel{

    private static final String TAG = "StudentProfile";
    private Spinner spinnerProgram;
    private Spinner spinnerYears;

    public S_profileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerProgram = view.findViewById(R.id.spinnerProgram);
        spinnerYears = view.findViewById(R.id.spinnerYear);

        radioRecruiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentDialog dialog = new fragmentDialog();
                dialog.setTargetFragment(S_profileFragment.this, 1);
                dialog.show(getFragmentManager(), "fragmentDialog");
            }
        });

        initializeFields();
    }

    @Override
    public void initializeFields() {
        // Add the options for the program.
        // TODO: Maybe we complete this with all programs.
        String[] programs = new String[]{
                "None",
                "Bachelor Applied Mathematics",
                "Bachelor Applied Physics",
                "Bachelor Architecture, Urbanism and Building Sciences",
                "Bachelor Automotive Technology",
                "Bachelor Biomedical Engineering",
                "Bachelor Chemical Engineering and Chemistry",
                "Bachelor Computer Science and Engineering",
                "Bachelor Data Science",
                "Bachelor Electrical Engineering",
                "Bachelor Industrial Design",
                "Bachelor Industrial Engineering",
                "Bachelor Mechanical Engineering",
                "Bachelor Medical Sciences and Technology",
                "Bachelor Psychology and Technology",
                "Bachelor Sustainable Innovation"
        };

        String[] years = new String[]{
                "Year 1",
                "Year 2",
                "Year 3",
                "Year 4",
                "Year 5+"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, programs);
        spinnerProgram.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, years);
        spinnerYears.setAdapter(adapter2);

        String userID = mAuth.getUid();
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // Initialize all fields empty.
                initBaseFields(userProfile);
                spinnerProgram.setSelection(0);
                spinnerYears.setSelection(0);

                // If the user already has existing info, we load that in.
                if (userProfile != null) {
                    if (userProfile.accountType == User.AccountType.RECRUITER
                            && getActivity() != null) {
                        Intent intent = new Intent(getContext(), RecruiterActivity.class);
                        intent.putExtra("toProfileR", "go");
                        startActivity(intent);
                        getActivity().finish();
                    }

                    if (userProfile.studyProgram != null) {
                        int index = adapter.getPosition(userProfile.studyProgram);
                        spinnerProgram.setSelection(index);
                    }

                    if (userProfile.studyYear != null) {
                        int index = adapter2.getPosition(userProfile.studyYear);
                        spinnerYears.setSelection(index);
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

        User.AccountType accountType = User.AccountType.NONE;

        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        if(!checkBaseFields()) {
            return false;
        }

        reference.child(userID).child("firstName").setValue(firstName);
        reference.child(userID).child("lastName").setValue(lastName);
        reference.child(userID).child("accountType").setValue(accountType);
        reference.child(userID).child("studyProgram").setValue(spinnerProgram.getSelectedItem());
        reference.child(userID).child("studyYear").setValue(spinnerYears.getSelectedItem());
        reference.child(userID).child("phoneNumber").setValue(phoneNumber);
        reference.child(userID).child("postalAddress").setValue(postalAddress);
        reference.child(userID).child("postalCode").setValue(postalCode);
        reference.child(userID).child("city").setValue(city);

        return true;
    }

    public void onSaveChangesSwitchToR() {
        if (!onSaveChanges()) {
            radioRecruiter.setChecked(false);
            radioStudent.setChecked(true);
        }
    }

    @Override
    public void sendCode(String code) {
        if(code.equals("12345678")){
            onSaveChangesSwitchToR();
            Toast.makeText(getActivity(), "Successfully Switched account type", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void sendCancel(boolean Cancel) {
        if (Cancel == true){
            radioRecruiter.setChecked(false);
            radioStudent.setChecked(true);
        }
    }
}