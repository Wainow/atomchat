package com.example.atomchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    ArrayList<Post> posts;
    LayoutInflater inflater;

    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = posts.get(position);
        holder.username_post.setText(userColor(post.getAuthor()));
        holder.username_post.setTextColor(Color.parseColor(userColor(post.getAuthor())));
        holder.text_post.setText(post.getText() + "...");
        holder.data_post.setText(post.getDate());
        if(post.getImageURL().equals("none")){
            holder.post_image.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(userColor(post.getAuthor())), Color.parseColor(userColor(post.getAuthor()))));
        } else{
            Glide.with(inflater.getContext()).load(post.getImageURL()).into(holder.post_image);
            holder.post_image.setClipToOutline(true);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), PostShow.class);
                intent.putExtra("getImageURL", post.getImageURL());
                intent.putExtra("getDate", post.getDate());
                intent.putExtra("getText", post.getText());
                intent.putExtra("getAuthor", userColor(post.getAuthor()));
                intent.putExtra("getKey", post.getKey());
                inflater.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username_post;
        public TextView data_post;
        public TextView text_post;
        public ImageView post_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username_post = itemView.findViewById(R.id.username_post);
            data_post = itemView.findViewById(R.id.data_post);
            text_post = itemView.findViewById(R.id.text_post);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }

    public String userColor(String id) {

        String color = "";
        String norm = "1234567890ABCDEFabcdef";
        int n = 0;
        for (int i = 0; i < id.length() && n < 6; i++) {
            for (int j = 0; j < 22; j++) {
                if (id.charAt(i) == norm.charAt(j)) {
                    if (j < 16) color = color + norm.charAt(j);
                    else color = color + norm.charAt(j - 6);
                    n++;
                }
            }
        }
        while (n++ < 6) color = color + '0';
        color = "#" + color;
        return color;
    }
}
