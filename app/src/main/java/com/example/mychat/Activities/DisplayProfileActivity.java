package com.example.mychat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.mychat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayProfileActivity extends AppCompatActivity {
    private static final String TAG = "DisplayProfileActivity";

    private CircleImageView mImage;
    private Button mRequest_button, mChat_button;
    private TextView mName, mEmail, mPhone_Number, mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);
        init();
    }

    private void init()
    {
        mImage = findViewById(R.id.display_image);
        mRequest_button = findViewById(R.id.display_request_button);
        mChat_button = findViewById(R.id.display_chat_button);
        mName = findViewById(R.id.display_name);
        mEmail = findViewById(R.id.display_email);
        mPhone_Number = findViewById(R.id.display_phone_number);
        mGender = findViewById(R.id.display_gender);

    }
}
