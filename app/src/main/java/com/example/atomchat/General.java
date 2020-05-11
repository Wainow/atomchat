package com.example.atomchat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import bolts.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class General extends AppCompatActivity {
    //инициализирую базу данных с той которая привязанна к приложению
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //создаю переменную для работы с базой данных и говорю ей что все изменения будут происходить во вкладке 'users'
    DatabaseReference myRef = database.getReference("chatting");
    DatabaseReference myRef_list = database.getReference("users_list");
    DatabaseReference myRef_list_user = database.getReference("users_list").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    DatabaseReference MyRef_tokens = database.getReference("Tokens");

    private FirebaseAuth mAuth;
    private String userID;
    private EditText editTextMessage;
    private EditText TextMessage;
    private ImageButton imageButtonMessage;
    private ImageView profile_image;
    private TextView username;
    private static int MAX_MESSAGE_LENGTH = 151;
    private ArrayList<Chat> array_messages = new ArrayList<>();
    private RecyclerView list_of_messages;
    private DataAdapter dataAdapter;
    private TextView status_text;
    private ChildEventListener seenListener;
    //public APIService apiService;
    public boolean notify;

    Intent intent;
    private String userID_receiver;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        //как и раньше: связываю существующие на окне окна для ввода текста с переменными
        editTextMessage = findViewById(R.id.edit_text_message);
        list_of_messages = findViewById(R.id.list_of_messages);
        imageButtonMessage = findViewById(R.id.send_message_btn);

        profile_image = findViewById(R.id.profile_image_general);
        username = findViewById(R.id.username);
        status_text = findViewById(R.id.status);
        intent = getIntent();
        userID_receiver = intent.getStringExtra("userid");
        final String user_status = intent.getStringExtra("userstatus");
        username.setText(userColor(userID_receiver));
        status_text.setText(user_status);
        profile_image.setColorFilter(Color.parseColor(userColor(userID_receiver)));

        list_of_messages.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(this, array_messages);
        list_of_messages.setAdapter(dataAdapter);

        //получаю данные о пользователе
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //получаю уникальные ключ данного пользователя (в данные момент это не используется)
        userID = user.getUid();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(General.this);
        linearLayoutManager.setStackFromEnd(true);
        list_of_messages.setLayoutManager(linearLayoutManager);

        //notifications
        //apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        //notify = false;

        seenMessage(userID, userID_receiver);
        //слушатель изменений в базе данных
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //помещаю изменения в базе в переменную типа string
                String m = dataSnapshot.child("message").getValue().toString();
                String sender = dataSnapshot.child("sender").getValue().toString();
                String receiver = dataSnapshot.child("receiver").getValue().toString();
                String d = dataSnapshot.child("date").getValue().toString();
                String isseen = dataSnapshot.child("isseen").getValue().toString();
                //добавляю в массив сообщений новое значение
                //array_messages.add(m);

                Chat chat = new Chat(sender,receiver,m, d, isseen);
                if(chat.getReceiver().equals(userID) && chat.getSender().equals(userID_receiver) || chat.getReceiver().equals(userID_receiver) && chat.getSender().equals(userID)) {
                    array_messages.add(chat);
                }
                //говорю адаптеру что нужно обновиться
                dataAdapter.notifyDataSetChanged();
                list_of_messages.smoothScrollToPosition(array_messages.size());
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

        //слушатель нажатия на кнопку отправления
        imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                //записываю написанное в тексте в переменнюу
                String message = editTextMessage.getText().toString();
                //если введенные текст пустой или больше разрешённого посылаю пользователя
                if(message.equals("")){
                    return;
                } else if(message.length() > MAX_MESSAGE_LENGTH){
                    return;
                }
                //говорю базе данных записать сообщение в - случайно сгенерированный в данном входе ключ - в этом ключе создать вкладку message - туда положить сообщение
                sendMessage(userID, userID_receiver, message, userDate());
                //обнулить написанный текст после отправки
                editTextMessage.setText("");
                //слушатель вкладок (если в базе есть новые сообщения - он сработает)
            }
        });

        //getRegToken();
    }

    private void sendMessage(String sender, String receiver, String message, String date){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("date", date);
        hashMap.put("isseen", "false");

        myRef.push().setValue(hashMap);

        getToken(receiver);
        sendNotificationToUser(sender, message, receiver);
        /*
        final String msg = message;
        final String rcvr = receiver;
        final String sndr = sender;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users-list").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify){
                    sendNotification(rcvr, sndr, msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */
    }

    public String userDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        return dateString;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            //startActivity(new Intent(General.this, Chatting.class));
            finish();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
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

    private void status(String status){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        myRef_list_user.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myRef.removeEventListener(seenListener);
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    private void seenMessage(final String userID, final String userID_receiver){
        seenListener = myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String m = dataSnapshot.child("message").getValue().toString();
                    String sender = dataSnapshot.child("sender").getValue().toString();
                    String receiver = dataSnapshot.child("receiver").getValue().toString();
                    String d = dataSnapshot.child("date").getValue().toString();
                    String isseen = dataSnapshot.child("isseen").getValue().toString();

                    Chat chat = new Chat(sender,receiver,m, d, isseen);

                    if(chat.getReceiver().equals(userID) && chat.getSender().equals(userID_receiver)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", "true");
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                    dataAdapter.notifyDataSetChanged();
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

    private void sendNotificationToUser(String author, String message, String receiver) {
        Data data = new Data(userID, R.mipmap.ic_launcher, userColor(author) + " : " + message, "New Message", userID_receiver);
        //String token = "dAGBT_kTbfk:APA91bEfQwnzU-z6sJRJsl0XYBmfNk9QyFIVKnO2wTy5mIbSO0pvpEJRbv95A7TchSMroECzwPlAQGDmUigdIWIWSjGVN7ufYgCzg4sFjb1lEMxj_i90oJX9xM10V1jWu_TVvLbZ4lOq";
        //final String token = getToken(author);
        Sender sender = new Sender(data, token);

        Toast.makeText(General.this, "Method working!", Toast.LENGTH_SHORT).show();

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
    /*
    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String token = snapshot.getValue().toString();
                    Data data = new Data(userID, R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userID_receiver);

                    Sender sender = new Sender(data, token);

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(General.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getRegToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    private static final String TAG = "hi";

                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(General.this, msg, Toast.LENGTH_SHORT).show();

                        //sendMessage(FirebaseAuth.getInstance().getUid(), "LaWhoyBh36hpFkhHqEd1pWRvoie2", msg, userDate());
                    }
                });
    }

     */
}
