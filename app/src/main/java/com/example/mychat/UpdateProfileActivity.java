package com.example.mychat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final String TAG = "UpdateProfileActivity";
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;


    private EditText mFullName, mEmail, mPassword, mPhoneNumber;
    private ImageView mImageView;
    private RadioGroup mRadioGroup;
    private Button mUpdateButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;

    private Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        init();
        loadInformationFromAuth();
    }

    private void init()
    {
        mFullName = findViewById(R.id.update_fullname);
        mEmail = findViewById(R.id.update_email);
        mPassword = findViewById(R.id.update_password);
        mPhoneNumber = findViewById(R.id.update_phone);
        mRadioGroup = findViewById(R.id.sign_up_radio_group);
        mUpdateButton = findViewById(R.id.update_button);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getUid() != null)
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(mAuth.getUid());
        else
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    private void  loadInformationFromAuth()
    {

        FirebaseUser cu = mAuth.getCurrentUser();

        try
        {
            if (cu != null)
            {
                mFullName.setText(cu.getDisplayName());
                mEmail.setText(cu.getEmail());
                mPhoneNumber.setText(cu.getPhoneNumber());
            }

        }catch (NullPointerException e)
        {
            Log.e(TAG, "loadInformation: ", e);
        }

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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void takeImageFromCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED)
            return;

        if (requestCode == GALLERY_REQUEST_CODE)
        {

            if (data != null)
            {
                Uri contentUri = data.getData();
                try
                {
                    Bitmap thumbnail = ImageUtility.getResizedBitmap(ImageUtility.getImage(ImageUtility.getBytes(getContentResolver().openInputStream(contentUri))), 200);
                    imageBitmap = thumbnail;
                    // imageView.setImageBitmap(thumbnail);

                }catch (FileNotFoundException e)
                {
                    Log.e(TAG, "onActivityResult: ", e);
                }catch (IOException e)
                {
                    Log.e(TAG, "onActivityResult: ", e);
                }
            }
        }
        else if (requestCode == CAMERA_REQUEST_CODE)
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageBitmap = ImageUtility.getResizedBitmap(thumbnail ,200);
            //ImageView.setImageBitmap(thumbnail);
        }
        else
        {
            return;
        }


    }
}
