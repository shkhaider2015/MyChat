package com.example.mychat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.HomeActivity;
import com.example.mychat.Models.PostModel;
import com.example.mychat.Models.UserModel;
import com.example.mychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener{

    private static final String TAG = "PostActivity";

    private EditText mPostDetails;
    private TextView mCancel, mPost;

    FirebaseUser mUser = null;
    UserModel userModel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();
    }

    private void init()
    {
        mPostDetails = findViewById(R.id.activity_post_article);
        mCancel = findViewById(R.id.activity_post_cancel);
        mPost = findViewById(R.id.activity_post_post);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(mUser.getUid())
                .child("Profile")
                .addValueEventListener(this);

        Log.d(TAG, "init: Current date : " + currentDate());

        mCancel.setOnClickListener(this);
        mPost.setOnClickListener(this);
    }

    private void uploadToDatabase()
    {
        String postDetails = mPostDetails.getText().toString().trim();
        String currentDate = currentDate();

        if (postDetails.isEmpty())
        {
            mPostDetails.setError("Text required");
            mPostDetails.requestFocus();
            return;
        }
        if (postDetails.length() < 10)
        {
            mPostDetails.setError("Minimum 10 character required");
            mPostDetails.requestFocus();
            return;
        }
        if (userModel == null)
        {
            Log.d(TAG, "uploadToDatabase: usermodel is null");
            return;
        }

        PostModel postModel = new PostModel(userModel.getName(), userModel.getImageUri(), currentDate, postDetails, mUser.getUid());

        FirebaseDatabase
                .getInstance()
                .getReference("Posts")
                .child(currentDate)
                .child(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                .setValue(postModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: Successfully upload post");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: while uploading post getting error : " + e);

                    }
                });






    }

    private String currentDate()
    {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return dateFormat.format(date);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userModel = dataSnapshot.getValue(UserModel.class);
        Log.d(TAG, "onDataChange: success : " + userModel.getName());
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {
        userModel = null;
        Toast.makeText(PostActivity.this, "Something Wrong accuar", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.activity_post_post:
                uploadToDatabase();
                break;
            case R.id.activity_post_cancel:
                startActivity(new Intent(PostActivity.this, HomeActivity.class));
                finish();
                break;
        }
    }
}
