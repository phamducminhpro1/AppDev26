package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

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
                            Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, SplashActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register, try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
    };

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

    public boolean emptyField(String text, EditText field) {
        if (text.isEmpty()) {
            field.setError("This field is required!");
            field.requestFocus();
            return true;
        }

        return false;
    }

    public void registerAccount(){

        email = editEmail.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        passwordConfirm = editPasswordConfirm.getText().toString().trim();
        firstName = editFirstName.getText().toString().trim();
        lastName = editLastName.getText().toString().trim();

        if (radioStudent.isChecked()) {
            accountType = User.AccountType.STUDENT;
        } else if (radioRecruiter.isChecked()) {
            accountType = User.AccountType.RECRUITER;
        }

        if (emptyField(email, editEmail) || emptyField(firstName, editFirstName)
                || emptyField(lastName, editLastName) || emptyField(password, editPassword)
                || emptyField(passwordConfirm, editPasswordConfirm)) {
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Invalid e-mail address!");
            editEmail.requestFocus();
            return;
        }

        if (accountType == User.AccountType.NONE) {
            radioStudent.setError("Select an account type!");
            radioRecruiter.setError("Select an account type!");
            radioGroupType.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            editPassword.setError("Passwords are not the same!");
            editPassword.requestFocus();
            return;
        }

        // Regex from:
        // https://stackoverflow.com/questions/40336374/how-do-i-check-if-a-java-string-contains-at-least-one-capital-letter-lowercase
        if (password.length() < 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            editPassword.setError("Password should be at least 8 characters, contain a number, and both uppercase and lowercase letters!");
            editPassword.requestFocus();
            return;
        }

        saveToken();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerComplete);

    }

    public void onRegister(View view) {
        openDialog();
    }

    private void openDialog() {
        CodeDialog codeDialog = new CodeDialog();
        codeDialog.show(getSupportFragmentManager(), "code dialog");
    }



    @Override
    public void sendCode(String code) {
        if(code.equals("12345678")){
            registerAccount();
        }else{
            Toast.makeText(RegisterActivity.this, "Incorrect access code", Toast.LENGTH_LONG).show();
        }
    }

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