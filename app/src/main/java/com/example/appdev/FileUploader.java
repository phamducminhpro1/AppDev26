package com.example.appdev;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUploader implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetContent;
    private ActivityResultLauncher<Uri> mOpenCamera;
    private ActivityResultLauncher<String> requestPermissionLauncher, requestPermissionFile;
    private Context mContext;
    private String fileType;

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

        mOpenCamera = mRegistry.register("key2", owner, new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            System.out.println("PICTURE LOADED INTO URI");
                            if (uploadTask != null && uploadTask.isInProgress()) {
                                Toast.makeText(mContext, "Uploading file...", Toast.LENGTH_LONG);
                            } else {
                                uploadFile(onComplete);
                            }
                        }
                    }
                });

        requestPermissionFile =
                mRegistry.register("key3", owner, new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        openCamera(onComplete);
                    } else {
                        return;
                    }
                });

        requestPermissionLauncher =
                mRegistry.register("key3", owner, new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openFile(fileType, onComplete);
            } else {
                return;
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        long timeStamp = System.currentTimeMillis();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir,imageFileName+".jpg");

        return image;
    }

    public void openCamera(OnCompleteListener<Uri> onComplete) {
        this.onComplete = onComplete;
        // If we don't have permission to the location yet, we should ask for it.
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            return;
        }

        fileUri = FileProvider.getUriForFile(mContext,
                mContext.getApplicationContext().getPackageName() + ".provider", photoFile);
        mOpenCamera.launch(fileUri);
    }

    public void openFile(String fileType, OnCompleteListener<Uri> onComplete) {
        this.onComplete = onComplete;
        this.fileType = fileType;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionFile.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }

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
