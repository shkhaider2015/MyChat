package com.example.mychat.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.Models.PostModel;
import com.example.mychat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements View.OnClickListener {

    private List<PostModel> data;
    private Context mCTX;

    public PostAdapter(Context mCTX, List<PostModel> data)
    {
        this.mCTX = mCTX;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        PostModel postModel = data.get(position);

        Picasso
                .get()
                .load(postModel.getUserImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);

        holder.mName.setText(postModel.getUserName());
        holder.mDate.setText(postModel.getDate());
        holder.mDetail.setText(postModel.getPostDetail());
        holder.mLike.setOnClickListener(this);
        holder.mComment.setOnClickListener(this);
        holder.mShare.setOnClickListener(this);
        holder.mLinearLayout.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView imageView;
        TextView mName, mDate, mDetail;
        Button mLike, mComment, mShare;
        LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.post_image);
            mName = itemView.findViewById(R.id.post_name);
            mDate = itemView.findViewById(R.id.post_date);
            mDetail = itemView.findViewById(R.id.post_detail);
            mLike = itemView.findViewById(R.id.post_like);
            mComment = itemView.findViewById(R.id.post_comment);
            mShare = itemView.findViewById(R.id.post_share);
            mLinearLayout = itemView.findViewById(R.id.post_linear_layout);
        }
    }



}
