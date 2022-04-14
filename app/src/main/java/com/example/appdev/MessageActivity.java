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

    private TextView textUsername;
    private CircleImageView imageProfile;
    private EditText textMessage;

    private DatabaseReference reference;

    // Used to upload files and access the camera.
    private FileUploader fileUploader;

    // Used for creating the list of all message
    private MessageAdapter messageAdapter;
    private List<Message> mChat;
    private RecyclerView recyclerView;

    // Creates the pop up effect for the attachment menu
    private ExpandableLayout expandableAttachments;

    // The ids of the user in the chat.
    private String selfId, otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get the id of the other user from the intent.
        Intent intent = getIntent();
        otherId = intent.getStringExtra("userid");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        selfId = fUser.getUid();

        // Initialize the file uploader.
        fileUploader = new FileUploader(this, this.getActivityResultRegistry());
        getLifecycle().addObserver(fileUploader);

        // Set up the recycler view which will show all the messages in the chat.
        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Get all the UI elements from the view.
        expandableAttachments = findViewById(R.id.expandableAttachments);
        imageProfile = findViewById(R.id.imageProfile);
        textUsername = findViewById(R.id.textUsername);
        textMessage = findViewById(R.id.editTextMessage);

        // Allow the user to close the chat by pressing the back button.
        Toolbar toolbarChat = findViewById(R.id.toolbarChat);
        toolbarChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // When clicking on the send button, send the message with the text from the text field.
        FloatingActionButton buttonSend = findViewById(R.id.buttonSendMessage);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = textMessage.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(selfId, otherId, msg, "", "");
                }

                // Reset the text field after sending the message.
                textMessage.setText("");
            }
        });

        // When pressing the image button, open the file uploader so the user can select an image.
        FloatingActionButton buttonImage = findViewById(R.id.buttonSendImage);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openFile("image/*", imageComplete);
                expandableAttachments.collapse();
            }
        });

        // When pressing the file button, open the file uploader so the user can select an file.
        FloatingActionButton buttonFile = findViewById(R.id.buttonSendFile);
        buttonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openFile("application/pdf", pdfComplete);
                expandableAttachments.collapse();
            }
        });

        // When pressing the camera button, open the camera so the user can take an image.
        FloatingActionButton buttonCamera = findViewById(R.id.buttonOpenCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openCamera(imageComplete);
                expandableAttachments.collapse();
            }
        });

        // Expand or collapse the attachment menu.
        FloatingActionButton buttonAttach = findViewById(R.id.buttonAttach);
        buttonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAttach();
            }
        });

        // Get the information of the other user.
        reference = FirebaseDatabase.getInstance().getReference("Users").child(otherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String displayName = user.firstName + " " + user.lastName;

                // When clicking on the other users name in the top bar,
                // the user should be taken to the profile page of the user they're chatting with.
                textUsername.setText(displayName);
                textUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(MessageActivity.this, ViewProfileActivity.class);
                        profileIntent.putExtra("userid", otherId);
                        startActivity(profileIntent);
                    }
                });

                // Load the profile picture of the other user if they have one.
                imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
                if (user.imageUrl != null) {
                    if (!user.imageUrl.equals("")) {
                        Glide.with(getApplicationContext()).load(user.imageUrl).into(imageProfile);

                        // When clicking on the profile picture, take the user to a full screen view of the picture.
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

                // Read all the messages into the chat.
                readMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Unable to read from the database.
            }
        });
    }

    // After uploading an image, send this image to the other user.
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

    // After uploading a pdf, send it to the other user.
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

    // Expand or collapse the attachment menu.
    private void onAttach() {
        if (expandableAttachments.isExpanded()) {
            expandableAttachments.collapse();
        } else {
            expandableAttachments.expand();
        }
    }

    // Send a message with the current time attached to it.
    private void sendMessage(String sender, String receiver, String message, String imageUrl, String fileUrl) {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chats");

        LocalDateTime now = java.time.LocalDateTime.now();

        Message msg = new Message(sender, receiver, message, imageUrl, fileUrl,
                now.getDayOfMonth() + "-" + now.getMonthValue(), now.getHour() + ":" + now.getMinute());
        chatReference.push().setValue(msg);
    }

    // Reads all the messages that have been send between these users.
    private void readMessages() {
        mChat = new ArrayList<>();

        // Get a reference to all the messages in the database.
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Start with an empty list of messages.
                mChat.clear();

                // Loop through the messages.
                for (DataSnapshot s : snapshot.getChildren()) {
                    Message msg = s.getValue(Message.class);

                    // If this message was sent between these users, add it to the list.
                    if (msg.receiver.equals(selfId) && msg.sender.equals(otherId) ||
                            msg.receiver.equals(otherId) && msg.sender.equals(selfId)) {
                        mChat.add(msg);
                    }
                }

                // Update the adapter to show all the collected messages in the chat.
                messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Unable to read from the database.
            }
        });
    }
}