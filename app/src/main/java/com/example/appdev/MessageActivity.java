package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbarChat;
    FloatingActionButton buttonSend;
    EditText textMessage;

    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference reference;

    Intent intent;

    String selfId, otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        intent = getIntent();
        otherId = intent.getStringExtra("userid");

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        selfId = fUser.getUid();

        textMessage = findViewById(R.id.editTextMessage);
        toolbarChat = findViewById(R.id.toolbarChat);
        toolbarChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSend = findViewById(R.id.buttonSendMessage);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = textMessage.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(selfId, otherId, msg);
                }

                textMessage.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(otherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String displayName = user.firstName + " " + user.lastName;
                toolbarChat.setTitle(displayName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatsKay");

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);

        chatReference.push().setValue(map);
    }
}