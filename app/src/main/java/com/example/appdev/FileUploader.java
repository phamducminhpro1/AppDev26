package com.example.appdev;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class FileUploader implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetContent;
    private Context mContext;

    private final StorageReference storageReference;
    private Uri fileUri;
    private StorageTask uploadTask;
    private ProgressDialog pd;
    private OnCompleteListener<Uri> onComplete;

    public FileUploader(@NonNull Context context, @NonNull ActivityResultRegistry registry) {
        this.mContext = context;
        this.mRegistry = registry;
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        System.out.println("ON CREATE FILE UPLOADER!!!");
        mGetContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        fileUri = uri;
                        if (uploadTask != null && uploadTask.isInProgress()) {
                            Toast.makeText(mContext, "Uploading file...", Toast.LENGTH_LONG);
                        } else {
                            uploadFile(onComplete);
                        }
                    }
                });
    }

    public void openFile(String fileType, OnCompleteListener<Uri> onComplete) {
        this.onComplete = onComplete;
        mGetContent.launch(fileType);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(OnCompleteListener<Uri> onCompleteListener) {
        pd = new ProgressDialog(mContext);
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
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    onCompleteListener.onComplete(task);
                    if (task.isSuccessful()) {
                        pd.dismiss();
                    } else {
                        Toast.makeText(mContext, "Failed to upload file", Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }
}
