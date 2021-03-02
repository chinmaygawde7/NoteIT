package com.example.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// This code is of creating a new note

public class NewNotesActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText etTitle, etContent;
    private Toolbar mToolbar;

    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;

    private Menu mainMenu;
    private String noteId = "no";

    private boolean isExist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notes);

        try{

            noteId = getIntent().getStringExtra("noteId");



            if(noteId.equals("no")){

                mainMenu.getItem(0).setVisible(false);
                isExist = false;

            }else{
                isExist = true ;
            }

        }catch (Exception e){
            e.printStackTrace();
        }



        btnCreate = findViewById(R.id.new_note_button);
        etTitle = findViewById(R.id.new_note_title);
        etContent = findViewById(R.id.new_note_content);
        mToolbar = findViewById(R.id.new_note_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();

        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {

                        createNote(title, content);

                }else {
                    Toast.makeText(getApplicationContext(), "Fill up the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        putData();
    }

    private void putData(){

        if(isExist) {

            fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.hasChild("title") && snapshot.hasChild("content")) {


                        String title = snapshot.child("title").getValue().toString();
                        String content = snapshot.child("content").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

        private void createNote(String title , String content){

        if(fAuth.getCurrentUser() != null){

            if (isExist){

                //Update Exisiting Note
                Map updateMap = new HashMap();
                updateMap.put("title", etTitle.getText().toString().trim());
                updateMap.put("content", etContent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);

                fNotesDatabase.child(noteId).updateChildren(updateMap);

                Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();

            }else {

                //New Note creation


                final DatabaseReference newNoteRef = fNotesDatabase.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();


                                } else {

                                    Toast.makeText(getApplicationContext(), "ERROR:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                });

                mainThread.start();

            }




        }else{

            Toast.makeText(this, "User is not Signed in", Toast.LENGTH_SHORT).show();
        }

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.new_note_menu, menu);

         mainMenu = menu;

         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

    switch (item.getItemId()){
        case android.R.id.home:
            finish();
            break;
        case R.id.new_note_delete_btn:
            if(isExist){
                deleteNote();
            }else{
                Toast.makeText(this, "Nothing to Delete", Toast.LENGTH_SHORT).show();
            }
            break;
    }


         return true;
    }

    private void deleteNote(){

        fNotesDatabase.child(noteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewNotesActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    noteId = "no";
                    finish();
                }else{
                    Log.e("NewNoteAcitivity", task.getException().toString());
                    Toast.makeText(NewNotesActivity.this, "ERROR:"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
