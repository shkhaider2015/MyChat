package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SiginActivity extends AppCompatActivity {

    private static final String TAG = "SiginActivity";
    private EditText mEmail, mPassword;
    private Button mSigninButton;
    private TextView mNotHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);
    }

    private void init()
    {
        mEmail = findViewById(R.id.signin_username);
        mPassword = findViewById(R.id.signin_password);
        mSigninButton = findViewById(R.id.signin_button);
        mNotHaveAccount = findViewById(R.id.signin_Dont_have_account);
    }
}
