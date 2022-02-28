package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbarChat;
    FloatingActionButton buttonSend;
    EditText textMessage;

    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<Message> mChat;
    RecyclerView recyclerView;

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

        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


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
                readMessages();
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
        map.put("text", message);

        chatReference.push().setValue(map);
    }

    private void readMessages() {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatsKay");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Message msg = s.getValue(Message.class);
                    if (msg.receiver.equals(selfId) && msg.sender.equals(otherId) ||
                            msg.receiver.equals(otherId) && msg.sender.equals(selfId)) {
                        mChat.add(msg);
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}