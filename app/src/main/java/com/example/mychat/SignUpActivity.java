package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private EditText mFullname, mEmail, mPassword, mConfirmpassword;
    private RadioGroup mRadioGroup;
    private RadioButton mMale, mFemale;
    private Button mSignUp;
    private TextView mHaveAccount;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });

        mHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SiginActivity.class));
            }
        });
    }

    private void init()
    {
        mFullname = findViewById(R.id.sign_up_username);
        mEmail = findViewById(R.id.sign_up_email);
        mPassword = findViewById(R.id.sign_up_password);
        mConfirmpassword = findViewById(R.id.sign_up_confirm_password);
        mRadioGroup = findViewById(R.id.sign_up_radio_group);
        mSignUp = findViewById(R.id.sign_up_button);
        mHaveAccount = findViewById(R.id.sign_up_have_account);
    }

    private void checkInfo()
    {
        String name = mFullname.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String cPass = mConfirmpassword.getText().toString().trim();
        int genderID = mRadioGroup.getCheckedRadioButtonId();
        int gender = 0;

        if (name.isEmpty())
        {
            mFullname.setError("Name is required");
            mFullname.requestFocus();
            return;
        }
        if (name.length() > 20)
        {
            mFullname.setError("Name is too long");
            mFullname.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Email is not correct");
            mEmail.requestFocus();
            return;
        }
        if (!password.equals(cPass))
        {
            mConfirmpassword.setError("Passwords are not matching");
            mConfirmpassword.requestFocus();
            return;
        }
        if (password.length() < 6)
        {
            mPassword.setError("Password is too small");
            mPassword.requestFocus();
            return;
        }

        if (genderID == R.id.sign_up_male)
            gender = 1;
        else
            gender = 2;

        uploadInfo(name, email, password, gender);
    }

    private void uploadInfo(final String name, final String email, String pass, final int gender)
    {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            User user = new User(name, email, gender);
                            Log.d(TAG, "onComplete: Task : Successful");
                            mDatabase.getReference("Users")
                                    .child(mAuth.getUid().toString())
                                    .setValue(user);

                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            finish();

                        }
                        else
                        {
                            Log.w(TAG, "onComplete: Task : Failure ", task.getException());
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
