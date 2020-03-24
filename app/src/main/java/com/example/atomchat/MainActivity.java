package com.example.atomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText log, pass;

    private Animation logoAnim, logoAnim_reg, editTextAnim, editTextAnim2;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void unitStart(){
        logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        ImageButton atom = findViewById(R.id.atom); //(ImageButton)
        atom.startAnimation(logoAnim);

        log = findViewById(R.id.edit_text_login); // (EditText)
        pass = findViewById(R.id.edit_text_password); // (EditText)
        editTextAnim = AnimationUtils.loadAnimation(this, R.anim.edit_text_anim);
        editTextAnim2 = AnimationUtils.loadAnimation(this, R.anim.edit_text_anim2);
        log.startAnimation(editTextAnim);
        pass.startAnimation(editTextAnim2);
    }

    private void unitReg(){
        logoAnim_reg = AnimationUtils.loadAnimation(this, R.anim.logo_anim_reg);
        ImageButton atom = findViewById(R.id.atom); // (ImageButton)
        atom.startAnimation(logoAnim_reg);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Log in");

        unitStart();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //Toast.makeText(MainActivity.this, "YES", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(MainActivity.this, "NO", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public void onClick(View view){

        unitReg();

        if(pass.getText().toString().equals("")){
            Toast.makeText(this, "Логин не может быть пустым", Toast.LENGTH_SHORT).show();
        } else if(log.getText().toString().equals("")){
            Toast.makeText(this, "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
        } else {
            if(view.getId() == R.id.atom){
                singing(log.getText().toString(), pass.getText().toString());
            }
        }
    }

    public void singing(String log, String pass){
        mAuth.signInWithEmailAndPassword(log, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Успешно", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, General.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(MainActivity.this, "Пароль неверный", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
