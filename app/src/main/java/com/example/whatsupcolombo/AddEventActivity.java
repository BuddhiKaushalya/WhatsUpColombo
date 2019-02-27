package com.example.whatsupcolombo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class AddEventActivity extends AppCompatActivity {

    private EditText mTitleTv, mDescriptionTv;
    private ImageView mImamgeImg;
    private Button mSubmitBtn;

    //folder path to Firebase Storage
    String mStoragePath = "Image_Uploads/";
    //root path fore firebase database
    String mDatabasePAth = "Data";

    //creating URI
    Uri mFilepath;

    //creating storage and database reference
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    //progress dialog
    ProgressDialog mProgressDialog;

    //image Request code for choosing image
    int IMAGE_REQUEST_CODE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setupUi();


        //image click to load image
        mImamgeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create Intent
                Intent intent = new Intent();
                //setting intent type as image to select image from device
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST_CODE);

            }
        });

        //button clik to submit data to database
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call method to upload data to firebase
                uploadDataToFirebase();
            }
        });
        //assign Firebasestoreage instance to storage reference object
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //asssign firebasedatabase instance with root database name
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePAth);

        //progress daialog
        mProgressDialog = new ProgressDialog(AddEventActivity.this);
    }

    private void uploadDataToFirebase() {
        //check if filepath is empty or not
        if (mFilepath != null) {
            //setting progressbar title
            mProgressDialog.setTitle("uploading");
            //show progress dialog
            mProgressDialog.show();
            //create second storage reference
            StorageReference storageReference2 = mStorageReference.child(mStoragePath + System.currentTimeMillis() + "." + getFileExtention(mFilepath));

            //adding addOnSuccessListener to Storaqgereference
            storageReference2.putFile(mFilepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get Title
                    String mEventTitle = mTitleTv.getText().toString().trim();
                    //get description
                    String mDescrition = mDescriptionTv.getText().toString().trim();
                    //hide prgressbar
                    mProgressDialog.dismiss();
                    //show toast that image is uploaded
                    Toast.makeText(AddEventActivity.this, "uploaded successfully", Toast.LENGTH_LONG).show();
                    ;
                    ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mEventTitle, mDescrition, taskSnapshot.getStorage().getDownloadUrl().toString(), mEventTitle.toLowerCase());
                    Log.d("loggg",taskSnapshot.toString());
                    //getting image upload id
                    String imageUploadID = mDatabaseReference.push().getKey();
                    //add image upload id child elemnet to database reference
                    mDatabaseReference.child(imageUploadID).setValue(imageUploadInfo);

                }
                //if something went wrong like network failure
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //hide progress dialg
                    mProgressDialog.dismiss();
                    //show error toadt
                    Toast.makeText(AddEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.setTitle("Uploading");

                }
            });
        } else {

            Toast.makeText(AddEventActivity.this, "please select image", Toast.LENGTH_SHORT).show();
        }

    }

    //method to get the selected image file extension
    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void setupUi() {
        mTitleTv = (EditText) findViewById(R.id.pTitleEt);
        mDescriptionTv = (EditText) findViewById(R.id.pDescriptionEt);
        mImamgeImg = (ImageView) findViewById(R.id.pImageIV);
        mSubmitBtn = (Button) findViewById(R.id.pSubmitBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null ) {

            mFilepath = data.getData();

            try {
                //getting selected image into bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilepath);
                //setting bitmap to imageview
                mImamgeImg.setImageBitmap(bitmap);
                mImamgeImg.setImageURI(data.getData());


            } catch (Exception e) {
                Toast.makeText(AddEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddEventActivity.this, "select an image", Toast.LENGTH_SHORT).show();
            Log.d("ds", data.getData().toString());
        }
    }

}