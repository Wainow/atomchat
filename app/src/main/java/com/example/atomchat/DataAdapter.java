package com.example.atomchat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    private FirebaseUser user;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    ArrayList<Chat> messages;
    LayoutInflater inflater;

    public DataAdapter(Context context, ArrayList<Chat> messages) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT){
            View view = inflater.inflate(R.layout.item_message_real, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = messages.get(position); //LaWhoyBh36hpFkhHqEd1pWRvoie2
        String userColor = userColor(chat.getSender());
        //Toast.makeText(inflater.getContext(), userColor, Toast.LENGTH_LONG).show();
        holder.message.setText(chat.getMessage());
        holder.background.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(userColor), Color.parseColor(userColor)));
        holder.time.setText(chat.getDate());
        if(chat.getIsseen().equals("false")){
            //holder.relative_seen.setBackgroundColor(Color.parseColor("#161616"));
            holder.eye_seen.setVisibility(View.GONE);
            holder.eye_not_seen.setVisibility(View.VISIBLE);
        } else if(chat.getIsseen().equals("true")){
            holder.eye_seen.setVisibility(View.VISIBLE);
            holder.eye_not_seen.setVisibility(View.GONE);
        } else{
            holder.eye_seen.setVisibility(View.GONE);
            holder.eye_not_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(messages.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return  MSG_TYPE_LEFT;
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

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
    String userColor(String id, ViewHolder holder) {
        String color ="";
        //String id = "#2903030000000000000000000000";
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
        String r = "";
        String g = "";
        String b = "";
        r = r + color.charAt(0) + color.charAt(1);
        g = g + color.charAt(2) + color.charAt(3);
        b = b + color.charAt(4) + color.charAt(5);
        int red = hex2decimal(r);
        int green = hex2decimal(g);
        int blue = hex2decimal(b);
        if (1 - (0.299 * red + 0.587 * green + 0.114 * blue) / 255 < 0.5) {
            // светлый;
        }
        else{
            // темный
            holder.message.setTextColor(Color.parseColor("#FFFFFF"));
        }
        return color;
    }
}
