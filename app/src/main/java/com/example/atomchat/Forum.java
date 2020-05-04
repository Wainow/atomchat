package com.example.atomchat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Forum extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("forum");
    private ArrayList<Post> list_posts = new ArrayList<>();
    private RecyclerView list_of_posts;
    private PostAdapter postAdapter;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String imageURL = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        list_of_posts = findViewById(R.id.list_of_posts);
        list_of_posts.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, list_posts);
        list_of_posts.setAdapter(postAdapter);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        readPosts();
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

    public void readPosts(){
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String author = dataSnapshot.child("author").getValue().toString();
                String data = dataSnapshot.child("data").getValue().toString();
                String text = dataSnapshot.child("text").getValue().toString();
                String imageURL = dataSnapshot.child("imageURL").getValue().toString();
                String key = dataSnapshot.child("key").getValue().toString();

                Post post = new Post(author, data, text, imageURL, key);
                list_posts.add(post);
                //говорю адаптеру что нужно обновиться
                postAdapter.notifyDataSetChanged();
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

    public void post_onClick(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.post_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.edit_text_post);
        final ImageButton addImage = (ImageButton) promptsView.findViewById(R.id.add_image_button);
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Вводим текст и отображаем в строке ввода на основном экране:
                                //final_text.setText(userInput.getText());
                                sendPost(FirebaseAuth.getInstance().getUid(), userDate(), userInput.getText().toString(), imageURL);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        //Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();
    }

    public void add_onClick(View view) {
        openImage();
    }

    private void sendPost(String author, String data, String text, String imageURL){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("author", author);
        hashMap.put("text", text);
        hashMap.put("data", data);
        hashMap.put("imageURL", imageURL);
        String key = myRef.push().getKey();
        hashMap.put("key", key);
        myRef.child(key).setValue(hashMap);
        this.imageURL = "none";
    }

    public String userDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        return dateString;
    }

    public String userDayDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(new Date()).toString();
        return dateString;
    }

    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        //String mUri = downloadUri.toString();
                        imageURL = downloadUri.toString();

                        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("forum").child(userDayDate()).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        //HashMap<String, Object> map = new HashMap<>();
                        //map.put("imageURL", mUri);
                        //myRef.updateChildren(map);

                        pd.dismiss();
                    } else{
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else{
                uploadImage();
            }
        }
    }
}
