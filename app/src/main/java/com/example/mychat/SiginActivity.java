package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SiginActivity extends AppCompatActivity  {

    private static final String TAG = "SiginActivity";
    private EditText mEmail, mPassword;
    private Button mSigninButton;
    private TextView mNotHaveAccount;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);
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

        mAuth = FirebaseAuth.getInstance();
    }

    private void signin()
    {
        String email = mEmail.getText().toString().trim();
        String pass = mPassword.getText().toString().trim();

        if (email.isEmpty())
        {
            mEmail.setError("Email required");
            mEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Email is not correct");
            mEmail.requestFocus();
            return;
        }
        if (pass.isEmpty())
        {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return;
        }
        if (pass.length() < 6)
        {
            mPassword.setError("Password is not correct");
            mPassword.requestFocus();
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
                        }
                        else
                        {
                            Log.w(TAG, "onComplete: Login Failure : ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "onFailure: Failure : ", e);
                    }
                });


    }

}
