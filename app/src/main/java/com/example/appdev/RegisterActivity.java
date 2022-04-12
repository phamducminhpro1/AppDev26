package com.example.appdev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
/*
This activity registers user accounts
The user can fill in their information and choose an account type
The account is saved to the firebase database
 */
public class RegisterActivity extends AppCompatActivity implements CodeDialog.CodeDialogListener{

    private EditText editEmail, editPassword, editPasswordConfirm, editFirstName, editLastName;
    private RadioGroup radioGroupType;
    private RadioButton radioStudent, radioRecruiter;
    private FirebaseAuth mAuth;

    private String email, password, passwordConfirm, firstName, lastName, token;
    private User.AccountType accountType = User.AccountType.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //path to user instance
        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        editFirstName = findViewById(R.id.editTextRegisterFirstName);
        editLastName = findViewById(R.id.editTextRegisterLastName);

        radioStudent = findViewById(R.id.radioButtonStudent);
        radioRecruiter = findViewById(R.id.radioButtonRecruiter);
        radioGroupType = findViewById(R.id.radioGroupType);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Save user credentials if instance is created
    OnCompleteListener registerComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                User user = new User(mAuth.getCurrentUser().getUid(), email, firstName, lastName, accountType, token);

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "User has been registered successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, SplashActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to register, try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this,
                        "Failed to register, possibly already registered.", Toast.LENGTH_LONG).show();
            }

        }
    };

    //Create a unique token
    private void saveToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            token = "";
                            return;
                        }
                        if(task.getResult() != null){
                            token = task.getResult();
                        }else{
                            token = "";
                        }
                    }
                });
    }

    //Check if field has content
    public boolean emptyField(String text, EditText field) {
        if (text.isEmpty()) {
            field.setError("This field is required!");
            field.requestFocus();
            return true;
        }

        return false;
    }

    //Check user
    public void registerAccount(){

        email = editEmail.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        passwordConfirm = editPasswordConfirm.getText().toString().trim();
        firstName = editFirstName.getText().toString().trim();
        lastName = editLastName.getText().toString().trim();

        //Set account type
        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        //Check whether every field is filled
        if (emptyField(email, editEmail) || emptyField(firstName, editFirstName)
                || emptyField(lastName, editLastName) || emptyField(password, editPassword)
                || emptyField(passwordConfirm, editPasswordConfirm)) {
            return;
        }

        //Check whether email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Invalid e-mail address!");
            editEmail.requestFocus();
            return;
        }

        //Check whether account type is selected
        if (accountType == User.AccountType.NONE) {
            radioStudent.setError("Select an account type!");
            radioRecruiter.setError("Select an account type!");
            radioGroupType.requestFocus();
            return;
        }

        //Check whether passwords are equal
        if (!password.equals(passwordConfirm)) {
            editPassword.setError("Passwords are not the same!");
            editPassword.requestFocus();
            return;
        }

        //Password requirements
        // Regex from:
        // https://stackoverflow.com/questions/40336374/how-do-i-check-if-a-java-string-contains-at-least-one-capital-letter-lowercase
        if (password.length() < 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            editPassword.setError("Password should be at least 8 characters, contain a number, and both uppercase and lowercase letters!");
            editPassword.requestFocus();
            return;
        }

        //Save created token to database
        saveToken();

        //Create instance in database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerComplete);

    }

    //register button on click
    public void onRegister(View view) {
        openDialog();
    }

    //Create a dialog
    private void openDialog() {
        CodeDialog codeDialog = new CodeDialog();
        codeDialog.show(getSupportFragmentManager(), "code dialog");
    }

    //Receive access code through interface from dialog
    @Override
    public void sendCode(String code) {
        if(code.equals("12345678")){
            registerAccount();
        }else{
            Toast.makeText(RegisterActivity.this, "Incorrect access code", Toast.LENGTH_LONG).show();
        }
    }

    //On Facebook button click, go to Facebook website
    public void toFacebook(View view) {
        android.widget.Button UrlOpen = findViewById(R.id.button);

        UrlOpen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent GetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(GetIntent);
            }
        });
    }

    //On TUE button click, go to TUE website
    public void toTUE(View view) {
        android.widget.Button UrlOpen = findViewById(R.id.button2);

        UrlOpen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent GetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tue.nl"));
                startActivity(GetIntent);
            }
        });
    }
}