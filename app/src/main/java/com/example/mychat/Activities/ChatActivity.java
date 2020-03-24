package com.example.mychat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.mychat.Models.MessageModel;
import com.example.mychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements ValueEventListener, View.OnClickListener {
    private static final String TAG = "ChatActivity";

    RecyclerView mRecyclerView;
    LinearLayout mLinearLayout;
    EditText mEditText;
    Button mSendButton;
    String mUser_sender_id, muser_receiver_id;
    private List<MessageModel> messageModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        muser_receiver_id = getIntent().getStringExtra("receiver_id");

    }

    private void init()
    {
        mRecyclerView = findViewById(R.id.chat_reyclerview_message_list);
        mLinearLayout = findViewById(R.id.chat_layout_chatbox);
        mEditText = findViewById(R.id.chat_edittext_chatbox);
        mSendButton = findViewById(R.id.chat_button_chatbox_send);

        mUser_sender_id = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getUid();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        messageModelList = new ArrayList<>();

        FirebaseDatabase
                .getInstance()
                .getReference("Chat")
                .child(mUser_sender_id + muser_receiver_id)
                .child(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                .addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "onDataChange: DATASNAPSHOT : " + dataSnapshot.getChildren().toString());
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.chat_button_chatbox_send:
                sendMessage();
                break;
        }
    }

    private void sendMessage()
    {
        String senderId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String message_data = mEditText.getText().toString().trim();
        String date_time = Calendar.getInstance().getTime().toString();
        String image_uri = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).toString();

        MessageModel messageModel = new MessageModel(senderId, name, message_data, date_time, image_uri);

        DatabaseReference mRef = FirebaseDatabase
                .getInstance()
                .getReference("Chat")
                .child(mUser_sender_id + muser_receiver_id)
                .child(String.valueOf(Calendar.getInstance().getTimeInMillis()));

        mRef.setValue(messageModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: Message Sent Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Message Sent Failure");
                    }
                });
    }
}
