package com.example.appdev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public abstract class profileFragment extends Fragment {

    EditText editFirstName, editLastName, editPostalAddress, editPhoneNumber, editPostalCode, editCity;
    ImageView imageProfile;
    RadioGroup radioGroupType;
    RadioButton radioStudent, radioRecruiter;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    User.AccountType originalType;

    FileUploader fileUploader;

    public profileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        fileUploader = new FileUploader(requireContext(), requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(fileUploader);

        editFirstName = view.findViewById(R.id.editTextFirstName);
        editLastName = view.findViewById(R.id.editTextLastName);
        editPostalAddress = view.findViewById(R.id.editTextPostalAddress);
        editPhoneNumber = view.findViewById(R.id.editTextPhone);
        editPostalCode = view.findViewById(R.id.editTextZip);
        editCity = view.findViewById(R.id.editTextCity);

        radioStudent = view.findViewById(R.id.radioButtonStudent);
        radioRecruiter = view.findViewById(R.id.radioButtonRecruiter);
        radioGroupType = view.findViewById(R.id.radioGroupType);

        imageProfile = view.findViewById(R.id.imageProfile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openFile("image/*", onImageUpload);
            }
        });

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
    }

    OnCompleteListener<Uri> onImageUpload = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String mUri = downloadUri.toString();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("imageUrl", mUri);
                reference.updateChildren(map);
            }
        }
    };

    public abstract void initializeFields();

    public void initBaseFields(User userProfile) {
        // Initialize all fields empty.
        editFirstName.setText("");
        editLastName.setText("");
        editPhoneNumber.setText("");
        editPostalAddress.setText("");
        editPostalCode.setText("");
        editCity.setText("");

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

            if (userProfile.postalCode != null) {
                editPostalCode.setText(userProfile.postalCode);
            }

            if (userProfile.city != null) {
                editCity.setText(userProfile.city);
            }

            if (userProfile.postalAddress != null) {
                editPostalAddress.setText(userProfile.postalAddress);
            }


            if (userProfile.accountType != null) {
                originalType = userProfile.accountType;
                if (userProfile.accountType == User.AccountType.STUDENT) {
                    radioGroupType.check(radioStudent.getId());
                } else if (userProfile.accountType == User.AccountType.RECRUITER) {
                    radioGroupType.check(radioRecruiter.getId());
                }
            }

            imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
            if (userProfile.imageUrl != null) {
                if (!userProfile.imageUrl.equals("") && getContext() != null) {
                    Glide.with(getContext()).load(userProfile.imageUrl).into(imageProfile);
                }
            }

            if (userProfile.postalCode != null) {
                editPostalCode.setText(userProfile.postalCode);
            }

            if (userProfile.city != null) {
                editCity.setText(userProfile.city);
            }
        }
    }

    public abstract boolean onSaveChanges();

    public boolean checkBaseFields() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        String postalCode = editPostalCode.getText().toString();

        User.AccountType accountType = User.AccountType.NONE;

        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        if (firstName.isEmpty()) {
            editFirstName.setError("First name required!");
            editFirstName.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            editLastName.setError("Last name required!");
            editLastName.requestFocus();
            return false;
        }

        if (accountType == User.AccountType.NONE) {
            radioStudent.setError("Select an account type!");
            radioRecruiter.setError("Select an account type!");
            radioGroupType.requestFocus();
            return false;
        }

        if (!phoneNumber.isEmpty()) {
            if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                editPhoneNumber.setError("Invalid phone number!");
                editPhoneNumber.requestFocus();
                return false;
            }
        }

        if (postalCode.isEmpty()) {
            editPostalCode.setError("Postal code required!");
            editPostalCode.requestFocus();
            return false;
        }

        return true;
    }
}