package com.example.duofingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class profileViewDesign extends AppCompatActivity {


    ImageView profileImageBig;
    ImageView profileImageSmall;

    public static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_design);

        profileImageBig = findViewById(R.id.ProfileImageBig);

        storage =  FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://duofingo-58001.appspot.com/");

        profileImageBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean pick = true;

                if(pick == true) {

                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else{
                        pickImage(v);
                    }

                }
                else{
                    if(!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else{
                        pickImage(v);
                    }
                }
            }
        });


    }

    private void pickImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Log.d("Tag in cam uri", imageUri.toString());
                Log.d("Tag in cam", imageStream.toString());
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImageBig.setImageBitmap(selectedImage);

                StorageReference childRef = storageReference.child(UUID.randomUUID().toString());

                //uploading the image
                UploadTask uploadTask = childRef.putFile(imageUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(profileViewDesign.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profileViewDesign.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean perm2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return perm2;
    }

    private boolean checkCameraPermission() {

        boolean perm1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean perm2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return perm1 && perm2;
    }
}