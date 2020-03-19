package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mychat.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private static final String TAG = "UpdateActivity";
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private EditText mFullName, mAbout, mPhoneNumber;
    private ImageView mImageView;
    private RadioGroup mRadioGroup;
    private Button mUpdateButton;
    private ProgressBar mProgress;

    private Uri localImageUri = null;

    StorageReference stRef = null;
    DatabaseReference mProfileRef = null;
    FirebaseAuth mAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize Views
        init();

        // Set click listener for views
        mImageView.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);

        // set Value event listener
        mProfileRef.addValueEventListener(this);
    }

    private void init()
    {
        mFullName = findViewById(R.id.update_fullname);
        mAbout = findViewById(R.id.update_about);
        mPhoneNumber = findViewById(R.id.update_phone);
        mRadioGroup = findViewById(R.id.update_group);
        mUpdateButton = findViewById(R.id.update_button);
        mImageView = findViewById(R.id.update_profile_pic);
        mProgress = findViewById(R.id.update_progress);

        stRef = FirebaseStorage
                .getInstance()
                .getReference();

        mAuth = FirebaseAuth
                .getInstance();

        mProfileRef = FirebaseDatabase
                .getInstance()
                .getReference(mAuth.getCurrentUser().getUid())
                .child("Profile");
    }


    private void showPictureDialog()
    {
        AlertDialog.Builder picDialog = new AlertDialog.Builder(this);
        picDialog.setTitle("Select Action");
        String[] picDialogItems = {"Select From Galley", "Select From Camera"};

        picDialog.setItems(picDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case 0:
                        //choose photo from gallery
                        chooseImageFromGallery();
                        break;
                    case 1:
                        // choose image from camera
                        takeImageFromCamera();
                        break;
                }
            }
        });

        picDialog.show();


    }



    private void chooseImageFromGallery()
    {
        if (PermissionsUtility.checkReadWritePermission(this))
        {
            Log.d(TAG, "chooseImageFromGallery: READ & WRITE PERMISSION ACCEPTED IN CHECKED");
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        }
        else
        {
            Log.d(TAG, "chooseImageFromGallery: PERMISSION DENIED in R&W... START REQUEST PERMISSION");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);

        }
    }

    private void takeImageFromCamera()
    {
        if (PermissionsUtility.checkCameraPermission(this))
        {
            Log.d(TAG, "takeImageFromCamera: CAMERA PERMISSION ACCEPTED IN CHECKED");
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
        else
        {
            Log.d(TAG, "takeImageFromCamera: PERMISSION DENIED in CAMERA... START REQUEST PERMISSION ");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == GALLERY_REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {
                if (PermissionsUtility.checkReadWritePermission(this))
                {
                    Toast.makeText(this, "Gallery Permission Accepted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                    if (PermissionsUtility.useRunTimePermission())
                    {
                        Log.d(TAG, "onRequestPermissionsResult: NEED RUNTIMEPERMISSION FOR R&W");
                        // RunTime Permission
                        showMessageOkCancel("You need to allow storage Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionsUtility.requestCameraPermission(UpdateActivity.this, GALLERY_REQUEST_CODE);
                            }
                        });


                    }

                }
            }
            else
            {
                Log.d(TAG, "onRequestPermissionsResult: GRANT RESULT LENGTH IS NOT GREATER THAN ZER0 IN GALLERY");
                return;
            }
        }
        else if (requestCode == CAMERA_REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {
                if (PermissionsUtility.checkCameraPermission(this))
                {
                    Toast.makeText(this, "Camera Permission Accepted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                    if (PermissionsUtility.useRunTimePermission())
                    {
                        // RunTime Permission
                        Log.d(TAG, "onRequestPermissionsResult: NEED RUNTIMEPERMISSION FOR CAMERA");
                        showMessageOkCancel("You Need to allow camera Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                PermissionsUtility
                                        .requestCameraPermission(UpdateActivity.this, CAMERA_REQUEST_CODE);
                            }
                        });
                    }
                }
            }
            else
            {
                Log.d(TAG, "onRequestPermissionsResult: GRANT RESULT LENGTH IS NOT GREATER THAN ZER0 IN CAMERA");
                return;
            }
        }
        else
        {
            Log.d(TAG, "onRequestPermissionsResult: REQUEST CODE IS NOT EQUAL TO GALLERY OR CAMERA ");
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED)
            return;

        if (requestCode == GALLERY_REQUEST_CODE)
        {

            if (data != null)
            {
                Uri contentUri = data.getData();
                mImageView.setImageURI(contentUri);
                mImageView.setTag(ImageUtility.getPath(this, contentUri));
                Log.d(TAG, "onActivityResult: data.getData() : " + contentUri);
            }
        }
        else if (requestCode == CAMERA_REQUEST_CODE)
        {

            if (data != null)
            {
                if (data.getData() != null)
                {
                    Uri contentUri = data.getData();
                    mImageView.setImageURI(contentUri);
                    mImageView.setTag(ImageUtility.getPath(this, contentUri));
                    Log.d(TAG, "onActivityResult: data.getData() : " + contentUri);
                }
                else
                {
                    Bitmap imgBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    mImageView.setImageBitmap(imgBitmap);
                    mImageView.setTag(ImageUtility.getPath(this, ImageUtility.getImageUri(this, imgBitmap) ));
                    Log.d(TAG, "onActivityResult: data.getData().getExtra() : " + imgBitmap);
                }
            }


        }
        else
        {
            return;
        }


    }

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener OkListner)
    {
        new AlertDialog.Builder(UpdateActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", OkListner)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.update_profile_pic:
                showPictureDialog();
                break;
            case R.id.update_button:
                //handle
                mProgress.setVisibility(View.VISIBLE);
//                uploadDataToFirebase();
                check();
                break;
        }
    }

    private void check()
    {
        Log.d(TAG, "check: URI : " + mImageView.getTag());
        try {
            imageUpload();
        }catch (FileNotFoundException fe)
        {
            Log.e(TAG, "check: ", fe);
        }
    }

    private void imageUpload() throws FileNotFoundException
    {
        if (mAuth.getCurrentUser().getUid() == null)
            return;

        InputStream inputStream = new FileInputStream(mImageView.getTag().toString());
        final StorageReference imageRef = stRef.child(mAuth.getCurrentUser().getUid() + "/Profile/" + Calendar.getInstance().getTimeInMillis()+".jpg");

        imageRef.putStream(inputStream)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(UpdateActivity.this, "Upload Success : ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: -------------------->");
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        localImageUri = uri;
                                        Log.d(TAG, "onSuccess: Uri in localImageUri : " + uri);
                                        uploadData();
                                    }
                                });

                }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Image did not upload");

            }
        });
    }

    private void uploadData()
    {
        String name, about, phone, gender, email, profileUri;
        name = mFullName.getText().toString().trim();
        about = mAbout.getText().toString().trim();
        phone = mPhoneNumber.getText().toString().trim();
        gender = getGender();
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        profileUri = localImageUri.toString();


        UserModel userModel = new UserModel(name, about, phone, gender, email, profileUri);

        mProfileRef.setValue(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Successfully Upload data :");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Did not upload data successfully : ", e);
                    }
                });

    }


    private String getGender()
    {
        int selectedid = mRadioGroup.getCheckedRadioButtonId();

        if (selectedid == R.id.update_male)
            return "Male";
        else
            return "Female";
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "onDataChange: dataSnapshot : " + dataSnapshot.getValue());
        updateUI(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {
        Log.d(TAG, "onCancelled: Failed to read database : " + databaseError);
    }

    private void updateUI(DataSnapshot dataSnapshot)
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if (NetworkUtility.getConnectionType(this) != 0 && NetworkUtility.isInternetAvailable())
        {
            if (dataSnapshot != null)
            {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

//                mImageView.setImageURI(Uri.parse(dataSnapshot.child("Profile").child("imageUri").getValue().toString()));
//                mFullName.setText(dataSnapshot.child("Profile").child("name").getValue().toString());

                Log.d(TAG, "updateUI: TESTING ---------> " + userModel.phone);
            }
        }
        else
        {
            if (user.getPhotoUrl() != null)
                mImageView.setImageURI(user.getPhotoUrl());
            if (user.getDisplayName() != null)
                mFullName.setText(user.getDisplayName());

        }

    }
}
