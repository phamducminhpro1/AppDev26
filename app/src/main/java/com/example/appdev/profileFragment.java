package com.example.appdev;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

// As both S_profileFragment and R_profileFragment are similar,
// this abstract class profileFragment describes all common attributes
public abstract class profileFragment extends Fragment {

    // All fields which are shared between students and recruiters.
    EditText editFirstName, editLastName, editPostalAddress, editPhoneNumber, editPostalCode, editCity;
    ImageView imageProfile;
    RadioGroup radioGroupType;
    RadioButton radioStudent, radioRecruiter;

    DatabaseReference reference;
    FirebaseAuth mAuth;
    String userId;

    // Store the original account type so we can keep track of switching accounts.
    User.AccountType originalType;

    // Used for uploading profile pictures.
    FileUploader fileUploader;

    public profileFragment() {
        // Required empty public constructor
    }

    //Identify fields, set up profile picture and buttons
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        // Store the users id.
        userId = mAuth.getCurrentUser().getUid();

        // Set up the file uploader which will be used for uploading a profile picture.
        fileUploader = new FileUploader(requireContext(), requireActivity().getActivityResultRegistry());
        getLifecycle().addObserver(fileUploader);

        // Find all the fields.
        editFirstName = view.findViewById(R.id.editTextFirstName);
        editLastName = view.findViewById(R.id.editTextLastName);
        editPostalAddress = view.findViewById(R.id.editTextPostalAddress);
        editPhoneNumber = view.findViewById(R.id.editTextPhone);
        editPostalCode = view.findViewById(R.id.editTextZip);
        editCity = view.findViewById(R.id.editTextCity);

        radioStudent = view.findViewById(R.id.radioButtonStudent);
        radioRecruiter = view.findViewById(R.id.radioButtonRecruiter);
        radioGroupType = view.findViewById(R.id.radioGroupType);

        // On clicking the profile picture use the file uploader.
        imageProfile = view.findViewById(R.id.imageProfile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { fileUploader.openFile("image/*", onImageUpload); }
        });

        //save button
        Button saveButton = view.findViewById(R.id.buttonSaveChanges);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveChanges();
            }
        });

        //discard button
        Button discardButton = view.findViewById(R.id.buttonDiscard);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeFields();
            }
        });

        //logout button
        Button logoutButton = view.findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        //Delete profile button
        Button deleteButton = view.findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First ask for confirmation before deleting their account.
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to delete this account?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                // If they respond positively, delete their account.
                                onDelete();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    // Select image and upload to database
    OnCompleteListener<Uri> onImageUpload = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String mUri = downloadUri.toString();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                HashMap<String, Object> map = new HashMap<>();
                map.put("imageUrl", mUri);
                reference.updateChildren(map);
            }
        }
    };

    // Abstract functions so the specific student and recruiter implementations can
    // initialize their fields as well.
    public abstract void initializeFields();

    //Initialize all shared fields
    //If a value is already in the database, it is loaded in
    public void initBaseFields(User userProfile) {
        // Initialize all fields empty.
        editFirstName.setText("");
        editLastName.setText("");
        editPhoneNumber.setText("");
        editPostalAddress.setText("");
        editPostalCode.setText("");
        editCity.setText("");

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

        // By default show the profile icon
        imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
        // If the user has a profile picture, we use this instead.
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

    // Abstract function for saving all fields to the database.
    public abstract boolean onSaveChanges();

    //Check whether all fields are correctly filled
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

    //Delete all chats with the current user
    public void deleteMessages() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Message msg = s.getValue(Message.class);

                    // Delete any message sent or received by the user.
                    if (msg.receiver.equals(userId) || msg.sender.equals(userId)) {
                        chatRef.child(s.getKey()).removeValue();
                    }
                }

                deleteApplicationAndPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Delete all job applications and posts by the current user
    public void deleteApplicationAndPosts() {
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("Jobs");
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Job job = s.getValue(Job.class);

                    // Remove any job they have posted.
                    if (job.posterId.equals(userId)) {
                        jobRef.child(job.id).removeValue();
                        continue;
                    }

                    // Remove them from any job they have applied to.
                    if (job.appliedStudents.contains(userId)) {
                        job.appliedStudents.remove(userId);
                        jobRef.child(job.id).setValue(job);
                    }
                }

                // Now that all the information has been deleted, delete the account completely.
                deleteAccount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Delete current user account
    private void deleteAccount() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(userId).removeValue();
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Make sure to sign the user out before deleting their account.
                // Not doing this means they will be signed in to an invalid account.
                mAuth.signOut();
                getActivity().finish();
            }
        });
    }

    // When deleting, start with all of the messages.
    public void onDelete() {
        deleteMessages();
    }

}