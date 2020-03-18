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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UpdateActivity";
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private EditText mFullName, mAbout, mPhoneNumber;
    private ImageView mImageView;
    private RadioGroup mRadioGroup;
    private Button mUpdateButton;
    private ProgressBar mProgress;

    private Uri localImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize Views
        init();

        // Set click listener for views
        mImageView.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
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
                mImageView.setTag(contentUri);
                Log.d(TAG, "onActivityResult: data.getData() : " + contentUri);
//                try
//                {
//                    Bitmap thumbnail = ImageUtility.getResizedBitmap(ImageUtility.getImage(ImageUtility.getBytes(getContentResolver().openInputStream(contentUri))), 200);
////                    imageBitmap = thumbnail;
//                    mImageView.setImageBitmap(thumbnail);
//
//                }catch (FileNotFoundException e)
//                {
//                    Log.e(TAG, "onActivityResult: ", e);
//                }catch (IOException e)
//                {
//                    Log.e(TAG, "onActivityResult: ", e);
//                }
            }
        }
        else if (requestCode == CAMERA_REQUEST_CODE)
        {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            imageBitmap = ImageUtility.getResizedBitmap(thumbnail ,200);
            if (data != null)
            {
                if (data.getData() != null)
                {
                    Uri contentUri = data.getData();
                    mImageView.setImageURI(contentUri);
                    mImageView.setTag(ImageUtility.getRealPathFromURI(contentUri, UpdateActivity.this));
                    Log.d(TAG, "onActivityResult: data.getData() : " + contentUri);
                }
                else
                {
                    Bitmap imgBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    mImageView.setImageBitmap(imgBitmap);
                    mImageView.setTag(ImageUtility.getRealPathFromURI(ImageUtility.getImageUri(this, imgBitmap), UpdateActivity.this));
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
    }


}
