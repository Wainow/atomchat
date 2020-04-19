package com.example.atomchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.media.Image;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class General extends AppCompatActivity {
    //инициализирую базу данных с той которая привязанна к приложению
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //создаю переменную для работы с базой данных и говорю ей что все изменения будут происходить во вкладке 'users'
    DatabaseReference myRef = database.getReference("users");

    private FirebaseAuth mAuth;
    private String userID;
    private EditText editTextMessage;
    private EditText TextMessage;
    private ImageButton imageButtonMessage;
    private static int MAX_MESSAGE_LENGTH = 151;
    private ArrayList<Chat> array_messages = new ArrayList<>();
    private RecyclerView list_of_messages;
    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        setTitle("#Swjat");
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

        //слушатель изменений в базе данных
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //помещаю изменения в базе в переменную типа string
                String m = dataSnapshot.child("message").getValue().toString();
                String sender = dataSnapshot.child("sender").getValue().toString();
                String receiver = dataSnapshot.child("sender").getValue().toString();
                //добавляю в массив сообщений новое значение
                //array_messages.add(m);

                Chat chat = new Chat(sender,receiver,m);
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
                sendMessage(userID, "Swjat", message);
                //обнулить написанный текст после отправки
                editTextMessage.setText("");
                //слушатель вкладок (если в базе есть новые сообщения - он сработает)
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        myRef.push().setValue(hashMap);
    }

}
