package com.example.mychat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.mychat.Models.PostModel;
import com.example.mychat.Models.UserModel;
import com.example.mychat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayProfileActivity extends AppCompatActivity implements ValueEventListener {
    private static final String TAG = "DisplayProfileActivity";

    private CircleImageView mImage;
    private Button mRequest_button, mChat_button;
    private TextView mName, mEmail, mPhone_Number, mGender;

    private PostModel postModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);
        init();
        postModel = (PostModel) getIntent().getSerializableExtra("myclass");
        loadInfo();
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

    private void loadInfo()
    {
        Picasso
                .get()
                .load(postModel.getUserImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(mImage);

        FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(postModel.getId())
                .child("Profile")
                .addValueEventListener(this);
    }
    private void updateUI(UserModel model)
    {
        Picasso
                .get()
                .load(model.getImageUri())
                .placeholder(R.mipmap.ic_launcher)
                .into(mImage);

        mName.setText(model.getName());
        mEmail.setText(model.getEmail());
        mPhone_Number.setText(model.getPhone());
        mGender.setText(model.getGender());
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        UserModel userModel = dataSnapshot.getValue(UserModel.class);
        updateUI(userModel);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {

    }
}
