package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView textFirstName, textLastName, textPostalAddress, textPhoneNumber, textPostalCode, textCity;
    private ImageView imageProfile;
    private TextView textAccountType;
    private TextView textProgram, textYear;
    private Toolbar toolbarViewProfile;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        textFirstName = findViewById(R.id.textFirstName);
        textLastName = findViewById(R.id.textLastName);
        textPostalAddress = findViewById(R.id.textPostalAddress);
        textPhoneNumber = findViewById(R.id.textPhone);
        textPostalCode = findViewById(R.id.textZip);
        textCity = findViewById(R.id.textCity);
        textAccountType = findViewById(R.id.textAccountType);

        textProgram = findViewById(R.id.textProgram);
        textYear = findViewById(R.id.textYear);

        imageProfile = findViewById(R.id.imageProfile);

        toolbarViewProfile = findViewById(R.id.toolbarViewProfile);
        toolbarViewProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeFields();
    }

    public void initializeFields() {
        String userID = getIntent().getStringExtra("userid");
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // Initialize all fields empty.
                textFirstName.setText("---");
                textLastName.setText("---");
                textPhoneNumber.setText("---");
                textPostalAddress.setText("---");
                textPostalCode.setText("---");
                textCity.setText("---");

                // If the user already has existing info, we load that in.
                if (userProfile != null) {
                    initUserProfile(userProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initUserProfile(User userProfile) {
        if (userProfile.firstName != null) {
            textFirstName.setText(userProfile.firstName);
        }

        if (userProfile.lastName != null) {
            textLastName.setText(userProfile.lastName);
        }

        if (userProfile.phoneNumber != null) {
            if (!userProfile.phoneNumber.equals("")) {
                textPhoneNumber.setText(userProfile.phoneNumber);
            }
        }

        if (userProfile.postalCode != null) {
            if (!userProfile.postalCode.equals("")) {
                textPostalCode.setText(userProfile.postalCode);
            }
        }

        if (userProfile.city != null) {
            if (!userProfile.city.equals("")) {
                textCity.setText(userProfile.city);
            }
        }

        if (userProfile.postalAddress != null) {
            if (!userProfile.postalAddress.equals("")) {
                textPostalAddress.setText(userProfile.postalAddress);
            }
        }

        if (userProfile.accountType != null) {
            textAccountType.setText(userProfile.accountType.toString());
        }

        imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
        if (userProfile.imageUrl != null) {
            if (!userProfile.imageUrl.equals("")) {
                Glide.with(ViewProfileActivity.this).load(userProfile.imageUrl).into(imageProfile);
                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewProfileActivity.this, FullscreenImageActivity.class);
                        intent.putExtra("imageUrl", userProfile.imageUrl);
                        startActivity(intent);
                    }
                });
            }
        }

        if (userProfile.studyProgram != null) {
            textProgram.setText(userProfile.studyProgram);
        }

        if (userProfile.studyYear != null) {
            textYear.setText(userProfile.studyYear);
        }

        if (userProfile.postalCode != null) {
            textPostalCode.setText(userProfile.postalCode);
        }

        if (userProfile.city != null) {
            textCity.setText(userProfile.city);
        }
    }
}