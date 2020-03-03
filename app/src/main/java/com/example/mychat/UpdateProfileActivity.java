package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UpdateProfileActivity";
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;


    private EditText mFullName, mAbout, mPhoneNumber;
    private ImageView mImageView;
    private RadioGroup mRadioGroup;
    private Button mUpdateButton;
    private ProgressBar mProgress;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;
    private StorageReference mStorageReff;

    private Bitmap imageBitmap = null;
    private Uri onlineImageUri = null;
    private User oldUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        init();
        loadInformationFromAuth();

        mImageView.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                mRadioGroup.check(checkedId);
            }
        });
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

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser().getUid() != null)
        {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid());
            mStorageReff = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()+ "/Profile/");
        }
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
                Log.d(TAG, "loadInformationFromAuth: " + cu.getDisplayName() + cu.getEmail() + cu.getPhoneNumber());
                mFullName.setText(cu.getDisplayName());
                mPhoneNumber.setText(cu.getPhoneNumber());
            }

        }catch (NullPointerException e)
        {
            Log.e(TAG, "loadInformation: ", e);
        }

        oldUser = new User(mFullName.getText().toString().trim(),mAbout.getText().toString(), mAuth.getCurrentUser().getEmail(), mPhoneNumber.getText().toString().trim(), getGender());

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
                try
                {
                    Bitmap thumbnail = ImageUtility.getResizedBitmap(ImageUtility.getImage(ImageUtility.getBytes(getContentResolver().openInputStream(contentUri))), 200);
                    imageBitmap = thumbnail;
                    mImageView.setImageBitmap(thumbnail);

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
            mImageView.setImageBitmap(thumbnail);

        }
        else
        {
            return;
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
                                PermissionsUtility.requestCameraPermission(UpdateProfileActivity.this, GALLERY_REQUEST_CODE);
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
                                            .requestCameraPermission(UpdateProfileActivity.this, CAMERA_REQUEST_CODE);
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

    private void uploadDataToFirebase()
    {
        String name = mFullName.getText().toString().trim();
        String about = mAbout.getText().toString().trim();
        String phone = mPhoneNumber.getText().toString().trim();
        int gender = 1;
        if (mRadioGroup.getCheckedRadioButtonId() == R.id.update_female)
            gender = 2;

        User user = new User(name,about, mAuth.getCurrentUser().getEmail(), phone, gender);

        uploadImageToStorage(ImageUtility.getImageBytes(imageBitmap), user);


    }

    private void uploadImageToStorage(final byte[] bytes, final User user)
    {
        class UITS extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... voids) {

                StorageReference mref = FirebaseStorage
                        .getInstance()
                        .getReference(mAuth.getCurrentUser().getUid() + "/Profile");
                mref.putBytes(bytes)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                Log.d(TAG, "onSuccess: Successful upload an image");
                                onlineImageUri = Uri.parse(taskSnapshot
                                        .getMetadata()
                                        .getReference()
                                        .getDownloadUrl()
                                        .toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(UpdateProfileActivity.this, "IMAGE DID NOT UPLOAD", Toast.LENGTH_SHORT).show();
                            }
                        });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                uploadDataToDatabase(user);
            }
        }

        UITS uits = new UITS();
        uits.execute();

    }
    private void uploadDataToDatabase(final User user)
    {
        class UploadData extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... voids) {

                FirebaseDatabase
                        .getInstance()
                        .getReference("Users")
                        .child(mAuth.getCurrentUser().getUid())
                        .child("Profile")
                        .setValue(user);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backgroundTaskFinish(user);
                    }
                }, 3000);
            }
        }

        UploadData uploadData = new UploadData();
        uploadData.execute();

    }

    private void backgroundTaskFinish(User user)
    {
        Log.d(TAG, "backgroundTaskFinish: ");

        if (user.name != null && onlineImageUri != null)
        {
            UserProfileChangeRequest changeRequest = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(onlineImageUri)
                    .setDisplayName(user.name)
                    .build();
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            firebaseUser.updateProfile(changeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: Successfully Update profile");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failure updating profile");
                        }
                    });
        }

        mProgress.setVisibility(View.GONE);
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
                uploadDataToFirebase();
                break;
        }

    }

    private void showMessageOkCancel(String message, DialogInterface.OnClickListener OkListner)
    {
        new AlertDialog.Builder(UpdateProfileActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", OkListner)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    private int getGender()
    {
        int selected = mRadioGroup.getCheckedRadioButtonId();
        if (selected == R.id.update_female)
            return 2;
        return 1;
    }

    private int check()
    {
//sss//ssssssadsadsadsadasdsadas

        return 3;
    }
}
