package com.example.atomchat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users_list");

    private ArrayList<User> array_users = new ArrayList<>();
    private RecyclerView search_of_users;
    private UserAdapter userAdapter;
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        search_of_users = findViewById(R.id.search_of_users);
        search_of_users.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, array_users, false);
        search_of_users.setAdapter(userAdapter);

        readUsers();
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //помещаю изменения в базе в переменную типа string
                String id = dataSnapshot.child("id").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                User user = new User(id, status);
                if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getId())) {
                    array_users.add(user);
                }
                //говорю адаптеру что нужно обновиться
                userAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            //startActivity(new Intent(Forum.this, NewGeneral.class));
            finish();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

}
