package com.example.atomchat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atomchat.Notifications.Data;
import com.example.atomchat.Notifications.MyResponse;
import com.example.atomchat.Notifications.Sender;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGeneral extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    //инициализирую базу данных с той которая привязанна к приложению
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //создаю переменную для работы с базой данных и говорю ей что все изменения будут происходить во вкладке 'users'
    DatabaseReference myRef = database.getReference("users");
    DatabaseReference myRef_list_user = database.getReference("users_list").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private FirebaseAuth mAuth;
    private String userID;
    private EditText editTextMessage;
    private EditText TextMessage;
    private ImageButton imageButtonMessage;
    private static int MAX_MESSAGE_LENGTH = 151;
    private ArrayList<Chat> array_messages = new ArrayList<>();
    private RecyclerView list_of_messages;
    private DataAdapter dataAdapter;
    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Message channel";

    //header items
    private NavigationView navigationView;
    private View headView;
    private ImageView imageView;
    private TextView textView;
    private TextView username_textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_general);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHeader();
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

         */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_forum, R.id.nav_chatting)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        toolbar.setTitle("Swjat");
        //как и раньше: связываю существующие на окне окна для ввода текста с переменными
        editTextMessage = findViewById(R.id.edit_text_message);
        list_of_messages = findViewById(R.id.list_of_messages);
        imageButtonMessage = findViewById(R.id.send_message_btn);


        list_of_messages.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(this, array_messages);
        list_of_messages.setAdapter(dataAdapter);

        //получаю данные о пользователе
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //получаю уникальные ключ данного пользователя (в данные момент это не используется)
        userID = user.getUid();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NewGeneral.this);
        linearLayoutManager.setStackFromEnd(true);
        list_of_messages.setLayoutManager(linearLayoutManager);

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
                array_messages.add(chat);

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

                //записываю написанное в тексте в переменнюу
                String message = editTextMessage.getText().toString();
                //если введенные текст пустой или больше разрешённого посылаю пользователя
                if(message.equals("")){
                    return;
                } else if(message.length() > MAX_MESSAGE_LENGTH){
                    return;
                }
                //говорю базе данных записать сообщение в - случайно сгенерированный в данном входе ключ - в этом ключе создать вкладку message - туда положить сообщение
                sendMessage(userID, "Swjat", message, userDate());
                //обнулить написанный текст после отправки
                editTextMessage.setText("");
                //слушатель вкладок (если в базе есть новые сообщения - он сработает)
            }
        });

        //checkNewMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(NewGeneral.this, MainActivity.class));
                finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void sendMessage(String sender, String receiver, String message, String date){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("date", date);
        hashMap.put("isseen", "true");

        myRef.push().setValue(hashMap);
    }

    public String userDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        return dateString;
    }

    private void status(String status){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        myRef_list_user.updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    public void checkNewMessages(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(NewGeneral.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Сообщение")
                        .setContentText("соси мой жучий хуй")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(NewGeneral.this);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    public void setHeader() {
        navigationView = findViewById(R.id.nav_view);
        View headView = navigationView.getHeaderView(0);
        User user = new User();
        imageView = (ImageView) headView.findViewById(R.id.imageView);
        textView = headView.findViewById(R.id.textView);
        username_textView = headView.findViewById(R.id.username_textView);

        imageView.setColorFilter(Color.parseColor(user.userColor(FirebaseAuth.getInstance().getUid())));
        username_textView.setText(user.userColor(FirebaseAuth.getInstance().getUid()));
        //username_textView.setTextColor(Color.parseColor(user.userColor(FirebaseAuth.getInstance().getUid())));
        textView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        Menu menu = navigationView.getMenu();
        MenuItem fire = menu.findItem(R.id.nav_forum);
        Drawable newIcon = (Drawable)fire.getIcon();
        newIcon.mutate().setColorFilter(Color.argb(255, 200, 200, 200), PorterDuff.Mode.SRC_IN);
        fire.setIcon(newIcon);
        //MenuItem tools= menu.findItem(R.id.tools);
        //SpannableString s = new SpannableString(tools.getTitle());
        //s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        //tools.setTitle(s);
        //navigationView.setNavigationItemSelectedListener(this);
    }
}
