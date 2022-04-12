package com.example.appdev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private Toolbar toolbarChat;
    private TextView textUsername;
    private CircleImageView imageProfile;
    private FloatingActionButton buttonSend, buttonImage, buttonFile, buttonAttach, buttonCamera;
    private EditText textMessage;

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private DatabaseReference reference;

    private FileUploader fileUploader;

    private MessageAdapter messageAdapter;
    private List<Message> mChat;
    private RecyclerView recyclerView;
    private ExpandableLayout expandableAttachments;

    private Intent intent;

    private String selfId, otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        intent = getIntent();
        otherId = intent.getStringExtra("userid");

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        selfId = fUser.getUid();

        fileUploader = new FileUploader(this, this.getActivityResultRegistry());
        getLifecycle().addObserver(fileUploader);

        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        expandableAttachments = findViewById(R.id.expandableAttachments);

        imageProfile = findViewById(R.id.imageProfile);
        textUsername = findViewById(R.id.textUsername);

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
                    sendMessage(selfId, otherId, msg, "", "");
                }

                textMessage.setText("");
            }
        });

        buttonImage = findViewById(R.id.buttonSendImage);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openFile("image/*", imageComplete);
                expandableAttachments.collapse();
            }
        });

        buttonFile = findViewById(R.id.buttonSendFile);
        buttonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openFile("application/pdf", pdfComplete);
                expandableAttachments.collapse();
            }
        });

        buttonCamera = findViewById(R.id.buttonOpenCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openCamera(imageComplete);
                expandableAttachments.collapse();
            }
        });

        buttonAttach = findViewById(R.id.buttonAttach);
        buttonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAttach();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(otherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String displayName = user.firstName + " " + user.lastName;
                textUsername.setText(displayName);
                textUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(MessageActivity.this, ViewProfileActivity.class);
                        profileIntent.putExtra("userid", otherId);
                        startActivity(profileIntent);
                    }
                });

                imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
                if (user.imageUrl != null) {
                    if (!user.imageUrl.equals("")) {
                        Glide.with(getApplicationContext()).load(user.imageUrl).into(imageProfile);
                        imageProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MessageActivity.this, FullscreenImageActivity.class);
                                intent.putExtra("imageUrl", user.imageUrl);
                                startActivity(intent);
                            }
                        });
                    }
                }

                readMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    OnCompleteListener<Uri> imageComplete = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String msgImageUrl = downloadUri.toString();
                sendMessage(selfId, otherId, "", msgImageUrl, "");
            }
        }
    };

    OnCompleteListener<Uri> pdfComplete = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String msgFileUrl = downloadUri.toString();
                sendMessage(selfId, otherId, "", "", msgFileUrl);
            }
        }
    };

    private void onAttach() {
        if (expandableAttachments.isExpanded()) {
            expandableAttachments.collapse();
        } else {
            expandableAttachments.expand();
        }
    }

    private void sendMessage(String sender, String receiver, String message, String imageUrl, String fileUrl) {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chats");

        LocalDateTime now = java.time.LocalDateTime.now();

        Message msg = new Message(sender, receiver, message, imageUrl, fileUrl,
                now.getDayOfMonth() + "-" + now.getMonthValue(), now.getHour() + ":" + now.getMinute());
        chatReference.push().setValue(msg);
    }

    private void readMessages() {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
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