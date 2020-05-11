package com.example.atomchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atomchat.Notifications.Client;
import com.example.atomchat.Notifications.Data;
import com.example.atomchat.Notifications.MyResponse;
import com.example.atomchat.Notifications.Sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Callback;

public class PostShow extends AppCompatActivity {

    private Intent intent;
    private String ImageURL;
    private String Date;
    private String Text;
    private String Author;
    private String Key;

    private TextView text_post_show;
    private TextView username_post_show;
    private TextView data_post_show;
    private ImageView image_post_show;

    private ImageButton imageButtonMessage;
    private EditText editTextMessage;
    private RecyclerView list_of_answers;
    private ArrayList<ChatPost> array_answers = new ArrayList<>();
    private PostShowAdapter postShowAdapter;
    private static int MAX_MESSAGE_LENGTH = 151;
    private String userID;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference MyRef_tokens;
    private String token;
    private String AuthorID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        readIntent();
        setView();
        readAnswers();
    }

    private void readIntent(){
        intent = getIntent();
        ImageURL = intent.getStringExtra("getImageURL");
        Date = intent.getStringExtra("getDate");
        Text = intent.getStringExtra("getText");
        Author = intent.getStringExtra("getAuthor");
        AuthorID = intent.getStringExtra("getAuthorID");
        Key = intent.getStringExtra("getKey");
    }

    private void setView(){
        //text_post_show = findViewById(R.id.text_post_show);
        //data_post_show = findViewById(R.id.data_post_show);
        //username_post_show = findViewById(R.id.username_post_show);
        //image_post_show = findViewById(R.id.post_image_show);

        //text_post_show.setText(Text);
        //data_post_show.setText(Date);
        //username_post_show.setText(Author);
        //username_post_show.setTextColor(Color.parseColor(Author));
        //Glide.with(this).load(ImageURL).into(image_post_show);
        //image_post_show.setClipToOutline(true);

        imageButtonMessage = findViewById(R.id.send_message_btn);
        editTextMessage = findViewById(R.id.edit_text_message);
        list_of_answers = findViewById(R.id.list_of_answers);
        list_of_answers.setLayoutManager(new LinearLayoutManager(this));
        postShowAdapter = new PostShowAdapter(this, array_answers);
        list_of_answers.setAdapter(postShowAdapter);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("forum").child(Key).child("answers");
        MyRef_tokens = database.getReference("Tokens");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        String message = editTextMessage.getText().toString();
        if(message.equals("")){
            return;
        } else if(message.length() > MAX_MESSAGE_LENGTH){
            return;
        }
        sendMessage(userID, userDate(), message, Key);
        editTextMessage.setText("");
    }

    private void sendMessage(String author, String data, String text, String key){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("author", author);
        hashMap.put("data", data);
        hashMap.put("text", text);
        hashMap.put("key", key);

        myRef.push().setValue(hashMap);

        if(!author.equals(AuthorID)) {
            sendNotificationToUser(author, text);
        }
    }

    public String userDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        return dateString;
    }

    private void readAnswers(){
        ChatPost generalPost = new ChatPost(Author, Date, Text, ImageURL, Key);
        array_answers.add(generalPost);
        getToken(AuthorID);
        postShowAdapter.notifyDataSetChanged();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //помещаю изменения в базе в переменную типа string
                String author = dataSnapshot.child("author").getValue().toString();
                String data = dataSnapshot.child("data").getValue().toString();
                String text = dataSnapshot.child("text").getValue().toString();
                String key = dataSnapshot.child("key").getValue().toString();

                ChatPost chat = new ChatPost(author,data,text,"none", key);
                array_answers.add(chat);
                postShowAdapter.notifyDataSetChanged();
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

    private void sendNotificationToUser(String author, String message) {
        Data data = new Data(userID, R.mipmap.ic_launcher, userColor(author) + " : " + message, "New Answer on your Post", userID);
        //String token = "dAGBT_kTbfk:APA91bEfQwnzU-z6sJRJsl0XYBmfNk9QyFIVKnO2wTy5mIbSO0pvpEJRbv95A7TchSMroECzwPlAQGDmUigdIWIWSjGVN7ufYgCzg4sFjb1lEMxj_i90oJX9xM10V1jWu_TVvLbZ4lOq";
        //final String token = getToken(author);
        Sender sender = new Sender(data, token);

        //Toast.makeText(General.this, "Method working!", Toast.LENGTH_SHORT).show();

        APIService apiService =  Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(retrofit2.Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                if (response.code() == 200){
                    if (response.body().success != 1){
                        //Toast.makeText(General.this, "Failed!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(General.this, token, Toast.LENGTH_SHORT).show();
                    } else{
                        //Toast.makeText(General.this, response.toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(General.this, token, Toast.LENGTH_SHORT).show();
                    }
                } else{
                    //Toast.makeText(General.this, response.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(General.this, token, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<MyResponse> call, Throwable t) {

            }
        });
    }

    private String getToken(final String receiver){
        MyRef_tokens.child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                token = dataSnapshot.child("token").getValue().toString();
                //Toast.makeText(General.this, token, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Toast.makeText(General.this, "HI", Toast.LENGTH_SHORT).show();
        //Toast.makeText(General.this, token, Toast.LENGTH_SHORT).show();
        return token;
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
