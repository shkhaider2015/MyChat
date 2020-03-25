package com.example.mychat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.Models.MessageModel;
import com.example.mychat.R;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVE = 2;

    private Context mCTX;
    private List<MessageModel> mMessageList;
    private FirebaseUser muser;

    public MessageAdapter(Context mCTX, List<MessageModel> mMessageList, FirebaseUser muser)
    {
        this.mCTX = mCTX;
        this.mMessageList = mMessageList;
        this.muser = muser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT)
        {
            Log.d(TAG, "onCreateViewHolder: ---------> Sender");
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVE)
        {
            Log.d(TAG, "onCreateViewHolder: ---------> Receiver");
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceiveMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        MessageModel messageModel = mMessageList.get(position);

        switch (holder.getItemViewType())
        {
            case VIEW_TYPE_MESSAGE_SENT:

                break;
            case VIEW_TYPE_MESSAGE_RECEIVE:
                ((ReceiveMessageHolder) holder).bind(messageModel);
                break;
        }

    }

    @Override
    public int getItemCount()
    {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        MessageModel messageModel = mMessageList.get(position);

        if (messageModel.getId() == muser.getUid())
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVE;

    }

    private class SentMessageHolder extends RecyclerView.ViewHolder
    {
        TextView mMessageText, mTimeText;

        public SentMessageHolder(@NonNull View itemView)
        {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.text_message_body);
            mTimeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(MessageModel messageModel)
        {
            mMessageText.setText(messageModel.getMessage_data());
            mTimeText.setText(messageModel.getDate());
        }
    }

    private class ReceiveMessageHolder extends RecyclerView.ViewHolder
    {
        TextView mMessageText, mTimeText, mNameText;
        CircleImageView mImage;

        public ReceiveMessageHolder(@NonNull View itemView)
        {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.text_message_body);
            mTimeText = itemView.findViewById(R.id.text_message_time);
            mNameText = itemView.findViewById(R.id.text_message_name);
            mImage = itemView.findViewById(R.id.image_message_profile);
        }
        void bind(MessageModel messageModel)
        {
            mMessageText.setText(messageModel.getMessage_data());
            mTimeText.setText(messageModel.getDate());
            mNameText.setText(messageModel.getName());

            Picasso
                    .get()
                    .load(messageModel.getImage_uri())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mImage);
        }
    }
}
