package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final String TAG = "UpdateProfileActivity";
    private EditText mFullName, mEmail, mPassword, mPhoneNumber;
    private ImageView mImageView;
    private RadioGroup mRadioGroup;
    private Button mUpdateButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
