package com.example.atomchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
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
import java.util.HashMap;

public class Chatting extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("chatting");
    DatabaseReference myRef_list = database.getReference("users_list");
    DatabaseReference myRef_list_user = database.getReference("users_list").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private FirebaseAuth mAuth;
    private ArrayList<User> array_users = new ArrayList<>();
    private ArrayList<String> list_users = new ArrayList<>();
    private RecyclerView list_of_users;
    private UserAdapter userAdapter;
    private static final String TAG = "myLogs";
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("#Chatting");

        /** Trying to set Navigation Drawer Activity in this context*/
        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //mAppBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.nav_home, R.id.nav_forum, R.id.nav_chatting)
        //        .setDrawerLayout(drawer)
        //        .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /** -- Making purple color in toolbar -- */
        //toolbar.setTitleTextColor(Color.parseColor("#ce93d8"));
        //final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        //upArrow.setColorFilter(Color.parseColor("#ce93d8"), PorterDuff.Mode.SRC_ATOP);
        //getSupportActionBar().setHomeAsUpIndicator(upArrow);

        list_of_users = findViewById(R.id.list_of_users);
        list_of_users.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, array_users, true);

        readUsers();
        readChats();
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //помещаю изменения в базе в переменную типа string
                String sender = dataSnapshot.child("sender").getValue().toString();
                String receiver = dataSnapshot.child("receiver").getValue().toString();
                if(sender.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    //User user = new User(receiver);
                    list_users.add(receiver);
                }
                if(receiver.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    //User user = new User(sender);
                    list_users.add(sender);
                }

                list_of_users.setAdapter(userAdapter);
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    private void readChats() {
        Log.d(TAG, "Метод включен...");
        myRef_list.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //array_users.clear();
                String id1 = dataSnapshot.child("id").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                User user = new User(id1, status);
                for(String id : list_users){
                    if(user.getId().equals(id)){
                        if(array_users.size() != 0){
                            for(User userl : array_users){
                                if(!user.getId().equals(userl.getId())){
                                    array_users.add(user);
                                }
                            }
                        } else{
                            array_users.add(user);
                        }
                    }
                }
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

    public void plus_onClick(View view) {
        Intent intent = new Intent(Chatting.this, Search.class);
        //запускаю след окно
        startActivity(intent);
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
}
