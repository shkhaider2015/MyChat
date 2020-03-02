package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private EditText mFullname, mEmail, mPassword, mConfirmpassword;
    private RadioGroup mRadioGroup;
    private RadioButton mMale, mFemale;
    private Button mSignUp;
    private TextView mHaveAccount;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        mAuth = FirebaseAuth.getInstance();
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

    private void uploadInformation(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: Create user with email and password : success");
                        }
                        else
                        {
                            Log.w(TAG, "onComplete: Failure ", task.getException());
                        }
                    }
                });
    }
}
