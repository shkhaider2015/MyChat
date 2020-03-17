package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SiginActivity extends AppCompatActivity  {

    private static final String TAG = "SiginActivity";
    private EditText mEmail, mPassword;
    private Button mSigninButton;
    private TextView mNotHaveAccount;
    private ProgressBar mProgressbar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();

        mSigninButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signin();
            }
        });

        mNotHaveAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SiginActivity.this, SignUpActivity.class));
            }
        });

    }

    private void init()
    {
        mEmail = findViewById(R.id.signin_username);
        mPassword = findViewById(R.id.signin_password);
        mSigninButton = findViewById(R.id.signin_button);
        mNotHaveAccount = findViewById(R.id.signin_Dont_have_account);
        mProgressbar = findViewById(R.id.signin_progressbar);

        mAuth = FirebaseAuth.getInstance();

        NetworkUtility.strictModeOn();
    }

    private void signin()
    {
        mProgressbar.setVisibility(View.VISIBLE);
        String email = mEmail.getText().toString().trim();
        String pass = mPassword.getText().toString().trim();

        if (email.isEmpty())
        {
            mEmail.setError("Email required");
            mEmail.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Email is not correct");
            mEmail.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if (pass.isEmpty())
        {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if (pass.length() < 6)
        {
            mPassword.setError("Password is not correct");
            mPassword.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if (NetworkUtility.getConnectionType(this) == 0)
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            mProgressbar.setVisibility(View.GONE);
            return;
        }

        if (!NetworkUtility.isInternetAvailable())
        {
            Toast.makeText(this, "Network is available but without internet", Toast.LENGTH_SHORT).show();
            mProgressbar.setVisibility(View.GONE);
            return;
        }


        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: Login successful");
                            startActivity(new Intent(SiginActivity.this, HomeActivity.class));
                            finish();
                        }
                        else
                        {
                            Log.w(TAG, "onComplete: Login Failure : ", task.getException());
                            mProgressbar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "onFailure: Failure : ", e);
                        mProgressbar.setVisibility(View.GONE);
                    }
                });


    }



}
