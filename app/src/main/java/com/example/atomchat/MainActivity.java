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

    //создаю приватные переменные для "окна" в котором будет вводится логин и пароль
    private EditText log, pass;
    //создаю приватные переменные для анимаций которые включаются при входе в программу
    private Animation logoAnim, logoAnim_reg, editTextAnim, editTextAnim2;
    //создаю приватные переменные в которой хранятся данные текущего пользователя
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //создаю отдельный метод в котором инициализирую переменные для анимации: связываю настойки к анимации с переменной для анимации чтобы их можно было запустить
    private void unitStart(){
        logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        ImageButton atom = findViewById(R.id.atom); //(ImageButton)
        atom.startAnimation(logoAnim);

        //говорю о том чтобы окно где пишутся логин и пароль предлежат этим переменным
        log = findViewById(R.id.edit_text_login); // (EditText)
        pass = findViewById(R.id.edit_text_password); // (EditText)
        //инициализирую анимации
        editTextAnim = AnimationUtils.loadAnimation(this, R.anim.edit_text_anim);
        editTextAnim2 = AnimationUtils.loadAnimation(this, R.anim.edit_text_anim2);
        //запускаю анимации
        log.startAnimation(editTextAnim);
        pass.startAnimation(editTextAnim2);
    }

    //В этом методе тоже самое для анимации атома при нажатии на него
    private void unitReg(){
        //инициализирую анимации прокрутки атома
        logoAnim_reg = AnimationUtils.loadAnimation(this, R.anim.logo_anim_reg);
        //связываю кнопку-картинку с этой переменной
        ImageButton atom = findViewById(R.id.atom); // (ImageButton)
        //запускаю анимацию
        atom.startAnimation(logoAnim_reg);
    }

    //главный метод программы, аналог main, связываает окно программы с кодом
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //говорю что activity main принадлежит именно этому коду
        setContentView(R.layout.activity_main);
        // Титульное название сверху 'Log in'
        setTitle("Log in");

        //Запускаю анимации при старте программы
        unitStart();

        //получаю данные текущего пользователя
        mAuth = FirebaseAuth.getInstance();
        //не помню что это но лучше не трогать
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

    //слушатель нажатия на значок атома
    public void onClick(View view){
        //запуск анимации атома
        unitReg();
        //проверяю не пустые ли окна для ввода?
        if(pass.getText().toString().equals("")){
            Toast.makeText(this, "Логин не может быть пустым", Toast.LENGTH_SHORT).show();
        } else if(log.getText().toString().equals("")){
            Toast.makeText(this, "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
        } else {
            //если не пустые запускаю авторизацию
            if(view.getId() == R.id.atom){
                singing(log.getText().toString(), pass.getText().toString());
            }
        }
    }

    //в этом методе собственно проходит проверка на то существует ли данный пользователь в базе данных
    public void singing(String log, String pass){
        mAuth.signInWithEmailAndPassword(log, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Успешно", Toast.LENGTH_LONG).show();
                    // создаю переменную которая связывает текущее окно MainActivity со следующим - General
                    Intent intent = new Intent(MainActivity.this, General.class);
                    //запускаю след окно
                    startActivity(intent);
                } else{
                    Toast.makeText(MainActivity.this, "Пароль неверный", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
