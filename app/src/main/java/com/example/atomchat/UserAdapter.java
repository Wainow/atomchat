package com.example.atomchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<User> users;
    LayoutInflater inflater;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.username.setText(user.getUsername());
        //holder.username.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(user.getProfile_color()), Color.parseColor(user.getProfile_color())));
        holder.profile_image.setColorFilter(Color.parseColor(user.getProfile_color()));
        //holder.profile_image.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(user.getProfile_color()), Color.parseColor(user.getProfile_color())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), General.class);
                intent.putExtra("userid", user.getId());
                inflater.getContext().startActivity(intent);
            }
        });
        setLastMessage(user.getId(), holder.last_message);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public TextView last_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_message = itemView.findViewById(R.id.last_message);
        }
    }

    public void setLastMessage(final String userID_receiver, final TextView last_message){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("chatting");
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //помещаю изменения в базе в переменную типа string
                String m = dataSnapshot.child("message").getValue().toString();
                String sender = dataSnapshot.child("sender").getValue().toString();
                String receiver = dataSnapshot.child("receiver").getValue().toString();
                String d = dataSnapshot.child("date").getValue().toString();
                //добавляю в массив сообщений новое значение
                //array_messages.add(m);

                Chat chat = new Chat(sender,receiver,m, d);
                if(chat.getReceiver().equals(userID) && chat.getSender().equals(userID_receiver) || chat.getReceiver().equals(userID_receiver) && chat.getSender().equals(userID)) {
                    last_message.setText(m);
                    last_message.setTextColor(Color.parseColor(userColor(sender)));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

