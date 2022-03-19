package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbarChat;
    TextView textUsername;
    CircleImageView imageProfile;
    FloatingActionButton buttonSend, buttonImage, buttonFile;
    EditText textMessage;

    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference reference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private static final int FILE_REQUEST = 2;
    private Uri fileUri;
    private StorageTask uploadTask;
    private ProgressDialog pd;

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

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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
                openFile("image/*", IMAGE_REQUEST);
            }
        });

        buttonFile = findViewById(R.id.buttonSendFile);
        buttonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile("application/pdf", FILE_REQUEST);
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

    public void openFile(String fileType, int code) {
        Intent intent = new Intent();
        intent.setType(fileType);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, code);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(OnCompleteListener<Uri> onCompleteListener) {
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (fileUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(fileUri));
            uploadTask = fileReference.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(onCompleteListener);
        }
    }

    OnCompleteListener<Uri> imageComplete = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String msgImageUrl = downloadUri.toString();
                sendMessage(selfId, otherId, "", msgImageUrl, "");
                pd.dismiss();
            } else {
                Toast.makeText(MessageActivity.this, "Failed to upload image", Toast.LENGTH_LONG);
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
                pd.dismiss();
            } else {
                Toast.makeText(MessageActivity.this, "Failed to upload file", Toast.LENGTH_LONG);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            fileUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Uploading file...", Toast.LENGTH_LONG);
            } else {
                if (requestCode == IMAGE_REQUEST) {
                    uploadFile(imageComplete);
                } else if (requestCode == FILE_REQUEST) {
                    uploadFile(pdfComplete);
                }
            }
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